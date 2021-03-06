package org.apache.cassandra.net;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.cassandra.config.DatabaseDescriptor;

public class OutboundTcpConnection extends Thread {
	
	private volatile BlockingQueue<Entry> backlog=new LinkedBlockingQueue<Entry>();
	private volatile BlockingQueue<Entry> active=new LinkedBlockingQueue<Entry>();
	private final AtomicLong dropped=new AtomicLong();
	private final OutboundTcpConnectionPool poolReference;
	
	public OutboundTcpConnection(
			OutboundTcpConnectionPool pool) {
		super("WRITE-"+pool.endPoint());
		this.poolReference=pool;
	}

	public void enqueue(Message message, String id) {
		expireMessages();
		try{
			backlog.put(new Entry(message,id,System.currentTimeMillis()));
		}catch(InterruptedException e){
			throw new AssertionError(e);
		}
	}
	
	private void expireMessages(){
		while(true){
			Entry entry=backlog.peek();
			if(entry==null||entry.timestamp>=System.currentTimeMillis()-DatabaseDescriptor.getRpcTimeout())
				break;
			Entry entry2=backlog.poll();
			if(entry2!=entry){
				if(entry2!=null)
					active.add(entry2);
				break;
			}
			dropped.incrementAndGet();
		}
	}
	
	private static class Entry{
		final Message message;
		final String id;
		final long timestamp;
		
		Entry(Message message,String id,long timestamp){
			this.message=message;
			this.id=id;
			this.timestamp=timestamp;
		}
	}

}
