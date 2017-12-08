package com.oppsci.ngraphstore.processor.impl;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import com.oppsci.ngraphstore.processor.SPARQLProcessor;
import com.oppsci.ngraphstore.storage.MemoryStorage;

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
	
	public JSONObject select(String query) {
		ResultSet set = storage.select(query);
		JSONObject results = new JSONObject();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ResultSetFormatter.outputAsJSON(os, set);
		try {
			String jsonString = new String(os.toByteArray(),"UTF-8");
			JSONParser parser = new JSONParser();
			results = (JSONObject) parser.parse(jsonString);
		} catch (UnsupportedEncodingException | ParseException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return results;
	}

}
