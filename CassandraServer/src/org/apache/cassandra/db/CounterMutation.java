package org.apache.cassandra.db;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Collection;

import org.apache.cassandra.io.IVersionedSerializer;
import org.apache.cassandra.net.Message;
import org.apache.cassandra.service.StorageService;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.utils.FBUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CounterMutation implements IMutation {

	private static final Logger logger = LoggerFactory.getLogger(CounterMutation.class); 
	private static final CounterMutationSerializer serializer = new CounterMutationSerializer();
	private final RowMutation rowMutation;
	private final ConsistencyLevel consistency;

	public CounterMutation(RowMutation rm, ConsistencyLevel consistency_level) {
		this.rowMutation=rm;
		this.consistency=consistency_level;
	}

	@Override
	public String toString(boolean b) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTable() {
		// TODO Auto-generated method stub
		return null;
	}

	public ByteBuffer key() {
		// TODO Auto-generated method stub
		return null;
	}

	public ConsistencyLevel consistency() {
		return consistency;
	}

	public Message makeMutationMessage(int version) throws IOException {
		byte[] bytes=FBUtilities.serialize(this, serializer, version);
		return new Message(FBUtilities.getBroadcastAddress(),StorageService.Verb.COUNTER_MUTATION,bytes,version);
	}

	public RowMutation rowMutation() {
		return rowMutation;
	}
}

class CounterMutationSerializer implements IVersionedSerializer<CounterMutation>
{
    public void serialize(CounterMutation cm, DataOutput dos, int version) throws IOException
    {
        RowMutation.serializer().serialize(cm.rowMutation(), dos, version);
        dos.writeUTF(cm.consistency().name());
    }

    public CounterMutation deserialize(DataInput dis, int version) throws IOException
    {
        RowMutation rm = RowMutation.serializer().deserialize(dis, version);
        ConsistencyLevel consistency = Enum.valueOf(ConsistencyLevel.class, dis.readUTF());
        return new CounterMutation(rm, consistency);
    }

    public long serializedSize(CounterMutation cm, int version)
    {
        return RowMutation.serializer().serializedSize(cm.rowMutation(), version)
               + DBConstants.shortSize + FBUtilities.encodedUTF8Length(cm.consistency().name());
    }
}
