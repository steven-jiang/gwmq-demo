package net.demo.mqtt.common;

import java.io.IOException;

public class JsonDeserialException extends BusinessException {
	
	public JsonDeserialException(IOException e) {
		
		super(e);
		
		super.setStatusCode(400);
		super.setErrorCode("ObjectConvertFail");
		super.addParam("reason", e.getMessage());
	}
}
