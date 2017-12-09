package com.oppsci.ngraphstore.query.planner;

import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Model;

import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

public interface QueryPlanner {

	
	
	public SimpleResultSet select(Query query) throws Exception;

	public boolean ask(Query query) throws Exception;
	
	public Model construct(Query query) throws Exception;
	
	public Model describe(Query query) throws Exception;
	
}
