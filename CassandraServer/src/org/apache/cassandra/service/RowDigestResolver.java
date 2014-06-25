package org.apache.cassandra.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

import org.apache.cassandra.db.ColumnFamily;
import org.apache.cassandra.db.ReadResponse;
import org.apache.cassandra.db.Row;
import org.apache.cassandra.net.Message;

public class RowDigestResolver extends AbstractRowResolver {


	public RowDigestResolver(String table, ByteBuffer key) {
		super(key,table);
	}

	@Override
	public Iterable<Message> getMessages() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Row getData() throws IOException {
		for(Map.Entry<Message, ReadResponse> entry:replies.entrySet()){
			ReadResponse result =entry.getValue();
			if(!result.isDigestQuery()){
				return result.row();
			}
		}
		throw new AssertionError("getData should not be invoked when no data is pressent.");
	}

	/**
	 *	This method handles two different scenarios. 
	 * @throws DigestMismatchException 
	 **/
	@Override
	public Row resolve() throws DigestMismatchException {
		if(logger.isDebugEnabled()){
			logger.debug("resolving" + replies.size()+" responses");
		}
		long stratTime=System.currentTimeMillis();
		
		ColumnFamily data=null;
		ByteBuffer digest=null;
		for(Map.Entry<Message, ReadResponse> entry:replies.entrySet()){
			ReadResponse response=entry.getValue();
			if(response.isDigestQuery()){
				if(digest==null){
					digest=response.digest();
				}else{
					ByteBuffer digest2=response.digest();
					if(!digest.equals(digest2))
						throw new DigestMismatchException(key,digest,digest2);
				}
			}else{
				data=response.row().cf;
			}
		}
		if(digest!=null){
			ByteBuffer digest2=ColumnFamily.digest(data);
			if(!digest.equals(digest2)){
				throw new DigestMismatchException(key,digest,digest2);
			}
			if(logger.isDebugEnabled()){
				logger.debug("digest verified");
			}
		}
		if(logger.isDebugEnabled())
			logger.debug("resolve:"+(System.currentTimeMillis()-stratTime)+"ms.");
		return new Row(key,data);
	}

}
