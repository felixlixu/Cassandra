package org.apache.cassandra.auth;

import java.util.List;
import java.util.Set;

public class AllowAllAuthority implements IAuthority {

	@Override
	public Set<Permission> authorize(AuthenticatedUser user,
			List<Object> resource) {
		return Permission.ALL;
	}

}
