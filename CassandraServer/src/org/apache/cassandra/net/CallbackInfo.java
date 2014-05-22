package org.apache.cassandra.net;

import java.net.InetAddress;

public class CallbackInfo {

	protected final InetAddress target;
	protected final IMessageCallback callback;
	protected final Message message;
	
	public CallbackInfo(InetAddress to, IMessageCallback cb, Message message2) {
		this.target=to;
		this.callback=cb;
		this.message=message2;
	}

	public CallbackInfo(InetAddress to, IMessageCallback cb) {
		this.target=to;
		this.callback=cb;
		this.message=null;
	}

}
