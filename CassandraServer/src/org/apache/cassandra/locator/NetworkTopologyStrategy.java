package org.apache.cassandra.locator;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.config.ConfigurationException;
import org.apache.cassandra.dht.Token;

public class NetworkTopologyStrategy extends AbstractReplicationStrategy {

	NetworkTopologyStrategy(String table, TokenMetadata tokenMetadata,
			IEndpointSnitch snitch, Map<String, String> configOptions) {
		super(table, tokenMetadata, snitch, configOptions);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<InetAddress> calculateNaturalEndpoints(Token searchToken,
			TokenMetadata tokenMetadata) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void validateOptions() throws ConfigurationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getReplicationFactor() {
		// TODO Auto-generated method stub
		return 0;
	}

}
