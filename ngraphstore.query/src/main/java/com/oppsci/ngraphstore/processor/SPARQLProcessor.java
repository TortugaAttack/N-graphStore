package com.oppsci.ngraphstore.processor;

import org.apache.jena.query.ResultSet;
import org.json.simple.JSONObject;

/**
 * Provides an interface to process SPARQL Queries
 * 
 * @author f.conrads
 *
 */
public interface SPARQLProcessor {

	public JSONObject select(String query);

	public JSONObject explore(String uri) throws Exception;


	
}
