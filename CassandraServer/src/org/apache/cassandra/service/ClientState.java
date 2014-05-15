package org.apache.cassandra.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.cassandra.auth.AuthenticatedUser;
import org.apache.cassandra.auth.Permission;
import org.apache.cassandra.auth.Resource;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.cql.CQLStatement;
import org.apache.cassandra.thrift.AuthenticationException;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A container for per-client,thread-local state that Avro/Thrift threads must hold.
 **/
public class ClientState {

	private static Logger logger=LoggerFactory.getLogger(ClientState.class);
	
	private AuthenticatedUser user;
	private String keyspace;
	
	//Enough to keep buggy clients from OOM'ing us
	private static final int MAX_CACHE_PREPARED=10000;
	
	//reusable array for authorization
	private final List<Object> resource=new ArrayList<Object>();
	
	private Map<Integer,CQLStatement> prepared=new LinkedHashMap<Integer,CQLStatement>(16,0.75f,true){
		protected boolean removeEldsetEntry(Map.Entry<Integer, CQLStatement> eldst){
			return size() > MAX_CACHE_PREPARED;
		}
	};
	
	public ClientState(){
		reset();
	}
	
	public void reset() {
		user=DatabaseDescriptor.getAuthenticator().defaultUser();
		keyspace=null;
		resourceClear();
		prepared.clear();
	}

	private void resourceClear(){
		resource.clear();
		resource.add(Resource.ROOT);
		resource.add(Resource.KEYSPACES);
	}
	
	public void login(Map<? extends CharSequence,? extends CharSequence> credentials) throws AuthenticationException{
		AuthenticatedUser user=DatabaseDescriptor.getAuthenticator().authenticate(credentials);
		if(logger.isDebugEnabled()){
			logger.debug("logged in:{}",user);
		}
		System.out.println("This method is running");
		this.user=user;
	}

	public void setKeyspace(String ks) {
		this.keyspace=ks;
	}

	public void hasColumnFamilyAccess(String column_family, Permission perm) throws InvalidRequestException {
		hasColumnFamilyAccess(keyspace,column_family,perm);
	}

	public void hasColumnFamilyAccess(String keyspace, String columnFamily,
			Permission perm) throws InvalidRequestException {
		validateLogin();
		validateKeyspace(); //the current clientstate has this keyspace.
		
		resourceClear();
		resource.add(keyspace);
		resource.add(columnFamily);
		Set<Permission> perms=DatabaseDescriptor.getAuthority().authorize(user,resource);
		
		hasAccess(user,perms,perm,resource);
	}

	private static void hasAccess(AuthenticatedUser user,Set<Permission> perms,Permission perm,List<Object> resource) throws InvalidRequestException{
		if(perms.contains(perm)){
			return;
		}
		throw new InvalidRequestException(String.format("%s does not have permission %s for %s", 
				user,
				perm,
				Resource.toString(resource)));
	}
	
	private void validateKeyspace() throws InvalidRequestException {
		if(keyspace==null){
			throw new InvalidRequestException("You have not set a keyspace for this  session.");	
		}
	}

	private void validateLogin() throws InvalidRequestException {
		if(user==null){
			throw new InvalidRequestException("You have not logged in.");
		}
	}

	public String getKeyspace() throws InvalidRequestException {
		if(keyspace==null){
			throw new InvalidRequestException("no keyspace has been specified");
		}
		return keyspace;
	}

	public String getShcedulingValue() {
		switch(DatabaseDescriptor.getRequestSchedulerId()){
			case keyspace:return keyspace;
		}
		return "default";
	}
}
