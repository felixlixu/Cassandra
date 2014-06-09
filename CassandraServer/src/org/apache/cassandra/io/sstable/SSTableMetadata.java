package org.apache.cassandra.io.sstable;

import org.apache.cassandra.db.commitlog.ReplayPosition;
import org.apache.cassandra.utils.EstimatedHistogram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSTableMetadata {

	private static Logger logger = LoggerFactory.getLogger(SSTableMetadata.class);
	
	public static final SSTableMetadataSerializer serializer=new SSTableMetadataSerializer();

	public static SSTableMetadata createDefaultInstance() {
		return new SSTableMetadata();
	}

    public final EstimatedHistogram estimatedRowSize;
    public final EstimatedHistogram estimatedColumnCount;
    public final ReplayPosition replayPosition;
    public final long maxTimestamp;
    public final double compressionRatio;
    public final String partitioner;
	
	public SSTableMetadata(){
		this(defaultRowSizeHistogram(),
			 defaultColumnCountHistogram(),
			 ReplayPosition.NONE,
			 Long.MIN_VALUE,
			 Double.MIN_VALUE,
			 null);
	}


	public SSTableMetadata(EstimatedHistogram rowSizes,
			EstimatedHistogram columnCount,
			ReplayPosition replayPosition, long maxTimestamp, double cr,String partitioner) {
		this.estimatedRowSize=rowSizes;
		this.estimatedColumnCount=columnCount;
		this.replayPosition=replayPosition;
		this.maxTimestamp=maxTimestamp;
		this.compressionRatio=cr;
		this.partitioner=partitioner;
	}


	private static EstimatedHistogram defaultColumnCountHistogram() {
		
		return new EstimatedHistogram(114);
	}

	private static EstimatedHistogram defaultRowSizeHistogram() {
		
		return new EstimatedHistogram(150);
	}
	
}


