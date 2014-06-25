package org.apache.cassandra.service;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collection;
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

	public static IWriteResponseHandler create(
			Collection<InetAddress> writeEndpoints,
			ConsistencyLevel consistency_level, String table) {
		// TODO Auto-generated method stub
		return null;
	}

}
