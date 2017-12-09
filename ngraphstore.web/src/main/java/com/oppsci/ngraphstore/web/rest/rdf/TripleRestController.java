package com.oppsci.ngraphstore.web.rest.rdf;

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
	private UpdateProcessor directProcessor;
	
	/**
	 * Processes triples either using a PUT method
	 * 
	 * @param data the triple data
	 * @return true if succeeded, otherwise false
	 * @throws Exception 
	 */
	public Boolean processTriple(String data, String graphURI) throws Exception {
			return directProcessor.load(data, graphURI);
		
	}

	public Boolean processTriple(String triple, String method, String graphURI) throws Exception {
		if("insert".equals(method)) {
			directProcessor.insert(triple, graphURI);
		}		
		else if("delete".equals(method)) {
			directProcessor.delete(triple, graphURI);
		}
		return null;
	}

}
