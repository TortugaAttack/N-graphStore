package com.oppsci.ngraphstore.query.planner;

import com.oppsci.ngraphstore.query.parser.Query;
import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

public interface QueryPlanner {

	
	
	SimpleResultSet select(Query query) throws Exception;

}
