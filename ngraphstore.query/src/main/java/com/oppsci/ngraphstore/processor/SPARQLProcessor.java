package com.oppsci.ngraphstore.processor;

import org.json.simple.JSONObject;

/**
 * Provides an interface to process SPARQL Queries
 * 
 * @author f.conrads
 *
 */
public interface SPARQLProcessor {

	public JSONObject select(String query) throws Exception;
	
	public JSONObject ask(String query) throws Exception;
	
	public JSONObject construct(String query) throws Exception;
	public JSONObject describe (String query) throws Exception;

	public JSONObject explore(String uri) throws Exception;


	
}
