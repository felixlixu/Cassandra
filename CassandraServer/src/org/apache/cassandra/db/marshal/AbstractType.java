package org.apache.cassandra.db.marshal;

import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.SortedSet;

public abstract class AbstractType<T> implements Comparator<ByteBuffer> {

	public abstract void validate(ByteBuffer name) throws MarshalException;

    /** get a string representation of the bytes suitable for log messages */
    public abstract String getString(ByteBuffer bytes);
	
	public String getString(SortedSet<ByteBuffer> names) {
        StringBuilder builder = new StringBuilder();
        for (ByteBuffer name : names)
        {
            builder.append(getString(name)).append(",");
        }
        return builder.toString();
	}

	public boolean isCommutative() {
		return false;
	}

}
