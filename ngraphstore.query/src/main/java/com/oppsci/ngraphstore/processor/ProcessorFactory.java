package com.oppsci.ngraphstore.processor;

import com.oppsci.ngraphstore.processor.impl.DefaultSPARQLProcessor;
import com.oppsci.ngraphstore.storage.cluster.overseer.ClusterOverseer;
import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

/**
 * Factory for creating SPARQLProcessors
 * 
 * @author f.conrads
 *
 */
public class ProcessorFactory {

	/**
	 * Creates the Default SPARQL Processor
	 * 
	 * @param clusterOverseer
	 * 
	 * @return the DefaultSPARQLProcessor
	 */
	public static SPARQLProcessor createDefaultProcessor(ClusterOverseer<SimpleResultSet> clusterOverseer) {
		return new DefaultSPARQLProcessor(clusterOverseer);
	}

}
