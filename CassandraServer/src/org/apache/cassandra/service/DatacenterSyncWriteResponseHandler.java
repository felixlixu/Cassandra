package org.apache.cassandra.service;

import java.net.InetAddress;
import java.util.Collection;

import org.apache.cassandra.thrift.ConsistencyLevel;

public class DatacenterSyncWriteResponseHandler extends WriteResponseHandler {

	protected DatacenterSyncWriteResponseHandler(InetAddress endpoint) {
		super(endpoint);
		// TODO Auto-generated constructor stub
	}

	public static IWriteResponseHandler create(
			Collection<InetAddress> writeEndpoints,
			ConsistencyLevel consistency_level, String table) {
		// TODO Auto-generated method stub
		return null;
	}

}
