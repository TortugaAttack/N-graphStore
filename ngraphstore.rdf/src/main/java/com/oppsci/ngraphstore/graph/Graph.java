package com.oppsci.ngraphstore.graph;

import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONObject;

public class Graph {

	private List<Triple> statements = new LinkedList<Triple>();
	
	public JSONObject asJSON() {
		// TODO create JSON-LD string of statements
		return null;
	}

	public void addTriple(Triple triple) {
		statements.add(triple);
	}
	
	
	public void addTriples(List<Triple> triples) {
		statements.addAll(triples);
	}
	
	
	public List<Triple> getAllTriples(){
		return statements;
	}
}
