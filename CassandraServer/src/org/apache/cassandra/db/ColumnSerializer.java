package org.apache.cassandra.db;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.cassandra.io.IColumnSerializer;
import org.apache.cassandra.io.utils.FileDataInput;
import org.apache.cassandra.utils.ByteBufferUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColumnSerializer implements IColumnSerializer {

    private static final Logger logger = LoggerFactory.getLogger(ColumnSerializer.class);
    
    public final static int DELETION_MASK       = 0x01;
    public final static int EXPIRATION_MASK     = 0x02;
    public final static int COUNTER_MASK        = 0x04;
    public final static int COUNTER_UPDATE_MASK = 0x08;
	
	@Override
	public void serialize(IColumn column, DataOutput dos) throws IOException {
        assert column.name().remaining() > 0;
        ByteBufferUtil.writeWithShortLength(column.name(), dos);
        try
        {
            dos.writeByte(column.serializationFlags());
            if (column instanceof CounterColumn)
            {
                dos.writeLong(((CounterColumn)column).timestampOfLastDelete());
            }
            else if (column instanceof ExpiringColumn)
            {
                dos.writeInt(((ExpiringColumn) column).getTimeToLive());
                dos.writeInt(column.getLocalDeletionTime());
            }
            dos.writeLong(column.timestamp());
            ByteBufferUtil.writeWithLength(column.value(), dos);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
	}

    public Column deserialize(DataInput dis, IColumnSerializer.Flag flag) throws IOException
    {
        return deserialize(dis, flag, (int) (System.currentTimeMillis() / 1000));
    }
	
	@Override
	public IColumn deserialize(DataInput dis) throws IOException {
		 return deserialize(dis, Flag.LOCAL);
	}

	@Override
	public long serializedSize(IColumn object) {
		return object.serializedSize();
	}

	@Override
	public IColumn deserializer(DataInput dis, Flag flag, int expireBefore)
			throws IOException {
	       ByteBuffer name = ByteBufferUtil.readWithShortLength(dis);
	        if (name.remaining() <= 0)
	        {
	            String format = "invalid column name length %d%s";
	            String details = "";
	            if (dis instanceof FileDataInput)
	            {
	                FileDataInput fdis = (FileDataInput)dis;
	                details = String.format(" (%s, %d bytes remaining)", fdis.getPath(), fdis.bytesRemaining());
	            }
	            throw new CorruptColumnException(String.format(format, name.remaining(), details));
	        }

	        int b = dis.readUnsignedByte();
	        if ((b & COUNTER_MASK) != 0)
	        {
	            long timestampOfLastDelete = dis.readLong();
	            long ts = dis.readLong();
	            ByteBuffer value = ByteBufferUtil.readWithLength(dis);
	            return CounterColumn.create(name, value, ts, timestampOfLastDelete, flag);
	        }
	        else if ((b & EXPIRATION_MASK) != 0)
	        {
	            int ttl = dis.readInt();
	            int expiration = dis.readInt();
	            long ts = dis.readLong();
	            ByteBuffer value = ByteBufferUtil.readWithLength(dis);
	            return ExpiringColumn.create(name, value, ts, ttl, expiration, expireBefore, flag);
	        }
	        else
	        {
	            long ts = dis.readLong();
	            ByteBuffer value = ByteBufferUtil.readWithLength(dis);
	            return (b & COUNTER_UPDATE_MASK) != 0
	                   ? new CounterUpdateColumn(name, value, ts)
	                   : ((b & DELETION_MASK) == 0
	                      ? new Column(name, value, ts)
	                      : new DeletedColumn(name, value, ts));
	        }
	}
	
    private static class CorruptColumnException extends IOException
    {
        public CorruptColumnException(String s)
        {
            super(s);
        }
    }

}
