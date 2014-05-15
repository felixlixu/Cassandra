package org.apache.cassandra.db;

import java.nio.ByteBuffer;

import org.apache.cassandra.db.filter.QueryPath;
import org.apache.cassandra.net.MessageProducer;
import org.apache.cassandra.service.IReadCommand;
import org.apache.cassandra.service.RepairCallback;

public abstract class ReadCommand implements MessageProducer,IReadCommand {

	public static final byte CMD_TYPE_GET_SLICE_BY_NAMES=1;
	
	public final ByteBuffer key;
	public final QueryPath queryPath;
	public final String table;
	public final byte commandType;
	private boolean isDigestQuery=false;
	
	protected ReadCommand(String table,ByteBuffer key,QueryPath queryPath,byte cmdType){
		this.table=table;
		this.key=key;
		this.queryPath=queryPath;
		this.commandType=cmdType;
	}
	
	public boolean isDigestQuery(){
		return isDigestQuery();
	}
	
	public void setDigestQuery(boolean isDigest) {
		this.isDigestQuery=isDigest;
	}

	public String getKeyspace() {
		return table;
	}

	public String getColumnFamilyName() {
		return queryPath.columnFamilyName;
	}

	public ReadCommand copy() {
		// TODO Auto-generated method stub
		return null;
	}

	public void maybeTrim(Row row) {
		// TODO Auto-generated method stub
		
	}

	public ReadCommand maybeGenerateRetryCommand(RepairCallback handler, Row row) {
		// TODO Auto-generated method stub
		return null;
	}

}