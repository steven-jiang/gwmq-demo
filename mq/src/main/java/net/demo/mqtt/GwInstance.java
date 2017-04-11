package net.demo.mqtt;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GwInstance {
	
	private GWMqClient  client;
	
	private GWMqService service;
	
	private final MqttConnectPool mqtt;
	
	private final Set<String> thingSet;
	
	private final String gwName;
	
	public static final String NOTICE_TOPIC="/global/thingnotice";
	
	public static final String RECEIPT_TOPIC="/global/receipt";
	
	public static final String SUBSCRIPTION_TOPIC="/global/subscription";
	
	public GwInstance(String gwName,String[]  thIDs){
		
		this.gwName=gwName;
		
		mqtt=new MqttConnectPool(null,null,"tcp://localhost:1883");
		
		thingSet=new HashSet<>();
		thingSet.addAll(Arrays.asList(thIDs));
		
		service=new GWMqService(thingSet,gwName,mqtt);
		
		client=new GWMqClient(thingSet,gwName,mqtt);
		
	}
	
	public void shutdown(){
		
		client.stop();
		
		service.stop();
		
		mqtt.close();
		
	}
	
	public void addMonitor(String thingID) {
		
		client.addThingStatusListener(thingID,(k,v)->{
			System.out.println("monitor:the thing "+k+"'s status change to "+v+ " :from gw "+gwName);
		});
	}
	
	public void sendCommand(String thingID,String command) {
		
		
		client.sendThingCommand(thingID,command);
		
		System.out.println("send command:send command "+command +" to thing "+thingID+" :from gw "+gwName);
		
	}
	
	public void show() {
		
		System.out.println("local thing list:"+thingSet);
		
		service.show();
		client.show();
	}
}
