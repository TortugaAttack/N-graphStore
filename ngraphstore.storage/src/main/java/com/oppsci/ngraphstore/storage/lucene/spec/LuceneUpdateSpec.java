package com.oppsci.ngraphstore.storage.lucene.spec;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.oppsci.ngraphstore.graph.Triple;

public class LuceneUpdateSpec implements LuceneSpec {

	private String graph;
	private Triple<String>[] triples;

	public LuceneUpdateSpec(String graph, Triple<String>[] triples) {
		this.graph = graph;
		this.triples = triples;

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
		return new LinkedList<Triple<String>>(Arrays.asList(triples));
	}

	/**
	 * @param triples
	 *            the triples to set
	 */
	public void setTriples(Triple<String>[] triples) {
		this.triples = triples;
	}

}
