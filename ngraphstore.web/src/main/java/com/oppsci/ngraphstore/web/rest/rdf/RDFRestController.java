package com.oppsci.ngraphstore.web.rest.rdf;

import org.apache.commons.configuration.CompositeConfiguration;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
@RequestMapping(value = "/api")
public class RDFRestController {
	
	private static final String DEFAULT_GRAPH = "ngraphstore.rdf.sparql.defaultGraph";
	@Autowired
	private SPARQLRestController sparqlRestController;
	@Autowired
	private UpdateRestController updateRestController;
	@Autowired
	private TripleRestController tripleRestController;
	
	@Autowired
	private CompositeConfiguration config;
	
	
	/**
	 * 
	 * SPARQL rest service using query as parameter
	 * 
	 * @param query
	 * @return Results as JSON String
	 * @throws Exception 
	 */
	@RequestMapping(value = "/sparql", method = RequestMethod.GET, headers = "Accept=application/json")
	public byte[] getSPARQLresults(@RequestParam(value="query") String query) throws Exception {
		JSONObject results =  sparqlRestController.processQuery(query);
		return results.toJSONString().replace("\\/", "/").replace("\\\\\\\"", "\\\"").getBytes("UTF-8");
	}
	
	
	@RequestMapping(value = "/explore", method = RequestMethod.GET, headers = "Accept=application/json")
	public String getExploreResults(@RequestParam(value="uri") String uri) throws Exception {
		JSONObject results =  sparqlRestController.explore(uri);
		return results.toJSONString();
	}
	
	@RequestMapping(value = "/auth/directupdate", method = RequestMethod.POST, headers = "Accept=application/json")
	public String directUpdate(@RequestParam(value="jsonUpdate") String jsonUpdate) throws Exception {
		JSONParser jsonParser = new JSONParser();
		return updateRestController.exchangeTriples((JSONObject)jsonParser.parse(jsonUpdate));
		
	}

	@RequestMapping(value = "/auth/exchange", method = RequestMethod.POST, headers = "Accept=application/json")
	public String exchangeData(@RequestParam String old, @RequestParam String newTriples) throws Exception {
		JSONParser jsonParser = new JSONParser();
		//TODO create json
		return updateRestController.exchangeTriples((JSONObject)jsonParser.parse(old), (JSONObject)jsonParser.parse(newTriples));
		
	}
	
	/**
	 * POST method for SPARQL update queries. <br/>
	 * Will add data (f.e. INSERT)
	 * 
	 * @param query
	 * @return true if succeeded, otherwise false
	 */
	@RequestMapping(value = {"/auth/update"}, method = RequestMethod.POST, headers = "Accept=text/plain")
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
	@RequestMapping(value = {"/auth/update"}, method = RequestMethod.PUT, headers = "Accept=text/plain")
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
	 * @param method 
	 * @return true if succeeded, false otherwise
	 * @throws Exception 
	 */
	@RequestMapping(value = "/auth/data", method = RequestMethod.POST, headers = "Accept=text/plain")
	public Boolean postTriples(@RequestParam(value="data") String data, @RequestParam(value="graph", required = false, defaultValue="") String graph, @RequestParam(value="method") String method) throws Exception {
		
		String graphURI =graph;
		if(graphURI.isEmpty()) {
			graphURI = config.getString(DEFAULT_GRAPH);
		}
		Boolean updateSucceeded =  tripleRestController.processTriple(data, method, graphURI);
		return updateSucceeded;
	}
	
	/**
	 * 
	 * PUT method for loading direct triples  <br/>
	 * Will exchange data
	 * 
	 * @param data
	 * @return true if succeeded, false otherwise
	 * @throws Exception 
	 */
	@RequestMapping(value = "/auth/data", method = RequestMethod.PUT, headers = "Accept=text/plain")
	public Boolean putTriples(@RequestParam(value="data") String data, @RequestParam(value="graph", required = false, defaultValue="") String graph) throws Exception {
		String graphURI =graph;
		if(graphURI.isEmpty()) {
			graphURI = config.getString(DEFAULT_GRAPH);
		}
		Boolean updateSucceeded =  tripleRestController.processTriple(data, graphURI);
		return updateSucceeded;
	}
}
