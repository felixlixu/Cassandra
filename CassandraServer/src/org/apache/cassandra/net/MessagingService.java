package org.apache.cassandra.net;

import java.io.IOError;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.db.ReadVerbHandler;
import org.apache.cassandra.db.Row;
import org.apache.cassandra.gms.Gossiper;
import org.apache.cassandra.service.ReadCallback;
import org.apache.cassandra.service.RepairCallback;
import org.apache.cassandra.service.StorageService.Verb;

public class MessagingService implements MessagingServiceMBean {

	public static final int VERSION_11=4;
	public static final int version_=VERSION_11;
	
	private static final long DEFAULT_CALLBACK_TIMEOUT = DatabaseDescriptor.getRpcTimeout();
	
    private static class MSHandle
    {
        public static final MessagingService instance = new MessagingService();
    }
	
	public static MessagingService instance() {
		return MSHandle.instance;
	}

	public void registerVerbHandlers(Verb read, ReadVerbHandler readVerbHandler) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Send a message to a given endpoint.similar to sendRR
	 * */
	public String sendRR(MessageProducer producer, InetAddress to,
			IAsyncCallback cb) {
		try{
			return sendRR(producer.getMessage(Gossiper.instance.getVersion(to)),to,cb);
		}catch(IOException ex){
			throw new IOError(ex);
		}
	}

	public String sendRR(Message message, InetAddress to, IMessageCallback cb) {
		return sendRR(message, to, cb, DEFAULT_CALLBACK_TIMEOUT);
	}

	/**
	 * Send a message to a given endpoint. This method specifies a callback
	 * which is invoked with actual response.
	 * Also holds the message (only mutation messages) to determine if it needs
	 * to trigger a hint. 
	 **/
	public String sendRR(Message message, InetAddress to, IMessageCallback cb,
			long timeout) {
		String id=addCallbck(cb,message,to,timeout);
		sendOneWay(message,id,to);
		return id;
	}

	
	public void sendOneWay(Message message, String id, InetAddress to) {
		// TODO Auto-generated method stub
		
	}

	public String addCallbck(IMessageCallback cb, Message message,
			InetAddress to, long timeout) {
		String messageId=nextId();
		CallbackInfo previous;
		
		return null;
	}
	
	private static AtomicInteger idGen=new AtomicInteger();
	private static String nextId(){
		return Integer.toString(idGen.incrementAndGet());
	}

}
