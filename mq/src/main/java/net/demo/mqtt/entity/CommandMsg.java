package net.demo.mqtt.entity;

public class CommandMsg extends Message{
	
	private String command;
	
	public String getCommand() {
		return command;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	
	@Override
	public MsgType getType() {
		return MsgType.Command;
	}
}
