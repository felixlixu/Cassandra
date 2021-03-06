package org.apache.cassandra.io.utils;

import java.io.IOException;
import java.io.InputStream;

public class FastByteArrayInputStream extends InputStream {

	private int mark;
	private byte[] buf;
	private int count;
	protected int pos;

	public FastByteArrayInputStream(byte buf[]) {
		this.mark=0;
		this.buf=buf;
		this.count=buf.length;
	}

	@Override
	public int read() throws IOException {
		return pos<count?buf[pos++] & 0XFF:-1;
	}

}
