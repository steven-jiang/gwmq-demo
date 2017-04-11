package net.demo.mqtt;

import static net.demo.mqtt.GwInstance.NOTICE_TOPIC;
import static net.demo.mqtt.GwInstance.RECEIPT_TOPIC;
import static net.demo.mqtt.GwInstance.SUBSCRIPTION_TOPIC;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import net.demo.mqtt.common.SafeObjectMapper;
import net.demo.mqtt.entity.CommandMsg;
import net.demo.mqtt.entity.MsgType;
import net.demo.mqtt.entity.GwNotice;
import net.demo.mqtt.entity.ReceiptResp;
import net.demo.mqtt.entity.StatusMsg;
import net.demo.mqtt.entity.SubscriptionReq;

public class GWMqClient {
	
	
	private Map<String,SubInfo> outerThingCmd=new ConcurrentHashMap<>();
	
	private Map<String,SubInfo> outerThingStatus=new ConcurrentHashMap<>();
	
	private final ExecutorService execute= Executors.newCachedThreadPool();
	
	private final ThingOperate operate;
	
	private final Set<String> localThingSet;
	
	private final MqttConnectPool mqtt;
	
	private final String gwName;
	
	private Map<String,List<BiConsumer<String,String>>>  listerenMap=new ConcurrentHashMap<>();
	
	private static class SubInfo{
		
//		private String gwName=null;
		
		private Status status=Status.init;
		
	}
	
	private enum Status{
		enable,offline,init;
	}
	
	
	private Map<String,SubInfo> getOuterMap(MsgType type){
		if(type==MsgType.Command){
			return outerThingCmd;
		}else{
			return outerThingStatus;
		}
	}
	
	
	public void show() {
	
		System.out.println("sub outer thing status:"+outerThingStatus);
		
		System.out.println("sub outer thing cmd:"+outerThingCmd);
		
	}
	
	
	public GWMqClient(Set<String> localThingSet,String gwName, MqttConnectPool mqtt){
		
		this.localThingSet=localThingSet;
		
		this.mqtt=mqtt;
		
		this.gwName=gwName;
		
		operate=ThingOperate.getInstance();
		
		for(String thingID:localThingSet) {
			
			operate.registListeren(thingID, (id, s) -> onThingStatusChange(id,s));
		}
		
		registReceiptListener();
		
		registNoticeListener();
	}
	
	public void stop(){
		execute.shutdown();
	}
	
	private void registReceiptListener(){
		
		mqtt.addListener(RECEIPT_TOPIC, s -> {
			
			ReceiptResp req = SafeObjectMapper.getInstance().readValue(s, ReceiptResp.class);
			
			getOuterMap(req.getType()).computeIfPresent(req.getThingID(),(k,v)->{
					v.status = Status.enable;
					return v;
			});
			
		});
	}
	
	private void registNoticeListener(){
		
		mqtt.addListener(NOTICE_TOPIC, s -> {
			
			GwNotice req = SafeObjectMapper.getInstance().readValue(s, GwNotice.class);
			
			if(req.getGwName().equals(gwName)){
				return;
			}
			if(req.getType() == GwNotice.NoticeType.Offline ){
				onGatewayOffline(req.getThingIDs());
			}else{
				onGatewayOnline(req.getThingIDs());
			}
			
		});
	}
	
	
	
	
	private void registThingStatusChange(String thingID){
		
		mqtt.addListener("/things/"+thingID+"/status", s -> {
			
			StatusMsg req = SafeObjectMapper.getInstance().readValue(s, StatusMsg.class);

			onThingStatusChange(thingID,req.getStatus());
		});
		
	}
	
	
	private void onThingStatusChange(String thingID,String status){
		
		List<BiConsumer<String,String>> ls= listerenMap.get(thingID);
		if(ls!=null) {
			ls.forEach((fun) -> {
				fun.accept(thingID, status);
			});
		}
	}
	
	public void addThingStatusListener(String thingID,BiConsumer<String,String> onChange){
		
		listerenMap.computeIfAbsent(thingID,(k)->{
			List<BiConsumer<String,String>> list=  new CopyOnWriteArrayList<>();
			if(outerThingStatus.containsKey(thingID)){
				if(outerThingStatus.get(thingID).status==Status.enable) {
					registThingStatusChange(thingID);
				}else{
					throw new IllegalArgumentException("thing "+thingID+" invalid :"+outerThingStatus.get(thingID).status);
				}
			}else if(!localThingSet.contains(thingID)){
				subscriptOuterThingID(thingID,MsgType.Status);
				registThingStatusChange(thingID);
			}
			return list;
		}).add(onChange);
		
	}
	
	
	public void sendThingCommand(String thingID,String command){
		
		if(outerThingCmd.containsKey(thingID)){
			if(outerThingCmd.get(thingID).status==Status.enable) {
				sendOuterCmd(thingID, command);
			}else{
				throw new IllegalArgumentException("thing "+thingID+" invalid :"+outerThingCmd.get(thingID).status);
			}
			
		}else if(localThingSet.contains(thingID)){
			
			operate.sendCommand(thingID,command);
		}else {
			subscriptOuterThingID(thingID,MsgType.Command);
			sendOuterCmd(thingID, command);
		}
		
	}
	
	private void sendOuterCmd(String thingID, String command) {
		CommandMsg msg=new CommandMsg();
		msg.setCommand(command);
		
		mqtt.sendMsg("/things/"+thingID+"/command", SafeObjectMapper.getInstance().writeValueAsString(msg));
	}
	
	public void onGatewayOnline(Set<String> thingIDs){
		outerThingCmd.forEach((k,sub)->{
			if(thingIDs.contains(k)) {
				subscriptOuterThingID(k,MsgType.Command);
			}
		});
		
		
		outerThingStatus.forEach((k,sub)->{
			if(thingIDs.contains(k)) {
				subscriptOuterThingID(k,MsgType.Status);
			}
		});
		
	}
	
	public void onGatewayOffline(Set<String> thingIDs){
		
		
		outerThingCmd.forEach((k,sub)->{
			if(thingIDs.contains(k)) {
				sub.status = Status.offline;
			}
		});
		
		
		outerThingStatus.forEach((k,sub)->{
			if(thingIDs.contains(k)) {
				sub.status = Status.offline;
			}
		});
		
	}
	
	private  void   subscriptOuterThingID(String thingID, MsgType type){
		
		if(localThingSet.contains(thingID)){
			return;
		}
		
		getOuterMap(type).put(thingID,new SubInfo());
		
		SubscriptionReq req=new SubscriptionReq();
		req.setType(type);
		req.setThingID(thingID);
		req.setFrom(gwName);
		
		String json=SafeObjectMapper.getInstance().writeValueAsString(req);
		
		mqtt.sendMsg(SUBSCRIPTION_TOPIC,json);
		
		
		Future<?>  future=execute.submit(() -> {
			
			while(true){
				
				Status s=getOuterMap(type).get(thingID).status;
				
				if(s==Status.enable){
					return;
				}
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					break;
				}
			}
			throw new IllegalArgumentException();
			
		});
		
		
		try {
			 
			 future.get(10, TimeUnit.SECONDS);
			
		} catch (Exception e) {
		
			throw new IllegalArgumentException("outer thing not found "+thingID);
		}
		
	};
	

}
