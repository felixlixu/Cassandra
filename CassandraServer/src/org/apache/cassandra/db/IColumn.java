package org.apache.cassandra.db;

import java.nio.ByteBuffer;
import java.util.Collection;

import org.apache.cassandra.utils.FBUtilities;

public interface IColumn {

	public static final int MAX_NAME_LENGTH = FBUtilities.MAX_UNSIGNED_SHORT;

	int serializedSize();

	void addColumn(CounterUpdateColumn column);

	ByteBuffer name();

	Collection<IColumn> getSubColumns();

	int serializationFlags();

	int getLocalDeletionTime();

	long timestamp();

	ByteBuffer value();

}
