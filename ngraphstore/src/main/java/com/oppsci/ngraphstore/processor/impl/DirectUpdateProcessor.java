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
	public boolean insert(String triples) {
		try {
			return clusterOverseer.add(TripleFactory.parseTriples(triples));
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public boolean delete(String triples) {
		try {
			return clusterOverseer.delete(TripleFactory.parseTriples(triples));
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public boolean load(String triples) {
		try {
			return clusterOverseer.load(TripleFactory.parseTriples(triples));
		} catch (IOException e) {
			return false;
		}
	}

}
