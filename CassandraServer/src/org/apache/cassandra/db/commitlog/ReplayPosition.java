package org.apache.cassandra.db.commitlog;

public class ReplayPosition implements Comparable<ReplayPosition> {

	public static final ReplayPosition NONE=new ReplayPosition(-1,0);
	private final int segment;
	private final int position;

	public ReplayPosition(int segment, int position) {
		this.segment=segment;
		assert position>=0;
		this.position=position;
	}

	@Override
	public int compareTo(ReplayPosition arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

}
