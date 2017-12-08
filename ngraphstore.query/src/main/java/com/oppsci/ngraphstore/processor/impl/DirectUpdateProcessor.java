package com.oppsci.ngraphstore.processor.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import com.oppsci.ngraphstore.graph.TripleFactory;
import com.oppsci.ngraphstore.processor.UpdateProcessor;
import com.oppsci.ngraphstore.storage.ClusterOverseer;

public class DirectUpdateProcessor implements UpdateProcessor {

	@Autowired
	ClusterOverseer clusterOverseer;

	@Override
	public boolean insert(String triples, String graph) {
		try {
			return clusterOverseer.add(TripleFactory.parseTriples(triples), graph);
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public boolean delete(String triples, String graph) {
		try {
			return clusterOverseer.delete(TripleFactory.parseTriples(triples), graph);
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public boolean load(String triples, String graph) {
		try {
			return clusterOverseer.load(TripleFactory.parseTriples(triples), graph);
		} catch (IOException e) {
			return false;
		}
	}

}
