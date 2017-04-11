package net.demo.mqtt.common;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class SafeObjectMapper {
	
	
	private static final SafeObjectMapper simple = new SafeObjectMapper();
	private ObjectMapper mapper;
	
	private SafeObjectMapper() {
		ObjectMapper result = new ObjectMapper();
		result.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		result.configure(SerializationFeature.INDENT_OUTPUT, true);
		result.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		result.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		mapper = result;
	}
	
	private SafeObjectMapper(ObjectMapper mapper) {
		
		this.mapper = mapper;
	}
	
	public static final SafeObjectMapper getInstance() {
		
		return simple;
	}
	
	public static final SafeObjectMapper getInstance(ObjectMapper mapper) {
		return new SafeObjectMapper(mapper);
	}
	
	public void setSerializationInclusion(JsonInclude.Include serializationInclusion) {
		this.mapper.setSerializationInclusion(serializationInclusion);
	}
	
	public void configure(SerializationFeature feature, boolean b) {
		
		this.mapper.configure(feature, b);
	}
	
	public void configure(DeserializationFeature feature, boolean b) {
		
		this.mapper.configure(feature, b);
	}
	
	public String writeValueAsString(Object obj) {
		
		if (obj == null) {
			return "{}";
		}
		
		try {
			String json = mapper.writeValueAsString(obj);
			
			return json;
		} catch (JsonProcessingException e) {
			throw new JsonSerialException(e);
		}
		
	}
	
	
	public <T> T readValue(String jsonStr, Class<T> mapClass) {
		
		try {
			return mapper.readValue(jsonStr, mapClass);
		} catch (IOException e) {
			throw new JsonDeserialException(e);
		}
	}
}
