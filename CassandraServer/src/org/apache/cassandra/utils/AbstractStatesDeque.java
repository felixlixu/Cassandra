package org.apache.cassandra.utils;

import java.util.Iterator;

public abstract class AbstractStatesDeque implements Iterable<Double> {

	@Override
	public abstract Iterator<Double> iterator();
}
