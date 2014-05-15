package org.apache.cassandra.locator;

import java.net.InetAddress;

public class DynamicEndpointSnitch  extends AbstractEndpointSnitch implements ILatencySubscriber, DynamicEndpointSnitchMBean {

	public DynamicEndpointSnitch(IEndpointSnitch snitch) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compareEndpoints(InetAddress target, InetAddress a1,
			InetAddress a2) {
		// TODO Auto-generated method stub
		return 0;
	}

}
