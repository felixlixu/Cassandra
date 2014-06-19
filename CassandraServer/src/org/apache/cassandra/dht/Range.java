package org.apache.cassandra.dht;

import java.io.Serializable;

import org.apache.cassandra.service.StorageService;

public class Range<T extends RingPosition> extends AbstractBounds<T> implements Comparable<Range<T>>,Serializable {

	public Range(T left, T right, IPartitioner partitioner) {
		super(left, right, partitioner);
	}
	
	public Range(T left,T right){
		super(left,right,StorageService.getPartitioner());
	}

	@Override
	public int compareTo(Range<T> arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean contains(T bi) {
		return contains(left,right,bi);
	}
	
	public static<T extends RingPosition> boolean contains(T left,T right,T bi){
		if(isWrapAround(left,right)){
			if(bi.compareTo(left)>0){
				return true;
			}else{
				return right.compareTo(bi)>=0;
			}
		}else{
			return bi.compareTo(left)>0&&right.compareTo(bi)>=0;
		}
	}

	public static<T extends RingPosition> boolean isWrapAround(T left,T right){
		return left.compareTo(right)>=0;
	}
}
