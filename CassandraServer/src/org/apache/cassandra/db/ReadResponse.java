package org.apache.cassandra.db;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.cassandra.io.IColumnSerializer;
import org.apache.cassandra.io.IVersionedSerializer;
import org.apache.cassandra.utils.ByteBufferUtil;

public class ReadResponse {

	private static ReadResponseSerializer serializer_;
	private final Row row_;
	private final ByteBuffer digest_;
	
	static{
		serializer_=new ReadResponseSerializer();
	}

	public ReadResponse(Row row) {
		assert row!=null;
		row_=row;
		digest_=null;
	}

	public ReadResponse(ByteBuffer digest) {
		assert digest!=null;
		digest_=digest;
		row_=null;
	}
	
	public Row row() 
    {
		return row_;
    }
        
	public ByteBuffer digest() 
    {
		return digest_;
	}

	public boolean isDigestQuery()
    {
    	return digest_ != null;
    }

}

class ReadResponseSerializer implements IVersionedSerializer<ReadResponse>
{
	@Override
	public void serialize(ReadResponse response, DataOutput dos, int version) throws IOException
	{
        dos.writeInt(response.isDigestQuery() ? response.digest().remaining() : 0);
        ByteBuffer buffer = response.isDigestQuery() ? response.digest() : ByteBufferUtil.EMPTY_BYTE_BUFFER;
        ByteBufferUtil.write(buffer, dos);
        dos.writeBoolean(response.isDigestQuery());
        if (!response.isDigestQuery())
            Row.serializer().serialize(response.row(), dos, version);
    }

    public long serializedSize(ReadResponse response, int version)
    {
        int size = DBConstants.intSize;
        size += DBConstants.boolSize;
        if (response.isDigestQuery())
            size += response.digest().remaining();
        else
            size += Row.serializer().serializedSize(response.row(), version);
        return size;
    }

	@Override
	public ReadResponse deserialize(DataInput dis, int version)
			throws IOException {
        byte[] digest = null;
        int digestSize = dis.readInt();
        if (digestSize > 0)
        {
            digest = new byte[digestSize];
            dis.readFully(digest, 0, digestSize);
        }
        boolean isDigest = dis.readBoolean();
        assert isDigest == digestSize > 0;

        Row row = null;
        if (!isDigest)
        {
            // This is coming from a remote host
            row = Row.serializer().deserialize(dis, version, IColumnSerializer.Flag.FROM_REMOTE, ArrayBackedSortedColumns.factory());
        }

        return isDigest ? new ReadResponse(ByteBuffer.wrap(digest)) : new ReadResponse(row);
	}

}
