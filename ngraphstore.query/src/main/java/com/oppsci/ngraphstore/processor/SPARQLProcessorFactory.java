package com.oppsci.ngraphstore.processor;

import com.oppsci.ngraphstore.processor.impl.DefaultSPARQLProcessor;

/**
 * Factory for creating SPARQLProcessors
 * 
 * @author f.conrads
 *
 */
public class SPARQLProcessorFactory {

	/**
	 * Creates the Default SPARQL Processor
	 * 
	 * @return the DefaultSPARQLProcessor
	 */
	public static SPARQLProcessor createDefaultProcessor() {
		return new DefaultSPARQLProcessor();
	}

}
