package org.apache.cassandra.db;

public enum ColumnFamilyType {
	Standard,
	Super;
	
	public static ColumnFamilyType create(String name){
		try{
			return name==null?ColumnFamilyType.Standard:ColumnFamilyType.valueOf(name);
		}catch(IllegalArgumentException e){
			return null;
		}
	}
}
