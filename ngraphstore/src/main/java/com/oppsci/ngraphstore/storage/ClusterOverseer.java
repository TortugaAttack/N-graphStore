package com.oppsci.ngraphstore.storage;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.oppsci.ngraphstore.query.Query;
import com.oppsci.ngraphstore.query.QueryParser;
import com.oppsci.ngraphstore.results.SimpleResultSet;
import com.oppsci.ngraphstore.storage.lucene.LuceneSearcher;

/**
 * 
 * The overseer of the n cluster. will execute a provided query on N Cluster
 * 
 * @author f.conrads
 *
 */
public class ClusterOverseer {

	private long timeout = 180;
	private int clusterSize;
	private LuceneSearcher[] searcher;
	
	public ClusterOverseer(int clusterSize, String rootFolder, long timeout) throws IOException {
		this.clusterSize=clusterSize;
		this.timeout=timeout;
		searcher = new LuceneSearcher[clusterSize];
		for(int i=0;i<clusterSize;i++) {
			//create LuceneSearcher at rootFolder/0/ ... rootFolder/N/
			searcher[i] = new LuceneSearcher(rootFolder+File.separator+i);
		}
	}
	
	private SimpleResultSet mergeSyncedResults(SimpleResultSet[] results) {
		SimpleResultSet merged = new SimpleResultSet();
		if(results.length>0) {
			//as they are synchronized this is okay
			merged.setVars(results[0].getVars());
		}
		for(SimpleResultSet srs : results) {
			merged.addRows(srs.getRows());
		}
		return merged;
	}
	
	/**
	 * This will execute N Cluster as threads and proides 
	 * @param queryString
	 * @return
	 * @throws Exception
	 */
	public SimpleResultSet search(LuceneSpec spec) throws Exception {
		List<Future<SimpleResultSet>> futures = new LinkedList<Future<SimpleResultSet>>();
		SimpleResultSet[] results = new SimpleResultSet[clusterSize];
		
		//create executorservice for threading
		ExecutorService service = Executors.newFixedThreadPool(clusterSize);
		
		//put query into each cluster using the according lucenesearcher
		for(int i=0;i<clusterSize;i++) {
			futures.add(service.submit(new Cluster(spec, searcher[i])));
		}
		//shutdown and await termination of threads
		service.shutdown();
		service.awaitTermination(timeout, TimeUnit.SECONDS);

		//start cluster as thread using pool
		for(int i=0;i<clusterSize;i++) {
			results[i] = futures.get(i).get();
		}
		//sync results and return
		return mergeSyncedResults(results);
	}
	
}
