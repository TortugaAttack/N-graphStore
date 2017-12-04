package com.oppsci.ngraphstore.rest;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Forwards REST query further to correct REST Controller
 * <ul>
 * <li>GET will be forwarded to SPARQL</li>
 * </ul>
 * 
 * @author f.conrads
 *
 */
@RestController
@RequestMapping(value = "/")
public class QueryRestController {
	
	@Autowired
	SPARQLRestController sparqlRestController;
	@Autowired
	UpdateRestController updateRestController;
	@Autowired
	TripleRestController tripleRestController;
	
	
	
	/**
	 * 
	 * SPARQL rest service using query as parameter
	 * 
	 * @param query
	 * @return Results as JSON String
	 */
	@RequestMapping(value = "/sparql", method = RequestMethod.GET, headers = "Accept=application/json")
	public String getSPARQLresults(@RequestParam(value="query") String query) {
		JSONObject results =  sparqlRestController.processQuery(query);
		return results.toJSONString();
	}
	
	/**
	 * POST method for SPARQL update queries. <br/>
	 * Will add data (f.e. INSERT)
	 * 
	 * @param query
	 * @return true if succeeded, otherwise false
	 */
	@RequestMapping(value = {"/update", "/sparql"}, method = RequestMethod.POST, headers = "Accept=text/plain")
	public Boolean postUpdateQuery(@RequestParam(value="update") String query) {
		Boolean updateSucceeded =  updateRestController.processUpdate(query, true);
		return updateSucceeded;
	}
	
	/**
	 * PUT method for SPARQL update queries <br/>
	 * Will exchange data (f.e. DROP)
	 * 
	 * @param query
	 * @return true if succeeded, otherwise false
	 */
	@RequestMapping(value = {"/update", "/sparql"}, method = RequestMethod.PUT, headers = "Accept=text/plain")
	public Boolean putUpdateQuery(@RequestParam(value="update") String query) {
		Boolean updateSucceeded =  updateRestController.processUpdate(query, false);
		return updateSucceeded;
	}
	
	/**
	 * 
	 * POST method for loading direct triples  <br/>
	 * Will add data
	 * 
	 * @param data
	 * @return true if succeeded, false otherwise
	 */
	@RequestMapping(value = "/data", method = RequestMethod.POST, headers = "Accept=text/plain")
	public Boolean postTriples(@RequestParam(value="data") String data) {
		Boolean updateSucceeded =  tripleRestController.processTriple(data, true);
		return updateSucceeded;
	}
	
	/**
	 * 
	 * PUT method for loading direct triples  <br/>
	 * Will exchange data
	 * 
	 * @param data
	 * @return true if succeeded, false otherwise
	 */
	@RequestMapping(value = "/data", method = RequestMethod.PUT, headers = "Accept=text/plain")
	public Boolean putTriples(@RequestParam(value="data") String data) {
		Boolean updateSucceeded =  tripleRestController.processTriple(data, false);
		return updateSucceeded;
	}
}
