package org.apache.cassandra.auth;

import java.util.Map;

public interface IAuthenticator {
	
	public static final String USERNAME_KEY="username";
	public static final String PASSWORD_KEY="password";
	AuthenticatedUser authenticate(
			Map<? extends CharSequence, ? extends CharSequence> credentials);
	AuthenticatedUser defaultUser();
}
