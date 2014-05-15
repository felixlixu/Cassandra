package org.apache.cassandra.service;

import java.net.InetAddress;
import java.util.List;

import org.apache.cassandra.db.Row;
import org.apache.cassandra.net.IAsyncCallback;
import org.apache.cassandra.net.Message;

public class RepairCallback implements IAsyncCallback {

	public final RowRepairResolver resolver;

	public RepairCallback(RowRepairResolver resolver,
			List<InetAddress> endpoints) {
		this.resolver = resolver;
	}

	@Override
	public boolean isLatencyForSnitch() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void response(Message msg) {
		// TODO Auto-generated method stub

	}

	public Row get() throws DigestMismatchException  {
		// TODO Auto-generated method stub
		return null;
	}

}
