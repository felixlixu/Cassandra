package org.apache.cassandra.net;

public interface IAsyncCallback extends IMessageCallback {

	public void response(Message msg);
}
