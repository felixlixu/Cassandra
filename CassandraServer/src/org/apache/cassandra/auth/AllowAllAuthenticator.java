package org.apache.cassandra.auth;

import java.util.Map;

import org.apache.cassandra.config.ConfigurationException;

public class AllowAllAuthenticator implements IAuthenticator {

	private final static AuthenticatedUser USER = new AuthenticatedUser("allow_all");
	
	@Override
	public AuthenticatedUser authenticate(
			Map<? extends CharSequence, ? extends CharSequence> credentials) {
		// TODO Auto-generated method stub
		return USER;
	}

	@Override
	public AuthenticatedUser defaultUser() {
		// TODO Auto-generated method stub
		return USER;
	}
	
    public void validateConfiguration() throws ConfigurationException
    {
        // pass
    }

}
