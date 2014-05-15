package org.apache.cassandra.net;

import java.net.InetAddress;

public class Message {

	final Header header_;
	
	public Message(Header header,byte[] body,int version){
		assert header !=null;
		assert body!=null;
		header_=header;
	}
	
	public InetAddress getFrom() {
		return header_.getFrom();
	}

}