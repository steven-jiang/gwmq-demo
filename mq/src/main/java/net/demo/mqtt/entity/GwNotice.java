package net.demo.mqtt.entity;

import java.util.HashSet;
import java.util.Set;


public   class GwNotice {
	
	
	private NoticeType type;
	
	private String gwName;
	
	private Set<String> thingIDs=new HashSet<>();
	
	public String getGwName() {
		return gwName;
	}

	public void setGwName(String gwName) {
		this.gwName = gwName;
	}
	
	public Set<String> getThingIDs() {
		return thingIDs;
	}
	
	public void setThingIDs(Set<String> thingIDs) {
		this.thingIDs = thingIDs;
	}
	
	public NoticeType getType() {
		return type;
	}
	
	public void setType(NoticeType type) {
		this.type = type;
	}
	
	public enum NoticeType{
		Offline,Online;
	}
	
}
