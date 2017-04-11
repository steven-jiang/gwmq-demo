package net.demo.mqtt.entity;

public class SubscriptionReq {
	
	
	private String thingID;
	
	private MsgType type;
	
	private String from;
	
	public String getFrom() {
		return from;
	}
	
	public void setFrom(String from) {
		this.from = from;
	}
	
	public MsgType getType() {
		return type;
	}
	
	public void setType(MsgType type) {
		this.type = type;
	}
	

	
	public String getThingID() {
		return thingID;
	}
	
	public void setThingID(String thingID) {
		this.thingID = thingID;
	}

	

}
