package net.demo.mqtt.common;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BusinessException extends RuntimeException {


	private Map<String,String> paramMap=new HashMap<>();


	private String errorCode;

	private int statusCode=500;


	public BusinessException(){

	}
	
	
	public BusinessException(Exception e){
		super(e);
		setErrorCode(e.getClass().getName());
		setStatusCode(500);
	}

	protected BusinessException(String...  params){

		if(params.length<2){
			return;
		}
		for(int i=0;i<params.length;i+=2){

			addParam(params[i],params[i+1]);
		}
	}

	public String getErrorCode() {

		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public void addParam(String key,String value){
		paramMap.put(key,value);
	}

	@JsonIgnore
	public Map<String,String> getParamMap(){
		return paramMap;
	}


	@JsonIgnore
	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}


	@Override
	@JsonIgnore
	public StackTraceElement[] getStackTrace() {
		return super.getStackTrace();

	}
}
