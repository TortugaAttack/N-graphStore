package com.oppsci.ngraphstore.web.rest.rdf;

/**
 * The Controller for SPARQL Update queries.
 * 
 * @author f.conrads
 *
 */
public class UpdateRestController {

	/**
	 * Processes an update query either using a POST or PUT method
	 * 
	 * @param query the SPARQL update query
	 * @param isPost true if POST, false if PUT
	 * @return true if succeeded, otherwise false
	 */
	public Boolean processUpdate(String query, boolean isPost) {
		// TODO Auto-generated method stub
		
		//TODO do not forget to set graph in query if no default is
		return null;
	}

}
