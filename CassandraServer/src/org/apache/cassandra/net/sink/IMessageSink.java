package org.apache.cassandra.net.sink;

import java.net.InetAddress;

import org.apache.cassandra.net.Message;

public interface IMessageSink {

	Message handleMessage(Message message, String id, InetAddress to);

}
