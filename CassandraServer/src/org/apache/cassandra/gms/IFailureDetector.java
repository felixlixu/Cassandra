package org.apache.cassandra.gms;

import java.net.InetAddress;

public interface IFailureDetector {

	boolean isAlive(InetAddress endpoint);

}
