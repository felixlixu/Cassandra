package org.apache.cassandra.utils;

public abstract class Filter {

	int hashCount;
	
	int getHashCount(){
		return hashCount;
	}
}
