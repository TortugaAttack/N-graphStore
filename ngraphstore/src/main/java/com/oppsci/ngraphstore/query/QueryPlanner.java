package com.oppsci.ngraphstore.query;

import com.oppsci.ngraphstore.results.SimpleResultSet;

public interface QueryPlanner {

	
	
	SimpleResultSet select(Query query);

}
