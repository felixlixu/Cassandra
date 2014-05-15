package org.apache.cassandra.db;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.cassandra.db.filter.QueryPath;
import org.apache.cassandra.db.marshal.AbstractType;
import org.apache.cassandra.io.IVersionedSerializer;
import org.apache.cassandra.net.Message;
import org.apache.cassandra.utils.ByteBufferUtil;
import org.apache.cassandra.utils.FBUtilities;

public class SliceByNamesReadCommand extends ReadCommand {
	
	public final SortedSet<ByteBuffer> columnNames;
	
	public SliceByNamesReadCommand(String keyspace, ByteBuffer key,
			QueryPath path, List<ByteBuffer> nameAsList) {
		super(keyspace,key,path,CMD_TYPE_GET_SLICE_BY_NAMES);
		this.columnNames=new TreeSet<ByteBuffer>(getComparator());
		this.columnNames.addAll(columnNames);
	}
	
    public String getColumnFamilyName()
    {
        return queryPath.columnFamilyName;
    }
	
    protected AbstractType getComparator()
    {
        return ColumnFamily.getComparatorFor(table, getColumnFamilyName(), queryPath.superColumnName);
    }

	
    @Override
    public String toString()
    {
        return "SliceByNamesReadCommand(" +
               "table='" + table + '\'' +
               ", key=" + ByteBufferUtil.bytesToHex(key) +
               ", columnParent='" + queryPath + '\'' +
               ", columns=[" + getComparator().getString(columnNames) + "]" +
               ')';
    }

	@Override
	public Message getMessage(Integer version) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}

class SliceByNamesReadCommandSerializer implements IVersionedSerializer<ReadCommand>
{
    public void serialize(ReadCommand cmd, DataOutput dos, int version) throws IOException
    {
        SliceByNamesReadCommand command = (SliceByNamesReadCommand) cmd;
        dos.writeBoolean(command.isDigestQuery());
        dos.writeUTF(command.table);
        ByteBufferUtil.writeWithShortLength(command.key, dos);
        command.queryPath.serialize(dos);
        dos.writeInt(command.columnNames.size());
        if (!command.columnNames.isEmpty())
        {
            for (ByteBuffer cName : command.columnNames)
            {
                ByteBufferUtil.writeWithShortLength(cName, dos);
            }
        }
    }

    public SliceByNamesReadCommand descrialize(DataInput dis, int version) throws IOException
    {
        boolean isDigest = dis.readBoolean();
        String table = dis.readUTF();
        ByteBuffer key = ByteBufferUtil.readWithShortLength(dis);
        QueryPath columnParent = QueryPath.deserialize(dis);

        int size = dis.readInt();
        List<ByteBuffer> columns = new ArrayList<ByteBuffer>();
        for (int i = 0; i < size; ++i)
        {
            columns.add(ByteBufferUtil.readWithShortLength(dis));
        }
        SliceByNamesReadCommand command = new SliceByNamesReadCommand(table, key, columnParent, columns);
        command.setDigestQuery(isDigest);
        return command;
    }

    public long serializedSize(ReadCommand cmd, int version)
    {
        SliceByNamesReadCommand command = (SliceByNamesReadCommand) cmd;
        int size = DBConstants.boolSize;
        size += DBConstants.shortSize + FBUtilities.encodedUTF8Length(command.table);
        size += DBConstants.shortSize + command.key.remaining();
        size += command.queryPath.serializedSize();
        size += DBConstants.intSize;
        if (!command.columnNames.isEmpty())
        {
            for (ByteBuffer cName : command.columnNames)
                size += DBConstants.shortSize + cName.remaining();
        }
        return size;
    }
}