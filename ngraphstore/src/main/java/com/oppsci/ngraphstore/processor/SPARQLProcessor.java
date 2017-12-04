package com.oppsci.ngraphstore.processor;

import org.apache.jena.query.ResultSet;

/**
 * Provides an interface to process SPARQL Queries
 * 
 * @author f.conrads
 *
 */
public interface SPARQLProcessor {

	ResultSet select(String query);


	
}
