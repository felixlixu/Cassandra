package org.apache.cassandra.auth;

import java.util.List;
import java.util.Set;

public interface IAuthority {

	public Set<Permission> authorize(AuthenticatedUser user,
			List<Object> resource);

}
