package com.oppsci.ngraphstore.query.planner;

import com.oppsci.ngraphstore.graph.Graph;
import com.oppsci.ngraphstore.query.parser.Query;
import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

public interface QueryPlanner {

	
	
	public SimpleResultSet select(Query query) throws Exception;

	public boolean ask(Query query) throws Exception;
	
	public Graph construct(Query query) throws Exception;
	
	public Graph describe(Query query) throws Exception;
	
}
