package org.apache.cassandra.service;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.cassandra.thrift.ConsistencyLevel;

public class WriteResponseHandler extends AbstractWriteResponseHandler {
	
	private AtomicInteger response;

	protected WriteResponseHandler(InetAddress endpoint){
		super(Arrays.asList(endpoint),ConsistencyLevel.ALL);
		response=new AtomicInteger(1);
	}

	public static IWriteResponseHandler create(InetAddress endpoint) {
		return new WriteResponseHandler(endpoint);
	}

}
