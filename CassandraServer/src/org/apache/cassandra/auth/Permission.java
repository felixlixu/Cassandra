package org.apache.cassandra.auth;

import java.util.EnumSet;

public enum Permission {
	READ,
	WRITE;
	public static final EnumSet<Permission> ALL=EnumSet.allOf(Permission.class);
	public static final EnumSet<Permission> NONE=EnumSet.noneOf(Permission.class);
}
