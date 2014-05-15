package org.apache.cassandra.concurrent;

import java.util.EnumMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.cassandra.config.DatabaseDescriptor;

public class StageManager {

	private static EnumMap<Stage, ThreadPoolExecutor> stages = new EnumMap<Stage, ThreadPoolExecutor>(Stage.class);
	
	public static ThreadPoolExecutor getStage(Stage mutation) {
		return stages.get(mutation);
	}
	
	public static final long KEEPALIVE = 60; 
	
	static{
		stages.put(Stage.READ, multiThreadedConfigurableStage(Stage.READ,DatabaseDescriptor.getConcurrentReaders()));
	}
	
    private static ThreadPoolExecutor multiThreadedConfigurableStage(Stage stage, int numThreads)
    {
        return new JMXConfigurableThreadPoolExecutor(numThreads,
                                                     KEEPALIVE,
                                                     TimeUnit.SECONDS,
                                                     new LinkedBlockingQueue<Runnable>(),
                                                     new NamedThreadFactory(stage.getJmxName()),
                                                     stage.getJmxType());
    }

}
