package com.oppsci.ngraphstore.web.rest.db;

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
@RequestMapping(value = "/api")
public class ManagmentRestController {
	
	@RequestMapping(value = "/auth/user", method = RequestMethod.GET, headers = "Accept=application/json")
	public String getSPARQLresults(@RequestParam(value = "query") String query) {
		return "true";
	}

}
