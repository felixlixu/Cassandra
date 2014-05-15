package org.apache.cassandra.auth;

import java.util.Collections;
import java.util.Set;

public class AuthenticatedUser {

	private final String username;
	private final Set<String> groups;

	public AuthenticatedUser(String username) {
		this.username=username;
		this.groups=Collections.emptySet();
	}

}
