package net.demo.mqtt;

import java.util.function.Consumer;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;

public class MqttConnectPool {

	private Logger log = LoggerFactory.getLogger(MqttConnectPool.class);
	
	MqttClient client;

	
	
	
	public  MqttConnectPool(String userName,String pwd,String url)  {
		
		
		MqttConnectOptions connOpt = new MqttConnectOptions();
		
		
//		connOpt.setUserName(userName);
//		connOpt.setPassword(pwd.toCharArray());
		connOpt.setCleanSession(false);
		connOpt.setAutomaticReconnect(true);
		
		connOpt.setKeepAliveInterval(30);

		try {
			
			client = new MqttClient(url, MqttClient.generateClientId());
			client.connect(connOpt);
		}catch(Exception e){
			throw new IllegalArgumentException(e);
		}
		
	}
	
	public void close()  {
		
		try {
			client.close();
		}catch(Exception e){
			throw new IllegalArgumentException();
		}
	}
	
	public void addListener(String topic,Consumer<String>  consumer)  {
		
		try {
			client.subscribe(topic, 1, (topic1, message) -> consumer.accept(new String(message.getPayload(), Charsets.UTF_8)));
		}catch(Exception e){
			throw new IllegalArgumentException();
		}
	}
	
	
	public  void sendMsg(String topic,String msg)  {
		
		MqttMessage mqttMsg=new MqttMessage();
		mqttMsg.setPayload(msg.getBytes(Charsets.UTF_8));
		mqttMsg.setQos(1);
		
		try {
			client.publish(topic, mqttMsg);
		}catch(Exception e){
			throw new IllegalArgumentException();
		}
	}


}
