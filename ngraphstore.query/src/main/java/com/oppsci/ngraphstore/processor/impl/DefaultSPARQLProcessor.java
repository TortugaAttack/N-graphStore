package com.oppsci.ngraphstore.processor.impl;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.oppsci.ngraphstore.processor.SPARQLProcessor;
import com.oppsci.ngraphstore.query.parser.Query;
import com.oppsci.ngraphstore.query.parser.impl.QueryParserImpl;
import com.oppsci.ngraphstore.query.planner.QueryPlanner;
import com.oppsci.ngraphstore.storage.cluster.overseer.ClusterOverseer;
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

	private ClusterOverseer<SimpleResultSet> overseer;

	private QueryPlanner planner;
	private QueryParserImpl parser;

	/**
	 * Creates the Default Sparql Processor. <br/>
	 * Sets the overseer to be used for explore queries.
	 * 
	 * @param overseer
	 */
	public DefaultSPARQLProcessor(ClusterOverseer<SimpleResultSet> overseer) {
		this.overseer = overseer;
	}

	public JSONObject select(String queryString) throws Exception {
		Query query = parser.parse(queryString);
		return planner.select(query).asJSON();
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject explore(String uri) throws Exception {
		JSONObject wrapper = new JSONObject();
		JSONArray exploreJSON = new JSONArray();
		String[][] exploreGraph = overseer.explore(uri);
		for (String[] quad : exploreGraph) {
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

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject ask(String queryString) throws Exception {
		Query query = parser.parse(queryString);

		boolean askResult = planner.ask(query);
		JSONObject result = new JSONObject();
		result.put("boolean", askResult);
		// header can be an empty json object
		result.put("header", new JSONObject());

		return result;
	}

	@Override
	public JSONObject construct(String queryString) throws Exception {
		Query query = parser.parse(queryString);
		return planner.construct(query).asJSON();
	}

	@Override
	public JSONObject describe(String queryString) throws Exception {
		Query query = parser.parse(queryString);
		return planner.describe(query).asJSON();
	}

}
