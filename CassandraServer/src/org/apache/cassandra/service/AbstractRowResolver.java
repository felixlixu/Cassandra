package org.apache.cassandra.service;

import java.nio.ByteBuffer;

import org.apache.cassandra.db.DecoratedKey;
import org.apache.cassandra.db.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRowResolver implements IResponseResolver<Row> {

	protected static Logger logger = LoggerFactory.getLogger(AbstractRowResolver.class);
	private DecoratedKey<?> key;
	protected final String table;

	public AbstractRowResolver(ByteBuffer key, String table) {
		this.key=StorageService.getPartitioner().decorateKey(key);
		this.table=table;
	}

}