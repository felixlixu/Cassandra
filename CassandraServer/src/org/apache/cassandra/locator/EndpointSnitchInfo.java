package org.apache.cassandra.locator;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public class EndpointSnitchInfo implements EndpointSnitchInfoMBean {

	public static void create(){
		MBeanServer mbs=ManagementFactory.getPlatformMBeanServer();
		try{
			mbs.registerMBean(new EndpointSnitchInfo(), new ObjectName("org.apache.cassandra.db:type=EndpointSnitchInfo"));
		}
		catch(Exception e){
			throw new RuntimeException(e);
		}
	}
}
