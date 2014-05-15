package org.apache.cassandra.auth;

import java.util.List;

import org.apache.cassandra.utils.Hex;

public class Resource {

	public final static String ROOT="cassandra";
	public final static String KEYSPACES="keyspaces";
	public static String toString(List<Object> resource) {
        StringBuilder buff = new StringBuilder();
        for (Object component : resource)
        {
            buff.append("/");
            if (component instanceof byte[])
                buff.append(Hex.bytesToHex((byte[])component));
            else
                buff.append(component.toString());
        }
        return buff.toString();
	}
}
