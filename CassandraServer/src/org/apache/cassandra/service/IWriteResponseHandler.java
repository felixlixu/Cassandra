package org.apache.cassandra.service;

import java.util.concurrent.TimeoutException;

public interface IWriteResponseHandler {

	void get() throws TimeoutException;

}
