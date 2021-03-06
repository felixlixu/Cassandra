package org.apache.cassandra.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DebuggableThreadPoolExecutor extends ThreadPoolExecutor {

	public DebuggableThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, NamedThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
		allowCoreThreadTimeOut(true);
	}

	public DebuggableThreadPoolExecutor(int size, long keepAliveTime,
			TimeUnit unit,
			LinkedBlockingQueue<Runnable> queue,
			NamedThreadFactory namedThreadFactory) {
		this(size,size,keepAliveTime,unit,queue,namedThreadFactory);
	}

	public static ExecutorService createWithPoolSize(String threadPoolName,
			int size) {
		return new DebuggableThreadPoolExecutor(size,Integer.MAX_VALUE,TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(),new NamedThreadFactory(threadPoolName) );
	}

}
