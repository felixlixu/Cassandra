package org.apache.cassandra.service;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentMap;

import org.apache.cassandra.db.DecoratedKey;
import org.apache.cassandra.db.ReadResponse;
import org.apache.cassandra.db.Row;
import org.apache.cassandra.net.Message;
import org.apache.cassandra.utils.FBUtilities;
import org.apache.commons.lang.ArrayUtils;
import org.cliffc.high_scale_lib.NonBlockingHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRowResolver implements IResponseResolver<Row> {

	
	protected static Logger logger = LoggerFactory.getLogger(AbstractRowResolver.class);
	protected DecoratedKey<?> key;
	protected final String table;
	protected final ConcurrentMap<Message,ReadResponse> replies=new NonBlockingHashMap<Message,ReadResponse>();
	private static final Message FAKE_MESSAGE=new Message(FBUtilities.getBroadcastAddress(),StorageService.Verb.INTERNAL_RESPONSE,ArrayUtils.EMPTY_BYTE_ARRAY,-1);
	
	public AbstractRowResolver(ByteBuffer key, String table) {
		this.key=StorageService.getPartitioner().decorateKey(key);
		this.table=table;
	}
	
	public void injectPreProcessed(ReadResponse result){
		assert replies.get(FAKE_MESSAGE)==null;
		replies.put(FAKE_MESSAGE, result);
	}

}
