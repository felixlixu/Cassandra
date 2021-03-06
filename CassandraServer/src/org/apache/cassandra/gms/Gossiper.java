package org.apache.cassandra.gms;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentMap;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.cassandra.net.MessagingService;
import org.apache.cassandra.service.StorageService;
import org.cliffc.high_scale_lib.NonBlockingHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gossiper implements IFailureDetectionEventListener,GossiperMBean {
	
	private static Logger logger = LoggerFactory.getLogger(Gossiper.class);
	private static final String MBEAN_NAME = "org.apache.cassandra.net:type=Gossiper";
	
	public final static int QUARANTINE_DELAY=StorageService.RING_DELAY*2;
	public long FatClientTimeout;

	
	public static Gossiper instance=new Gossiper();
	private final ConcurrentMap<InetAddress,Integer> versions=new NonBlockingHashMap<InetAddress,Integer>();
	
	private Gossiper(){
		FatClientTimeout=(long)(QUARANTINE_DELAY/2);
		
		FailureDetector.instance.registerFailureDetectionEventListener(this);
		
		try{
			MBeanServer mbs=ManagementFactory.getPlatformMBeanServer();
			mbs.registerMBean(this, new ObjectName(MBEAN_NAME));
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	public Integer getVersion(InetAddress address) {
		Integer v=versions.get(address);
		if(v==null){
			logger.trace("Assuming current protocol version for {}", address);
            return MessagingService.version_;			
		}else{
			return v;
		}
	}
}
