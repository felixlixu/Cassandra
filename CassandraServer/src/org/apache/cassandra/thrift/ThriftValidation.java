package org.apache.cassandra.thrift;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import org.apache.cassandra.config.CFMetaData;
import org.apache.cassandra.config.Schema;
import org.apache.cassandra.db.ColumnFamilyType;
import org.apache.cassandra.db.IColumn;
import org.apache.cassandra.db.KeyspaceNotDefinedException;
import org.apache.cassandra.db.Table;
import org.apache.cassandra.db.marshal.AbstractType;
import org.apache.cassandra.db.marshal.MarshalException;
import org.apache.cassandra.locator.AbstractReplicationStrategy;
import org.apache.cassandra.locator.NetworkTopologyStrategy;
import org.apache.cassandra.utils.FBUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This has a lot of building blocks for CassandraServer to call make sure it has valid put. 
 **/
public class ThriftValidation {

	private static Logger logger=LoggerFactory.getLogger(ThriftValidation.class);

	//it is need to valide tablename from schema.
	public static void validateTable(String tablename) throws KeyspaceNotDefinedException {
		if(!Schema.instance.getTables().contains(tablename)){
			throw new KeyspaceNotDefinedException("Keyspace " + tablename + " does not exist");
		}
	}

	public static CFMetaData validateColumnFamily(String tablename,
			String cfName) throws InvalidRequestException {
		validateTable(tablename);
		if(cfName.isEmpty()){
			throw new InvalidRequestException("non-empty columnfamily is required");
		}
		CFMetaData metadata=Schema.instance.getCFMetaData(tablename,cfName);
		if(metadata==null){
			throw new InvalidRequestException("unconfigured columnfamily "+cfName);
		}
		return metadata;
	}

	public static void validateColumnPath(CFMetaData metadata,
			ColumnPath column_path) throws InvalidRequestException {
		if(metadata.cfType==ColumnFamilyType.Standard){
			if(column_path.super_column!=null){
				throw new InvalidRequestException("supercolumn parameter is invalid for standard CF "+metadata.cfName);
			}
			if(column_path.column==null){
				throw new InvalidRequestException("column parameter is not optional for standard CF " + metadata.cfName); 
			}
		}else{
			if(column_path.super_column==null)
				throw new InvalidRequestException("supercolumn parameter is not optional for super CF " + metadata.cfName);
		}
		if(column_path.column!=null){
			validateColumnNames(metadata, column_path.super_column, Arrays.asList(column_path.column));
		}
		if(column_path.super_column!=null){
			validateColumnNames(metadata, column_path.super_column, Arrays.asList(column_path.super_column));
		}
	}

	private static void validateColumnNames(CFMetaData metadata,
			ByteBuffer super_column, Iterable<ByteBuffer> column_names) throws InvalidRequestException {
		if(super_column!=null){
			if(super_column.remaining()>IColumn.MAX_NAME_LENGTH){
				throw new InvalidRequestException("supercolumn name length must not be greater than " + IColumn.MAX_NAME_LENGTH);
			}
			if(super_column.remaining()==0){
				throw new InvalidRequestException("supercolumn name must not be empty");
			}
			if(metadata.cfType==ColumnFamilyType.Standard){
				throw new InvalidRequestException("supercolumn specified to ColumnFamily " + metadata.cfName + " containing normal columns");
			}
		}
		AbstractType comparator=metadata.getComparatorFor(super_column);
		for(ByteBuffer name:column_names){
			if(name.remaining()>IColumn.MAX_NAME_LENGTH){
				throw new InvalidRequestException("column name length must not be greater than " + IColumn.MAX_NAME_LENGTH);
			}
			if(name.remaining()==0){
				throw new InvalidRequestException("column name must not be empty");
			}
			try{
				comparator.validate(name);
			}catch(MarshalException e){
				throw new InvalidRequestException(e.getMessage());
			}
		}
	}

	public static void validateConsistencyLevel(String table,
			ConsistencyLevel cl, RequestType requestType) throws InvalidRequestException {
		switch(cl){
			case ANY: 
				if(requestType==RequestType.READ){
					throw new InvalidRequestException("ANY ConsistencyLever is only supported for writes");
				}
				break;
			case LOCAL_QUORUM:
				requireNetworkTopologyStategy(table,cl);
				break;
			case EACH_QUORUM:
				requireNetworkTopologyStategy(table,cl);
				if(requestType==RequestType.READ){
					throw new InvalidRequestException("EACH_QUORUM ConsistencyLevel is only supported for writes");
				}
				break;
				
		}
	}

	private static void requireNetworkTopologyStategy(String table,
			ConsistencyLevel cl) throws InvalidRequestException {
		AbstractReplicationStrategy strategy=Table.open(table).getReplicationStrategy();
		if(!(strategy instanceof NetworkTopologyStrategy)){
			throw new InvalidRequestException(String.format("consistency level %s not compatible with replication strategy (%s)",
                    cl, strategy.getClass().getName()));
		}
	}

	public static void validateKey(CFMetaData metadata, ByteBuffer key) throws InvalidRequestException {
		if(key==null||key.remaining()==0){
			throw new InvalidRequestException("Key may not be empty");
		}
		if(key.remaining()>FBUtilities.MAX_UNSIGNED_SHORT){
			throw new InvalidRequestException("Key length of"+key.remaining()+
					" is longer than maximum of "+FBUtilities.MAX_UNSIGNED_SHORT);
		}
		try{
			metadata.getKeyValidator().validate(key);
		}catch(MarshalException e){
			throw new InvalidRequestException(e.getMessage());
		}
	}

	public static CFMetaData validateColumnFamily(String tablename,
			String cfname, boolean isCommutativeOp) throws InvalidRequestException {
		CFMetaData metadata=validateColumnFamily(tablename,cfname);
		if(isCommutativeOp){
			 if (!metadata.getDefaultValidator().isCommutative())
	                throw new InvalidRequestException("invalid operation for non commutative columnfamily " + cfname);
		}else{
			 if (metadata.getDefaultValidator().isCommutative())
	                throw new InvalidRequestException("invalid operation for commutative columnfamily " + cfname);
		}
		return metadata;
	}

	public static void validateCommutativeForWrite(CFMetaData metadata,
			ConsistencyLevel consistency) throws InvalidRequestException {
		if(consistency==ConsistencyLevel.ANY){
			
		}else if(!metadata.getReplicateOnWrite()&&consistency!=ConsistencyLevel.ONE){
			throw new InvalidRequestException("cannot achieve CL > CL.ONE without replicate_on_write on columnfamily " + metadata.cfName);
		}
	}

	public static void validateColumnParent(CFMetaData metadata,
			ColumnParent column_parent) throws InvalidRequestException {
		if(metadata.cfType==ColumnFamilyType.Standard){
			if(column_parent.super_column!=null){
				 throw new InvalidRequestException("columnfamily alone is required for standard CF " + metadata.cfName);
			}
		}
		
		if(column_parent.super_column!=null){
			validateColumnNames(metadata, (ByteBuffer)null, Arrays.asList(column_parent.super_column));
		}
	}

	public static void validateColumnNames(CFMetaData metadata,
			ColumnParent column_parent, Iterable<ByteBuffer> asList) throws InvalidRequestException {
		validateColumnNames(metadata,column_parent.super_column,asList);
	}
	
}
