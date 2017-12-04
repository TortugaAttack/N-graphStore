package com.oppsci.ngraphstore.root;

import java.io.File;
import java.net.MalformedURLException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.oppsci.ngraphstore.processor.SPARQLProcessor;
import com.oppsci.ngraphstore.processor.SPARQLProcessorFactory;
import com.oppsci.ngraphstore.rest.SPARQLRestController;
import com.oppsci.ngraphstore.rest.TripleRestController;
import com.oppsci.ngraphstore.rest.UpdateRestController;

import con.oppsci.ngraphstore.storage.MemoryStorage;

/**
 * Controller for autowired elememnts
 * 
 * creates the Elements according to configuration file
 * 
 * @author f.conrads
 *
 */
@Configuration
@ComponentScan(basePackages = {"com.oppsci.ngraphstore.root", "com.oppsci.ngraphstore.rest"})
public class RootController {
	
	/**
	 * Creates the default sparql processor to use
	 * @return a DefaultSPARQLProcessor object
	 */
	public static @Bean SPARQLProcessor createSPARQLProcessor() {
		SPARQLProcessor processor = SPARQLProcessorFactory.createDefaultProcessor();
		return processor;
	}
	
	public static @Bean MemoryStorage createMemoryStorage() throws MalformedURLException {
		File f = new File("data.nt");
		String ntFile = f.toURI().toURL().toString();
		MemoryStorage mem = new MemoryStorage(ntFile);
		return mem;
	}
	
	/**
	 * Creates a SAPRQLRestController using the specified processor
	 * @param processor the SPARQLProcessor to use
	 * @return a SPARQLRestController Object
	 */
	public static @Bean SPARQLRestController createSPARQLRestController(SPARQLProcessor processor) {
		SPARQLRestController sparqlRestController = new SPARQLRestController(processor);
		return sparqlRestController;
	}
	
	/**
	 * Creates a UpdateRestController
	 * 
	 * @return
	 */
	public static @Bean UpdateRestController createUpdateRestController() {
		UpdateRestController updateRestController = new UpdateRestController();
		return updateRestController;
	}
	
	/**
	 * Creates a TripleRestController
	 * @return
	 */
	public static @Bean TripleRestController tripleUpdateRestController() {
		TripleRestController tripleRestController = new TripleRestController();
		return tripleRestController;
	}

}
