package org.apache.cassandra;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.cassandra.thrift.AuthenticationException;
import org.apache.cassandra.thrift.AuthenticationRequest;
import org.apache.cassandra.thrift.AuthorizationException;
import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class CassandraClient {

	/**
	 * @param args
	 * @throws TException 
	 * @throws AuthorizationException 
	 * @throws AuthenticationException 
	 */
	public static void main(String[] args) throws AuthenticationException, AuthorizationException, TException {
		// TODO Auto-generated method stub
		TTransport transport=new TSocket("127.0.0.1",9813);
		long start=System.currentTimeMillis();
		TProtocol protocol=new TBinaryProtocol(transport);
		Cassandra.Client client=new Cassandra.Client(protocol);
		transport.open();
		AuthenticationRequest request=new AuthenticationRequest();
		request.credentials=new ConcurrentHashMap<String,String>();
		client.login(request);
		ColumnPath path=new ColumnPath();
		path.column="Lixu".getBytes();
		
		Consistency_Level 
		client.get(key, column_path, consistency_level);
		transport.close();
	}

}
