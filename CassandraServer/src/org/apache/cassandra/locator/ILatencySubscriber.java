package org.apache.cassandra.locator;

import java.net.InetAddress;

public interface ILatencySubscriber {

	void receiveTiming(InetAddress address, double latency);

}
