package org.apache.cassandra.scheduler;

import java.util.concurrent.TimeoutException;

import org.apache.cassandra.config.RequestSchedulerOptions;

public class NoScheduler implements IRequestScheduler {

	@Override
	public void queue(Thread t, String id, long timeoutMS)
			throws TimeoutException {
	}

	@Override
	public void release() {}
	
	public NoScheduler(RequestSchedulerOptions options){}
	
	public NoScheduler(){}

}
