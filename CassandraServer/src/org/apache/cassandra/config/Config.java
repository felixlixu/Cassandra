package org.apache.cassandra.config;

public class Config {

	public String commitlog_directory;

	public Long rpc_timeout_in_ms=new Long(2000);

	public String request_scheduler;

	public RequestSchedulerOptions request_scheduler_options;

	public String endpoint_snitch;

	public boolean dynamic_snitch=true;

	public String listen_address;

	public String broadcast_address;

	public String[] data_file_directories;

	public String saved_caches_directory;

	public int row_cache_save_period;

	public int key_cache_save_period;

	public int concurrent_compactors;

	public double reduce_cache_capacity_to;

	public int concurrent_reads;
	
	public static enum RequestSchedulerId{
		keyspace
	}
}
