package org.apache.cassandra.scheduler;

import java.util.concurrent.TimeoutException;


public interface IRequestScheduler {

	public void queue(Thread t,String id,long timeoutMS) throws TimeoutException;

	public void release();
}
