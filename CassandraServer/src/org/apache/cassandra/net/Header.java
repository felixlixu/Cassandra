package org.apache.cassandra.net;

import java.net.InetAddress;
import java.util.Map;

import org.apache.cassandra.service.StorageService;

public class Header {

	private final InetAddress from_;
	
    Header(InetAddress from, StorageService.Verb verb, Map<String, byte[]> details)
    {
        assert from != null;
        assert verb != null;

        from_ = from;
    }
	
    InetAddress getFrom()
    {
        return from_;
    }
}
