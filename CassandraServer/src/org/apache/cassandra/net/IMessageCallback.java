package org.apache.cassandra.net;

public interface IMessageCallback {

	/**
	 * @return ture if this callback is on the read path and its latency  should be 
	 * given as input to the dynamic  snitch.
	 **/
	public boolean isLatencyForSnitch();
}
