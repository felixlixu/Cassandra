package org.apache.cassandra.net.sink;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.cassandra.net.Message;

public class SinkManager {

	private static List<IMessageSink> sinks=new ArrayList<IMessageSink>();
	
	public static Message processServerMessage(Message message, String id) {
		if(sinks.isEmpty()){
			return message;
		}
		
		for(IMessageSink ms:sinks){
			message=ms.handleMessage(message,id,null);
				if(message==null)
					return null;
		}
		return message;
	}

	public static Message processClientMessagE(Message message, String id,
			InetAddress to) {
		if(sinks.isEmpty())
			return message;
		for(IMessageSink ms:sinks){
			message=ms.handleMessage(message, id, to);
			if(message==null)
				return null;
		}
		return message;
	}

}
