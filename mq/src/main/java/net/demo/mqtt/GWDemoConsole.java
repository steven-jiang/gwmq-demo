package net.demo.mqtt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class GWDemoConsole {
	
	
	
	public static void main(String[] argc){
		
		GWDemoConsole instance=new GWDemoConsole();
		
		System.out.println(">>> input command\n");
		
		
		instance.init();
		
		while (true) {
			
			
			try {
				
				String input = readLine();
				
				if (StringUtils.isEmpty(input)) {
					continue;
				}
				
				String[] arrays = StringUtils.split(input, " ");
				
				String cmd = arrays[0];
				
				String[] params=new String[arrays.length-1];
				
				System.arraycopy(arrays,1,params,0,params.length);
				
				instance.doMsgCycle(cmd, params);
				
			} catch (IOException e) {
				e.printStackTrace();
				break;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
		}
		
	}
	
	private Map<String,GwInstance> serviceMap=new HashMap<>();
	
	public void init(){
		String[] a={"1","2","3"};
		String[] b={"4","5","6"};
		String[] c={"7","8","9"};
		
		GwInstance inst1=new GwInstance("one",a);
		GwInstance inst2=new GwInstance("two",b);
		GwInstance inst3=new GwInstance("three",c);
		
		serviceMap.put("one",inst1);
		serviceMap.put("two",inst2);
		serviceMap.put("three",inst3);
		
	}
	
	
	public void doMsgCycle(String cmd,String[] arrays)  {
		
		
		
		switch(cmd) {
			
			case "addGW": {
				String gwName = arrays[0];
				
				String[] ths=new String[arrays.length-1];
				System.arraycopy(arrays,1,ths,0,ths.length);
				
				GwInstance service = new GwInstance(gwName,ths);
				
				serviceMap.put(gwName, service);
				break;
			}
			case "removeGW": {
				String gwName = arrays[0];
				
				GwInstance service=serviceMap.remove(gwName);
				
				service.shutdown();
				break;
			}
			case "addMonitor": {
				String gwName = arrays[0];
				
				String thingID=arrays[1];
				
				serviceMap.get(gwName).addMonitor(thingID);
				break;
			}
			case "sendCommand":{
				String gwName = arrays[0];
				
				String thingID=arrays[1];
				
				String command=arrays[2];
				
				serviceMap.get(gwName).sendCommand(thingID,command);
				
				break;
			}
			case "show":{
				String gwName = arrays[0];
				
				serviceMap.get(gwName).show();
				
				break;
			}
			
			case "exit":
				System.exit(0);
		}
	}
	
	private static String readLine() throws IOException {
		
		char ch = (char) System.in.read();
		StringBuilder sb = new StringBuilder();
		while (ch != '\n') {
			sb.append(ch);
			ch = (char) System.in.read();
		}
		return sb.toString().trim();
	}
}
