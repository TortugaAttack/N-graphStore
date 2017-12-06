package com.oppsci.ngraphstore.query;

import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

public interface QueryPlanner {

	
	
	SimpleResultSet select(Query query);

}
