package org.apache.cassandra.db.filter;

import java.nio.ByteBuffer;
import java.util.SortedSet;

import org.apache.cassandra.utils.FBUtilities;

public class NamesQueryFilter implements IFilter {

	private final SortedSet<ByteBuffer> columns;

	public NamesQueryFilter(ByteBuffer column) {
		this(FBUtilities.singleton(column));
	}

	public NamesQueryFilter(SortedSet<ByteBuffer> columns) {
		this.columns=columns;
	}

}
