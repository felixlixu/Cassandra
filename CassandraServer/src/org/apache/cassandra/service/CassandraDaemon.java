package org.apache.cassandra.service;

import java.net.InetSocketAddress;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.CassandraServer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadPoolServer.Args;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class CassandraDaemon {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Cassandra.Processor processor=new Cassandra.Processor(new CassandraServer());
		try{
			TServerTransport serverTransport=new TServerSocket(new InetSocketAddress("0.0.0.0",9813));
			Args trArgs=new Args(serverTransport);
			trArgs.processor(processor);
			trArgs.protocolFactory(new TBinaryProtocol.Factory(true,true));
			TServer server=new TThreadPoolServer(trArgs);
			System.out.println("server begin .................");
			server.serve();
			System.out.println("----------------------");
			server.stop();
		}catch(Exception e){
			throw new RuntimeException("index thrift server start failed!!"+"/n"+e.getMessage());  
		}
	}

}
