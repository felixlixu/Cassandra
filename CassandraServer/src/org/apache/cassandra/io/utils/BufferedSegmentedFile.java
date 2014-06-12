package org.apache.cassandra.io.utils;

import java.io.File;

public class BufferedSegmentedFile extends SegmentedFile {

	public BufferedSegmentedFile(String path, long length) {
		super(path,length);
	}

	static class Builder extends SegmentedFile.Builder{

		@Override
		public void addPotentialBoundary(long boundary) {
			// only one segment in a standar-io file
		}

		@Override
		public SegmentedFile complete(String path) {
			long length=new File(path).length();
			return new BufferedSegmentedFile(path,length);
		}
		
	}
}
