package org.apache.cassandra.gms;

import java.net.InetAddress;
import java.util.concurrent.ConcurrentMap;

import org.apache.cassandra.net.MessagingService;
import org.cliffc.high_scale_lib.NonBlockingHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gossiper implements IFailureDetectionEventListener,GossiperMBean {
	
	private static Logger logger = LoggerFactory.getLogger(Gossiper.class);
	

	public static Gossiper instance=new Gossiper();
	private final ConcurrentMap<InetAddress,Integer> versions=new NonBlockingHashMap<InetAddress,Integer>();
	
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