package org.apache.cassandra.db.filter;

import org.apache.cassandra.db.DecoratedKey;
import org.apache.cassandra.db.superColumn.IdentityQueryFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryFilter {

	private static final Logger logger = LoggerFactory.getLogger(QueryFilter.class);
	private DecoratedKey<?> key;
	private QueryPath path;
	private IFilter filter;
	private IFilter superFilter;
	
	public QueryFilter(DecoratedKey decoratedKey, QueryPath queryPath,
			IdentityQueryFilter identityQueryFilter) {
		this.key=decoratedKey;
		this.path=queryPath;
		this.filter=identityQueryFilter;
		superFilter=path.superColumnName==null?null:new NamesQueryFilter(queryPath.superColumnName);
	}

	public static QueryFilter getIdentityFilter(DecoratedKey decoratedKey,
			QueryPath queryPath) {
		return new QueryFilter(decoratedKey,queryPath, new IdentityQueryFilter());
	}

}
