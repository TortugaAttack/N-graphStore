package com.oppsci.ngraphstore.processor.impl;

import org.apache.jena.query.ResultSet;
import org.springframework.beans.factory.annotation.Autowired;

import com.oppsci.ngraphstore.processor.SPARQLProcessor;

import con.oppsci.ngraphstore.storage.MemoryStorage;

/**
 * The Default SPARQLProcessor. <br/>
 * 
 * TODO: more information
 * 
 * @author f.conrads
 *
 */
public class DefaultSPARQLProcessor implements SPARQLProcessor {

	//currently only a test using Jena 
	@Autowired
	private MemoryStorage storage;
	
	public ResultSet select(String query) {
		return storage.select(query);
		
	}

}
