package net.demo.mqtt.common;

import com.fasterxml.jackson.core.JsonProcessingException;

public class JsonSerialException extends BusinessException {
	
	public JsonSerialException(JsonProcessingException e) {
		
		super(e);
		
		super.setStatusCode(400);
		super.setErrorCode("JSONFormatError");
		super.addParam("reason", e.getMessage());
	}
	
	
}
