package net.demo.mqtt.entity;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = StatusMsg.class, name = "Status"),
		@JsonSubTypes.Type(value = CommandMsg.class, name = "Command")
})
public abstract class Message {

	
	public abstract  MsgType getType();
	

}
