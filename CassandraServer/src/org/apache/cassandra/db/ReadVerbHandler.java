package org.apache.cassandra.db;

import java.io.DataInputStream;
import java.io.IOException;

import org.apache.cassandra.io.utils.FastByteArrayInputStream;
import org.apache.cassandra.net.IVerbHandler;
import org.apache.cassandra.net.Message;
import org.apache.cassandra.service.StorageService;
import org.apache.cassandra.utils.ByteBufferUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadVerbHandler implements IVerbHandler {

	private static Logger logger_ = LoggerFactory.getLogger( ReadVerbHandler.class );
	
	@Override
	public void doVerb(Message message, String id) {
		if(StorageService.instance.isBootstrapMode()){
			throw new RuntimeException("Cannot service reads while bootstrapping!");
		}
		try{
			FastByteArrayInputStream in=new FastByteArrayInputStream(message.getMessageBody());
			ReadCommand command=ReadCommand.serializer().deserialize(new DataInputStream(in),message.getVersion());
			Table table=Table.open(command.table);
			Row row=command.getRow(table);
			
			ReadResponse response=getResponse(command,row);
			
		}catch(IOException ex){
			throw new RuntimeException(ex);
		}
	}

	public static ReadResponse getResponse(ReadCommand command, Row row) {
		if (command.isDigestQuery()){
			if (logger_.isDebugEnabled())
                logger_.debug("digest is " + ByteBufferUtil.bytesToHex(ColumnFamily.digest(row.cf)));
            return new ReadResponse(ColumnFamily.digest(row.cf));
		}
		else{
			return new ReadResponse(row);
		}
	}

}
