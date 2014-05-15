package org.apache.cassandra.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.cassandra.locator.AbstractReplicationStrategy;
import org.apache.cassandra.locator.NetworkTopologyStrategy;

// KeySpace MetaData.
public class KSMetaData {

	public final String name;
	private final Map<String,CFMetaData> cfMetaData;
	public final Class<? extends AbstractReplicationStrategy> strategyClass;
	public final Map<String,String> strategyOptions;
	
	private KSMetaData(String name,Class<? extends AbstractReplicationStrategy> strategyClass,Map<String,String> strategyOptions,boolean durableWrites,Iterable<CFMetaData> cfDefs){
		this.name=name;
		this.strategyClass=strategyClass==null?NetworkTopologyStrategy.class:strategyClass;
		this.strategyOptions=strategyOptions;
		
		Map<String,CFMetaData> cfmap=new HashMap<String,CFMetaData>();
		this.cfMetaData=Collections.unmodifiableMap(cfmap);
	}
	
	public Map<String, CFMetaData> cfMetaData() {
		return cfMetaData;
	}

}
