package org.apache.cassandra.service;

import java.io.IOException;

import org.apache.cassandra.db.ReadCommand;
import org.apache.cassandra.net.Message;
import org.apache.cassandra.net.MessageProducer;

public class CachingMessageProducer implements MessageProducer {

	private final MessageProducer prod;

	public CachingMessageProducer(MessageProducer prod) {
		this.prod=prod;
	}

	@Override
	public Message getMessage(Integer version) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
