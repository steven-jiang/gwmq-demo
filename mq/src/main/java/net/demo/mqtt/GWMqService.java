package net.demo.mqtt;

import static net.demo.mqtt.GwInstance.NOTICE_TOPIC;
import static net.demo.mqtt.GwInstance.RECEIPT_TOPIC;
import static net.demo.mqtt.GwInstance.SUBSCRIPTION_TOPIC;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import net.demo.mqtt.common.SafeObjectMapper;
import net.demo.mqtt.entity.CommandMsg;
import net.demo.mqtt.entity.GwNotice;
import net.demo.mqtt.entity.MsgType;
import net.demo.mqtt.entity.ReceiptResp;
import net.demo.mqtt.entity.StatusMsg;
import net.demo.mqtt.entity.SubscriptionReq;

public class GWMqService {
	
	private Set<String> subThingStatusSet=new ConcurrentSkipListSet<>();
	
	private Set<String> subThingCmdSet=new ConcurrentSkipListSet<>();
	
	private final ThingOperate operate;
	
	private final Set<String> localThingSet;
	
	
	private final MqttConnectPool mqtt;
	
	private final String gwName;
	
	public GWMqService(Set<String> localThingSet,String gwName, MqttConnectPool mqtt){
		
		this.localThingSet=localThingSet;
		
		this.gwName=gwName;
		
		this.mqtt=mqtt;
		
		operate=ThingOperate.getInstance();
		
		registSubscription();
		
		sendGwNotice(GwNotice.NoticeType.Online);


	}
	
	private void sendGwNotice(GwNotice.NoticeType type) {
		GwNotice notice=new GwNotice();
		notice.setThingIDs(localThingSet);
		notice.setType(type);
		notice.setGwName(gwName);
		
		mqtt.sendMsg(NOTICE_TOPIC, SafeObjectMapper.getInstance().writeValueAsString(notice));
	}
	
	
	public void show() {
		
		System.out.println("push thing status:"+subThingStatusSet);
		
		
		System.out.println("take thing command:"+subThingCmdSet);
	}
	
	
	public void stop(){
		
		sendGwNotice(GwNotice.NoticeType.Offline);
		
		localThingSet.forEach(th-> operate.removeListeren(th));
		
	}

	
	private void receipt(String thingID, MsgType type){
		
		
		ReceiptResp heartbeat=new ReceiptResp();
		heartbeat.setThingID(thingID);
		heartbeat.setType(type);
		heartbeat.setFrom(gwName);
		
		
		mqtt.sendMsg(RECEIPT_TOPIC,SafeObjectMapper.getInstance().writeValueAsString(heartbeat));
		
	}
	
	private void registSubscription(){
		
		mqtt.addListener(SUBSCRIPTION_TOPIC, s -> {
			
			SubscriptionReq req = SafeObjectMapper.getInstance().readValue(s, SubscriptionReq.class);
			if (req.getFrom().equals(gwName)) {
				return;
			}
			String thingID=req.getThingID();
			
			if(req.getType()== MsgType.Status){
				registStatusSubscription(thingID);
			}else{
				registCommandSubscription(thingID);
			}
			
			receipt(thingID,req.getType());
		});
	}
	
	private void registStatusSubscription(String thingID) {
		
		if (localThingSet.contains(thingID) && !subThingStatusSet.contains(thingID)) {
				
			subThingStatusSet.add(thingID);
			
			operate.registListeren(thingID, (id, s) -> {
				
				StatusMsg msg=new StatusMsg();
				msg.setStatus(s);
				
				mqtt.sendMsg("/things/"+thingID+"/status", SafeObjectMapper.getInstance().writeValueAsString(msg));
				
			});
				
		}
		
	}
	
	private void registCommandSubscription(String thingID) {

			if(localThingSet.contains(thingID)&&!subThingCmdSet.contains(thingID)){
				
				subThingStatusSet.add(thingID);
				
				mqtt.addListener("/things/" + thingID + "/command", cmd -> {
						
					CommandMsg msg=SafeObjectMapper.getInstance().readValue(cmd,CommandMsg.class);
						
					if(localThingSet.contains(thingID)) {
							operate.sendCommand(thingID, msg.getCommand());
					}
				});
				
			}
		
	}
	
}
