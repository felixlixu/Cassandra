package org.apache.cassandra.service;

import java.io.IOException;

import org.apache.cassandra.net.Message;

public interface IResponseResolver<T> {

	public Iterable<Message> getMessages();

	public T getData() throws IOException;

	public T resolve();

}