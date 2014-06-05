package org.apache.cassandra.db;

import java.nio.ByteBuffer;

public interface IMutation {

	String toString(boolean b);
	String getTable();
	ByteBuffer key();

}
