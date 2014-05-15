package org.apache.cassandra.service;

import org.apache.cassandra.db.ReadCommand;
import org.apache.cassandra.net.MessageProducer;

public class CachingMessageProducer implements MessageProducer {

	private final MessageProducer prod;

	public CachingMessageProducer(MessageProducer prod) {
		this.prod=prod;
	}

}
