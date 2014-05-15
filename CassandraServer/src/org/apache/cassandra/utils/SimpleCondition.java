package org.apache.cassandra.utils;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

public class SimpleCondition implements Condition {

	@Override
	public void await() throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean await(long arg0, TimeUnit arg1) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long awaitNanos(long arg0) throws InterruptedException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void awaitUninterruptibly() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean awaitUntil(Date arg0) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void signal() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void signalAll() {
		// TODO Auto-generated method stub
		
	}

}
