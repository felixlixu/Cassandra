package org.apache.cassandra.locator;

import java.net.InetAddress;
import java.util.List;

/**
 *  This interface helps determine location of node in the data center relative to another node.
 *  Give a node A and another node B it can tell if A and B are on the same rack or in the same 
 *  data center.
 **/
public interface IEndpointSnitch {

	/**
	 * This method will sort the list by proximity to the given  address.
	 **/
	void sortByProximity(InetAddress broadcastAddress,
			List<InetAddress> endpoints);

	int compareEndpoints(InetAddress target,InetAddress a1,InetAddress a2);

	/** Return a String representing the datacenter this endpoint belongs to.*/
	String getDatacenter(InetAddress endpoint);
}
