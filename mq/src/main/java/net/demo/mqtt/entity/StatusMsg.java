package net.demo.mqtt.entity;

public class StatusMsg extends Message{
	
	private String status;

	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public MsgType getType() {
		return MsgType.Status;
	}
}
