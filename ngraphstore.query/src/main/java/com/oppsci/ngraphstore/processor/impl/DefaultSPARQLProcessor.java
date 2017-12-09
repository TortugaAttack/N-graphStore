package com.oppsci.ngraphstore.processor.impl;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import com.oppsci.ngraphstore.processor.SPARQLProcessor;
import com.oppsci.ngraphstore.storage.ClusterOverseer;
import com.oppsci.ngraphstore.storage.MemoryStorage;
import com.oppsci.ngraphstore.storage.lucene.LuceneConstants;
import com.oppsci.ngraphstore.storage.lucene.spec.LuceneSearchSpec;
import com.oppsci.ngraphstore.storage.lucene.spec.LuceneSpec;
import com.oppsci.ngraphstore.storage.lucene.spec.SearchStats;
import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

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
	
	@Autowired
	private ClusterOverseer overseer;
	
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

	@Override
	public JSONObject explore(String uri) throws Exception {
		JSONObject wrapper = new JSONObject();
		JSONArray exploreJSON = new JSONArray();
		String[][] exploreGraph = overseer.explore(uri);
		for(String[] quad : exploreGraph) {
			JSONObject jsonQuad = new JSONObject();
			jsonQuad.put("subject", quad[0]);
			jsonQuad.put("predicate", quad[1]);
			jsonQuad.put("object", quad[2]);
			jsonQuad.put("graph", quad[3]);
			exploreJSON.add(jsonQuad);
		}
		wrapper.put("graph", exploreJSON);
		return wrapper;
	}

}
