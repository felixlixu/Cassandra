package org.apache.cassandra.scheduler;

import java.util.concurrent.TimeoutException;


/**
 * Implements of IRequestScheduler must provide a constructor taking a 
 * RequestSchedulerOptions object.
 * */
public interface IRequestScheduler {

	// Queue incoming request threads
	public void queue(Thread t,String id,long timeoutMS) throws TimeoutException;

	//
	public void release();
}
