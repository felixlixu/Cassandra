package org.apache.cassandra.db;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.cassandra.db.filter.QueryPath;
import org.apache.cassandra.io.IVersionedSerializer;
import org.apache.cassandra.net.MessageProducer;
import org.apache.cassandra.service.IReadCommand;
import org.apache.cassandra.service.RepairCallback;

public abstract class ReadCommand implements MessageProducer,IReadCommand {

	public static final byte CMD_TYPE_GET_SLICE_BY_NAMES=1;
	public static Byte CMD_TYPE_GET_SLICE=2;
	
	public final ByteBuffer key;
	public final QueryPath queryPath;
	public final String table;
	public final byte commandType;
	private boolean isDigestQuery=false;
	private static ReadCommandSerializer serializer=new ReadCommandSerializer();

	
	
	protected ReadCommand(String table,ByteBuffer key,QueryPath queryPath,byte cmdType){
		this.table=table;
		this.key=key;
		this.queryPath=queryPath;
		this.commandType=cmdType;
	}
	
	public static ReadCommandSerializer serializer() {
		return serializer;
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

	public abstract Row getRow(Table table) throws IOException;

}

class ReadCommandSerializer implements IVersionedSerializer<ReadCommand>
{
	private static final Map<Byte,IVersionedSerializer<ReadCommand>> CMD_SERIALIZER_MAP=new HashMap<Byte,IVersionedSerializer<ReadCommand>>();

	static
	{
		CMD_SERIALIZER_MAP.put(ReadCommand.CMD_TYPE_GET_SLICE_BY_NAMES, new SliceByNamesReadCommandSerializer());
		//CMD_SERIALIZER_MAP.put(ReadCommand.CMD_TYPE_GET_SLICE, new SliceFromReadCommandSerializer());
	}
	
	@Override
	public void serialize(ReadCommand t, DataOutput dos, int version)
			throws IOException {
		dos.writeByte(t.commandType);
		CMD_SERIALIZER_MAP.get(t.commandType).serialize(t, dos, version);
	}

	@Override
	public ReadCommand deserialize(DataInput dis, int version)
			throws IOException {
		byte msgType=dis.readByte();
		return CMD_SERIALIZER_MAP.get(msgType).deserialize(dis, version);
	}

	@Override
	public long serializedSize(ReadCommand t, int version) {
		return 1 + CMD_SERIALIZER_MAP.get(t.commandType).serializedSize(t, version);
	}
	
}
