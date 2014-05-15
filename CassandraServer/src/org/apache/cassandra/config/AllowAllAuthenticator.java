package org.apache.cassandra.config;

import java.util.Map;

import org.apache.cassandra.auth.AuthenticatedUser;
import org.apache.cassandra.auth.IAuthenticator;

public class AllowAllAuthenticator implements IAuthenticator {

	private final static AuthenticatedUser USER=new AuthenticatedUser("allow_all");
	
	@Override
	public AuthenticatedUser authenticate(
			Map<? extends CharSequence, ? extends CharSequence> credentials) {
		return USER;
	}

	@Override
	public AuthenticatedUser defaultUser() {
		return USER;
	}

}
