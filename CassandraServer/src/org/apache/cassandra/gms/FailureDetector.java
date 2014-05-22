package org.apache.cassandra.gms;

import java.net.InetAddress;

public class FailureDetector implements IFailureDetector, FailureDetectorMBean{

	public static final IFailureDetector instance = new FailureDetector();

	@Override
	public boolean isAlive(InetAddress endpoint) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void registerFailureDetectionEventListener(Gossiper gossiper) {
		// TODO Auto-generated method stub
		
	}
}
