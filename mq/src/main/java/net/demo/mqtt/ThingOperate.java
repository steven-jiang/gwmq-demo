package net.demo.mqtt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class ThingOperate {
	
	
	private static final ThingOperate operate=new ThingOperate();
	
	public static ThingOperate getInstance() {
		return operate;
	}
	
	private ScheduledExecutorService schedule = Executors.newScheduledThreadPool(100);
	
	
	private Map<String,List<BiConsumer<String,String>>> listerenMap=new ConcurrentHashMap<>();
	
	
	public void sendCommand(String thingID, String cmd){
		
		System.out.println(" operate:send command "+cmd+" to "+thingID);
		
	}
	

	
	public void registListeren(String thingID,BiConsumer<String,String> onChange){
		
		final String th=thingID;
		
		listerenMap.computeIfAbsent(thingID,(k)->{
			
			int period= (int) (Math.random()*10+5);
			
			schedule.scheduleAtFixedRate(() -> {
				int random= (int) (Math.random()*1000);
				
				List<BiConsumer<String,String>> funList=listerenMap.get(th);
				
				funList.forEach(fun->fun.accept(th,"status"+random));
				
			},1,period, TimeUnit.SECONDS);
			
			List<BiConsumer<String,String>> list=new ArrayList<>();
			
			return list;
			
		}).add(onChange);
		
	}
	
	
	public void removeListeren(String thingID) {
		
		listerenMap.remove(thingID);
	}
}
