package org.apache.cassandra.service;

import java.util.List;

public interface StorageServiceMBean {

	public List<String> getLiveNodes();
	
	public List<String> getUnreachableNodes();
	
	public List<String> getJoinintNodes();
	
	public List<String> getLeavingNodes();
	
	public List<String> getMovingNodes();
	
	public String getToken();
	
	public String getReleaseVersion();
	
	public String[] getAllDataFileLocations();
	
	public String[] getAllDataFileLoacationsForTable(String table);
}
