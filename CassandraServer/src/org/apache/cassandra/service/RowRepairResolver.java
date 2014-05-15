package org.apache.cassandra.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

import org.apache.cassandra.db.Row;
import org.apache.cassandra.net.IAsyncResult;
import org.apache.cassandra.net.Message;

public class RowRepairResolver extends AbstractRowResolver {

	public List<IAsyncResult> repairResults=Collections.emptyList();

	public RowRepairResolver(String table, ByteBuffer key) {
		super(key,table);
	}

	@Override
	public Iterable<Message> getMessages() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Row getData() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Row resolve() {
		// TODO Auto-generated method stub
		return null;
	}

}