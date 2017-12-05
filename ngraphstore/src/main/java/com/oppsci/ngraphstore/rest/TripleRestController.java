package com.oppsci.ngraphstore.rest;

import org.springframework.beans.factory.annotation.Autowired;

import com.oppsci.ngraphstore.processor.UpdateProcessor;

/**
 * The Controller for handling uploaded triple data.
 * 
 * @author f.conrads
 *
 */
public class TripleRestController {

	@Autowired
	UpdateProcessor directProcessor;
	
	/**
	 * Processes triples either using a PUT method
	 * 
	 * @param data the triple data
	 * @return true if succeeded, otherwise false
	 */
	public Boolean processTriple(String data) {
			return directProcessor.load(data);
		
	}

	public Boolean processTriple(String triple, String method) {
		if(method.equals("insert")) {
			directProcessor.insert(triple);
		}		
		else if(method.equals("delete")) {
			directProcessor.delete(triple);
		}
		return null;
	}

}
