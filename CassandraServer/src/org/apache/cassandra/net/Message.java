package org.apache.cassandra.net;

import java.net.InetAddress;

import org.apache.cassandra.concurrent.Stage;
import org.apache.cassandra.service.StorageService;
import org.apache.cassandra.service.StorageService.Verb;

public class Message {

	final Header header_;
	private byte[] body_;
	private final transient int version;
	
	public Message(Header header,byte[] body,int version){
		assert header !=null;
		assert body!=null;
		
		header_=header;
		body_=body;
		this.version=version;
	}
	
	public Message(InetAddress from,StorageService.Verb verb,byte[] body,int version){
		this(new Header(from,verb),body,version);
	}

	public byte[] getHeader(String key){
		return this.header_.getDetail(key);
	}
	
	public Message withHeaderAdded(String key,byte[] value){
		return new Message(header_.withDetailsAdded(key, value),body_,version);
	}
	
	public Message withHeaderRemoved(String key){
		return new Message(header_.withDetailsRemoved(key),body_,version);
	}
	
	public InetAddress getFrom() {
		return header_.getFrom();
	}

	public StorageService.Verb getVerb() {
		return header_.getVerb();
	}

	public byte[] getMessageBody() {
		return body_;
	}

	public int getVersion() {
		return version;
	}
	
	public Stage getMessageType(){
		return StorageService.verbStages.get(getVerb());
	}
	
	public Message getReply(InetAddress from,byte[] body,int version){
		Header header=new Header(from,StorageService.Verb.REQUEST_RESPONSE);
		return new Message(header,body,version);
	}
	
    public String toString()
    {
        StringBuilder sbuf = new StringBuilder("");
        String separator = System.getProperty("line.separator");
        sbuf.append("FROM:" + getFrom())
        	.append(separator)
        	.append("TYPE:" + getMessageType())
        	.append(separator)
        	.append("VERB:" + getVerb())
        	.append(separator);
        return sbuf.toString();
    }

}
