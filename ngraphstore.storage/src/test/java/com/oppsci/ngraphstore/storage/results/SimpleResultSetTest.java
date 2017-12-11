package com.oppsci.ngraphstore.storage.results;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import com.oppsci.ngraphstore.graph.elements.Node;
import com.oppsci.ngraphstore.graph.elements.impl.BlankNode;
import com.oppsci.ngraphstore.graph.elements.impl.URINode;
import com.oppsci.ngraphstore.storage.lucene.spec.SearchStats;

public class SimpleResultSetTest {

	@Test
	public void emptyJsonTest() {
		SimpleResultSet res = new SimpleResultSet();
		JSONObject results = (JSONObject)res.asJSON();
		JSONArray head = ((JSONArray)results.get("head"));
		assertTrue(head.isEmpty());
		JSONArray bindings = ((JSONArray)results.get("results"));
		assertTrue(bindings.isEmpty());
	}
	
	@Test
	public void jsonTest() {
		SimpleResultSet res = new SimpleResultSet();
		//add head
		LinkedList<String> vars = new LinkedList<String>();
		vars.add("s");
		vars.add("p");
		res.setVars(vars);
		
		//add results
		LinkedList<Node[]> rows = new LinkedList<Node[]>();
		Node[] results1 = new Node[2];
		results1[0] = new URINode("<urn://node>");
		results1[1] = new BlankNode("_:b1");
		Node[] results2 = new Node[2];
		results2[0] = new URINode("<urn://node>");
		results2[1] = new BlankNode("\"literal\"@en");
		rows.add(results1);
		rows.add(results2);
		res.addRows(rows);
		
		JSONObject results = (JSONObject)res.asJSON();
		JSONArray head = ((JSONArray)results.get("head"));
		assertEquals("s", head.get(0));
		assertEquals("p", head.get(1));
		JSONArray bindings = ((JSONArray)results.get("results"));
		assertFalse(bindings.isEmpty());
	}
	
	
	@Test
	public void simpleTest() {
		//add head
		LinkedList<String> vars = new LinkedList<String>();
		vars.add("s");
		vars.add("p");
		
		//add results
		LinkedList<Node[]> rows = new LinkedList<Node[]>();
		Node[] results1 = new Node[2];
		results1[0] = new URINode("<urn://node>");
		results1[1] = new BlankNode("_:b1");
		Node[] results2 = new Node[2];
		results2[0] = new URINode("<urn://node>");
		results2[1] = new BlankNode("\"literal\"@en");
		rows.add(results1);
		rows.add(results2);
		SimpleResultSet res = new SimpleResultSet(vars, rows);
		SearchStats stats = new SearchStats();
		stats.setLastHit(100);
		res.setStats(stats);
		assertEquals(stats, res.getStats());
		assertEquals(rows, res.getRows());
		assertEquals(vars, res.getVars());
		LinkedList<Node[]> emptyRows = new LinkedList<Node[]>();
		res.setRows(emptyRows);
		assertEquals(emptyRows, res.getRows());
	}
}
