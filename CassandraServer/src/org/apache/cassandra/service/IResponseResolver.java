package org.apache.cassandra.service;

import java.io.IOException;

import org.apache.cassandra.net.Message;

public interface IResponseResolver<T> {

	public Iterable<Message> getMessages();

	/**
     * returns the data response without comparing with any digests
     */
	public T getData() throws IOException;

	public T resolve() throws DigestMismatchException;

}
