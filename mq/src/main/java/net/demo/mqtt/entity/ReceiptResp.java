package net.demo.mqtt.entity;

public class ReceiptResp {
	
	private String thingID;
	
	private MsgType type;
	
	private String from;
	
	public String getThingID() {
		return thingID;
	}
	
	public void setThingID(String thingID) {
		this.thingID = thingID;
	}
	
	public MsgType getType() {
		return type;
	}
	
	public void setType(MsgType type) {
		this.type = type;
	}
	
	public String getFrom() {
		return from;
	}
	
	public void setFrom(String from) {
		this.from = from;
	}
}
