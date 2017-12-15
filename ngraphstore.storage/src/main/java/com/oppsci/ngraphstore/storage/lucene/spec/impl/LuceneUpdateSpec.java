package com.oppsci.ngraphstore.storage.lucene.spec.impl;

import java.util.LinkedList;
import java.util.List;

import com.oppsci.ngraphstore.graph.Triple;
import com.oppsci.ngraphstore.storage.lucene.spec.LuceneSpec;

public class LuceneUpdateSpec implements LuceneSpec {

	private String graph;
	private List<Triple<String>> triples = new LinkedList<Triple<String>>();

	public LuceneUpdateSpec(Triple<String>[] triples) {
		this.triples = new LinkedList<Triple<String>>();
		for(Triple<String> triple : triples) {
			this.triples.add(triple);
		}

	}

	public LuceneUpdateSpec(String graph) {
		this.graph = graph;
	}

	public LuceneUpdateSpec() {
	}

	/**
	 * @return the graph
	 */
	public String getGraph() {
		return graph;
	}

	/**
	 * @param graph
	 *            the graph to set
	 */
	public void setGraph(String graph) {
		this.graph = graph;
	}

	/**
	 * @return the triples
	 */
	public List<Triple<String>> getTriples() {
		return triples;
	}

	/**
	 * @param triples
	 *            the triples to set
	 */
	public void setTriples(Triple<String>[] triples) {
		this.triples = new LinkedList<Triple<String>>();
		for(Triple<String> triple : triples) {
			this.triples.add(triple);
		}
	}

	
	/**
	 * @param triples
	 *            the triples to set
	 */
	public void setTriples(List<Triple<String>> triples) {
		this.triples = triples;
	}
}
