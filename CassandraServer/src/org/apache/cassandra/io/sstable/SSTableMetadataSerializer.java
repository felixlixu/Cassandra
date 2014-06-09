package org.apache.cassandra.io.sstable;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.cassandra.db.commitlog.ReplayPosition;
import org.apache.cassandra.utils.EstimatedHistogram;
import org.apache.cassandra.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSTableMetadataSerializer {

	private static final Logger logger = LoggerFactory
			.getLogger(SSTableMetadataSerializer.class);
	
    public void serialize(SSTableMetadata sstableStats, DataOutput dos) throws IOException
    {
        assert sstableStats.partitioner != null;

        EstimatedHistogram.serializer.serialize(sstableStats.estimatedRowSize, dos);
        EstimatedHistogram.serializer.serialize(sstableStats.estimatedColumnCount, dos);
        ReplayPosition.serializer.serialize(sstableStats.replayPosition, dos);
        dos.writeLong(sstableStats.maxTimestamp);
        dos.writeDouble(sstableStats.compressionRatio);
        dos.writeUTF(sstableStats.partitioner);
    }


	public SSTableMetadata deserialize(Descriptor descriptor)
			throws IOException {
		logger.debug("Load metadata for {}", descriptor);
		File statsFile = new File(
				descriptor.filenameFor(SSTable.COMPONENT_STATS));
		if (!statsFile.exists()) {
			logger.debug("No sstable stats for {}", descriptor);
			return new SSTableMetadata();
		}

		DataInputStream dis = new DataInputStream(new BufferedInputStream(
				new FileInputStream(statsFile)));
		try {
			return deserialize(dis, descriptor);
		} finally {
			FileUtils.closeQuietly(dis);
		}
	}

	public SSTableMetadata deserialize(DataInputStream dis, Descriptor desc)
			throws IOException {
		EstimatedHistogram rowSizes = EstimatedHistogram.serializer
				.deserialize(dis);
		EstimatedHistogram columnCounts = EstimatedHistogram.serializer
				.deserialize(dis);
		ReplayPosition replayPosition = desc.metadataIncludesReplayPosition ? ReplayPosition.serializer
				.deserialize(dis) : ReplayPosition.NONE;
		long maxTimestamp = desc.tracksMaxTimestamp ? dis.readLong()
				: Long.MIN_VALUE;
		double compressionRatio = desc.hasCompressionRatio ? dis.readDouble()
				: Double.MIN_VALUE;
		String partitioner = desc.hasPartitioner ? dis.readUTF() : null;
		return new SSTableMetadata(rowSizes, columnCounts, replayPosition,
				maxTimestamp, compressionRatio, partitioner);
	}

}
