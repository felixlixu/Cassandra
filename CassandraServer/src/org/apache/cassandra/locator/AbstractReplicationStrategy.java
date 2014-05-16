package org.apache.cassandra.locator;

import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.config.ConfigurationException;
import org.apache.cassandra.dht.RingPosition;
import org.apache.cassandra.dht.Token;
import org.cliffc.high_scale_lib.NonBlockingHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A abstract parent for all replication strategies. 
 * **/
public abstract class AbstractReplicationStrategy {

	private static final Logger logger=LoggerFactory.getLogger(AbstractReplicationStrategy.class);

	public final String table;
	public final Map<String,String> configOptions;
	private final TokenMetadata tokenMetadata;
	
	public IEndpointSnitch snitch;
	
	AbstractReplicationStrategy(String table,TokenMetadata tokenMetadata,IEndpointSnitch snitch,Map<String,String> configOptions){
		assert table!=null;
		assert snitch!=null;
		assert tokenMetadata!=null;
		
		this.tokenMetadata=tokenMetadata;
		this.snitch=snitch;
		this.tokenMetadata.register(this);
		this.configOptions=configOptions;
		this.table=table;
	}
	
	private final Map<Token,ArrayList<InetAddress>> cachedEndpoints=new NonBlockingHashMap<Token, ArrayList<InetAddress>>();
	
	public ArrayList<InetAddress> getCachedEndpoints(Token t){
		return cachedEndpoints.get(t);
	}
	
	public abstract List<InetAddress> calculateNaturalEndpoints(Token searchToken, TokenMetadata tokenMetadata);
	
	/**
	 * get the endpoints that should store the given Token.
	 * Note that while the endpoints are conceptually a Set(no duplicates will be included),
	 * we return a List to avoid an extra allocation when sorting by proximity later.
	 * */
	public List<InetAddress> getNaturalEndpoints(RingPosition serachPosition) {
		Token searchToken=serachPosition.getToken();
		Token keyToken=TokenMetadata.firstToken(tokenMetadata.sortedTokens(),searchToken);
		
		ArrayList<InetAddress> endpoints=getCachedEndpoints(keyToken);
		if(endpoints==null){
			TokenMetadata tokenMetadataClone=tokenMetadata.cloneOnlyTokenMap();
			keyToken=TokenMetadata.firstToken(tokenMetadataClone.sortedTokens(),searchToken);
			endpoints=new ArrayList<InetAddress>(calculateNaturalEndpoints(searchToken, tokenMetadataClone));
			cacheEndpoint(keyToken,endpoints);
		}
		return new ArrayList<InetAddress>(endpoints);
	}

	private void cacheEndpoint(Token keyToken, ArrayList<InetAddress> endpoints) {
		cachedEndpoints.put(keyToken, endpoints);
	}

	public abstract void validateOptions() throws ConfigurationException;
	
	public static AbstractReplicationStrategy createReplicationStrategy(
			String table,
			Class<? extends AbstractReplicationStrategy> strategyClass,
			TokenMetadata tokenMetadata, IEndpointSnitch snitch,
			Map<String, String> strategyOptions) throws ConfigurationException {
		
		AbstractReplicationStrategy strategy;
		Class[] parameterTypes=new Class[]{String.class,TokenMetadata.class,IEndpointSnitch.class,Map.class};
		try{
			Constructor<? extends AbstractReplicationStrategy> constructor=strategyClass.getConstructor(parameterTypes);
			strategy=constructor.newInstance(table,tokenMetadata,snitch,strategyOptions);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		strategy.validateOptions();
		return strategy;
	}

	public abstract int getReplicationFactor();
		
	

}
