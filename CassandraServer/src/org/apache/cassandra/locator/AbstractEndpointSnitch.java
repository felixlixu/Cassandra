package org.apache.cassandra.locator;

import java.net.InetAddress;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractEndpointSnitch implements IEndpointSnitch {

	//target is boardcasting
	public abstract int compareEndpoints(InetAddress target, InetAddress a1,
			InetAddress a2);

	
	@Override
	public void sortByProximity(final InetAddress address,
			List<InetAddress> addresses) {
		Collections.sort(addresses, new Comparator<InetAddress>() {
			public int compare(InetAddress a1, InetAddress a2) {
				return compareEndpoints(address, a1, a2);
			}
		});
	}
}
