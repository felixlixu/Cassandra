package org.apache.cassandra.service;

import java.util.concurrent.TimeoutException;

import org.apache.cassandra.net.IAsyncCallback;
import org.apache.cassandra.thrift.UnavailableException;

public interface IWriteResponseHandler extends IAsyncCallback {

	void get() throws TimeoutException;

	void assureSufficientLiveNodes() throws UnavailableException;

}
