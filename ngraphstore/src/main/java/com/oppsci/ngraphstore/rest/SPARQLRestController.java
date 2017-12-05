package com.oppsci.ngraphstore.rest;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.oppsci.ngraphstore.processor.SPARQLProcessor;

/**
 * SPARQL REST Controller. Will get the SPARQL query out of a GET Request and
 * forwards it to a provided SPARQLProcessor
 * 
 * 
 * 
 * @author f.conrads
 *
 */
public class SPARQLRestController {

	private SPARQLProcessor processor;

	/**
	 * Creates a SPARQLRestController using the provided SPARQLProcessor
	 * 
	 * @param processor
	 */
	public SPARQLRestController() {
	}

	/**
	 * Creates a SPARQLRestController using the provided SPARQLProcessor
	 * 
	 * @param processor
	 */
	public SPARQLRestController(SPARQLProcessor processor) {
		this.processor = processor;
	}

	/**
	 * @return the processor
	 */
	public SPARQLProcessor getProcessor() {
		return processor;
	}

	/**
	 * @param processor
	 *            the processor to set
	 */
	public void setProcessor(SPARQLProcessor processor) {
		this.processor = processor;
	}

	/**
	 * Will process the given sparql query and send the results in a JSON object
	 * 
	 * @param query
	 * @return
	 */
	public JSONObject processQuery(String query) {

		return processor.select(query);
	}

}
