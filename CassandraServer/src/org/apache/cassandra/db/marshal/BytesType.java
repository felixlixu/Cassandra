package org.apache.cassandra.db.marshal;

import java.nio.ByteBuffer;

public class BytesType extends AbstractType<ByteBuffer> {

	public static final BytesType instance=new BytesType();
	
	@Override
	public int compare(ByteBuffer arg0, ByteBuffer arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void validate(ByteBuffer bytes) throws MarshalException {
		
	}

	@Override
	public String getString(ByteBuffer bytes) {
		// TODO Auto-generated method stub
		return null;
	}

}
