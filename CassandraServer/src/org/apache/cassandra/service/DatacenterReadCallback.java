package org.apache.cassandra.service;

import java.net.InetAddress;
import java.util.List;

import org.apache.cassandra.thrift.ConsistencyLevel;

public class DatacenterReadCallback<T> extends ReadCallback<T> {

	public DatacenterReadCallback(IResponseResolver<T> resolver,
			ConsistencyLevel consistency_level, IReadCommand command,
			List<InetAddress> endpoints) {
		super(resolver, consistency_level, command, endpoints);
	}

}
