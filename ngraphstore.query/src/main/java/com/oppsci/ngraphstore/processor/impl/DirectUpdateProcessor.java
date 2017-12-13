package com.oppsci.ngraphstore.processor.impl;

import java.io.IOException;


import com.oppsci.ngraphstore.graph.TripleFactory;
import com.oppsci.ngraphstore.processor.UpdateProcessor;
import com.oppsci.ngraphstore.storage.cluster.overseer.ClusterOverseer;
import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

/**
 * Processor for direct updates. (E.g. loading an RDF File, direct changes in the database...)
 * 
 * @author f.conrads
 *
 */
public class DirectUpdateProcessor implements UpdateProcessor {

	
	private ClusterOverseer<SimpleResultSet> clusterOverseer;
	 
	/**
	 * Creates the DirectUpdateProcessor using the clusterOverseer for the updates
	 * 
	 * @param clusterOverseer
	 */
	public DirectUpdateProcessor(ClusterOverseer<SimpleResultSet> clusterOverseer) {
		this.clusterOverseer = clusterOverseer;
	}
	

	@Override
	public boolean insert(String triples, String graph) throws Exception {
		try {
			return clusterOverseer.add(TripleFactory.parseTriples(triples, graph));
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public boolean delete(String triples, String graph) throws Exception {
		try {
			return clusterOverseer.delete(TripleFactory.parseTriples(triples, graph));
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public boolean load(String triples, String graph) throws Exception {
		try {
			return clusterOverseer.load(TripleFactory.parseTriples(triples, graph));
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public void quadUpdate(String[] oldTerms, String[] newTerms) throws Exception {
		clusterOverseer.quadUpdate(oldTerms, newTerms);
	}

}
