package org.apache.cassandra.net;

import java.io.IOException;

public interface MessageProducer {

	Message getMessage(Integer version) throws IOException;

}
