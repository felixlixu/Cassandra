package org.apache.cassandra.db.marshal;

import java.nio.ByteBuffer;

public class UTF8Type extends AbstractType<String>{

	public static final UTF8Type  instance=new UTF8Type();
	
	@Override
	public int compare(ByteBuffer arg0, ByteBuffer arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void validate(ByteBuffer name) throws MarshalException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getString(ByteBuffer bytes) {
		// TODO Auto-generated method stub
		return null;
	}

}