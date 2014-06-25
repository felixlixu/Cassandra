package org.apache.cassandra.net;

import java.net.InetAddress;

import org.apache.cassandra.concurrent.Stage;

public class OutboundTcpConnectionPool {

	public final OutboundTcpConnection cmdCon;
	public final OutboundTcpConnection ackCon;
	private final InetAddress id;
	private InetAddress resetedEndpoint;
	
	public OutboundTcpConnectionPool(InetAddress remoteEp) {
		this.id=remoteEp;
		cmdCon=new OutboundTcpConnection(this);
		cmdCon.start();
		ackCon=new OutboundTcpConnection(this);
		ackCon.start();
	}

	public OutboundTcpConnection getConnection(Message message) {
		Stage stage=message.getMessageType();
		return stage==Stage.REQUEST_RESPONSE||stage==Stage.INTERNAL_RESPONSE||stage==Stage.GOSSIP
				?ackCon
				:cmdCon;
	}

	public InetAddress endPoint() {
		return resetedEndpoint==null?id:resetedEndpoint;
	}

}
