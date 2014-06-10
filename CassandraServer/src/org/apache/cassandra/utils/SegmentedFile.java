package org.apache.cassandra.utils;

import org.apache.cassandra.config.Config;
import org.apache.cassandra.config.Config.DiskAccessMode;
import org.apache.cassandra.utils.SegmentedFile.Builder;

public class SegmentedFile {

	public static class Builder {

	}

	public static Builder getBuilder(DiskAccessMode mode) {
		return mode==Config.DiskAccessMode.mmap
				?new MmappedSegmentedFile.Builder()
				:new BufferedSegmentedFile.Builder();
	}

	public static Builder getCompressedBuilder() {
		return new CompressedSegmentedFile.Builder();
	}

}
