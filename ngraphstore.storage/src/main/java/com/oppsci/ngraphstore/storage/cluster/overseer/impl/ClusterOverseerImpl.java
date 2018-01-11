package com.oppsci.ngraphstore.storage.cluster.overseer.impl;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.oppsci.ngraphstore.graph.Triple;
import com.oppsci.ngraphstore.storage.cluster.Cluster;
import com.oppsci.ngraphstore.storage.cluster.overseer.ClusterOverseer;
import com.oppsci.ngraphstore.storage.lucene.LuceneConstants;
import com.oppsci.ngraphstore.storage.lucene.LuceneIndexer;
import com.oppsci.ngraphstore.storage.lucene.LuceneSearcher;
import com.oppsci.ngraphstore.storage.lucene.spec.LuceneSpec;
import com.oppsci.ngraphstore.storage.lucene.spec.SearchStats;
import com.oppsci.ngraphstore.storage.lucene.spec.impl.LuceneQuadUpdateSpec;
import com.oppsci.ngraphstore.storage.lucene.spec.impl.LuceneSearchSpec;
import com.oppsci.ngraphstore.storage.lucene.spec.impl.LuceneUpdateSpec;
import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

/**
 * 
 * @author f.conrads
 *
 */
public class ClusterOverseerImpl implements ClusterOverseer<SimpleResultSet> {

	private long timeout = 180;
	private int clusterSize;
	private LuceneSearcher[] searcher;
	private LuceneIndexer[] indexer;
	private boolean ignoreErrors;
	private int maxSearch = LuceneConstants.MAX_SEARCH;
	
	public ClusterOverseerImpl(String rootFolder, int clusterSize, long timeout, boolean ignoreErrors) throws IOException {
		this(rootFolder, clusterSize, timeout, LuceneConstants.MAX_SEARCH, ignoreErrors);
	}
	
	public ClusterOverseerImpl(String rootFolder, int clusterSize, long timeout, int maxSearch, boolean ignoreErrors) throws IOException {
		this.timeout = timeout;
		this.maxSearch=maxSearch;
		this.clusterSize=clusterSize;
		this.indexer = createIndexerOnTheFly(rootFolder);
		closeIndexer(this.indexer);
		this.searcher = createSearcherOnTheFly(rootFolder);
		closeSearcher(this.searcher);
		this.ignoreErrors=ignoreErrors;

	}

	public SimpleResultSet mergeSyncedResults(SimpleResultSet[] results) {
		SimpleResultSet merged = new SimpleResultSet();
		if (results.length > 0) {
			// as they are synchronized this is okay
			merged.setVars(results[0].getVars());
		}
		for (SimpleResultSet srs : results) {
			merged.addRows(srs.getRows());
		}
		return merged;
	}

	/**
	 * This will execute N Cluster as threads and proides
	 * 
	 * @param queryString
	 * @return
	 * @throws Exception
	 */
	@Override
	public SimpleResultSet search(LuceneSpec spec, SearchStats[] stats) throws Exception {
		reopenSearcher();
		SimpleResultSet[] results = execute(new LuceneSpec[] {spec}, Cluster.SEARCH_METHOD, SimpleResultSet.class, stats)
				.toArray(new SimpleResultSet[] {});
		// sync results and return
		closeSearcher();
		return mergeSyncedResults(results);
	}
	
	@Override
	public SimpleResultSet searchAll(LuceneSpec spec, SearchStats[] stats) throws Exception {
		reopenSearcher();
		SimpleResultSet[] results = execute(new LuceneSpec[] {spec}, Cluster.SEARCH_ALL_METHOD, SimpleResultSet.class, stats)
				.toArray(new SimpleResultSet[] {});
		// sync results and return
		closeSearcher();
		return mergeSyncedResults(results);
	}

	/**
	 * Adds triples to index cluster
	 * 
	 * @param triples
	 * @param graph 
	 * @return
	 * @throws Exception 
	 */
	@Override
	public boolean add(Triple<String>[] triples) throws Exception {
		reopenIndexer();
		//TODO each cluster his own spec with only the first triples/N triples
		LuceneSpec spec = new LuceneUpdateSpec(triples);
		Boolean[] success = new Boolean[] {false};
		try {
			success = execute(createAddSpecs(triples), Cluster.INSERT_METHOD, boolean.class,null)
					.toArray(new Boolean[] {});
		} catch (InterruptedException | ExecutionException | TimeoutException e1) {
			e1.printStackTrace();
			throw e1;
		}
		for(boolean singleSuccess : success) {
			if(!singleSuccess) {
				try {
					rollback();
				} catch (IOException e) {
					e.printStackTrace();
					throw e;
				}
			}
		}
		closeIndexer();
		return true;
	}

	
	private LuceneUpdateSpec[] createAddSpecs(Triple<String>[] triples) {
		LuceneUpdateSpec[] specs = new LuceneUpdateSpec[clusterSize];
		int j = 0;
		for(int i=0;i<triples.length;i++) {
			if(specs[j]==null) {
				//init 
				specs[j] = new LuceneUpdateSpec();
			}
			specs[j++].getTriples().add(triples[i]);
			if(j>=clusterSize) {
				j=0;
			}
		}
		return specs;
	}
	
	/**
	 * Adds triples to index cluster
	 * 
	 * @param triples
	 * @param graph 
	 * @return
	 * @throws Exception 
	 */
	@Override
	public boolean load(Triple<String>[] triples) throws Exception {
		reopenIndexer();
		
		Boolean[] success = new Boolean[] {false};
		try {
			success = execute(createAddSpecs(triples), Cluster.LOAD_METHOD, boolean.class,null)
					.toArray(new Boolean[] {});
		} catch (InterruptedException | ExecutionException | TimeoutException e1) {
			//Logging purpose
			e1.printStackTrace();
			throw e1;
		}
		for(boolean singleSuccess : success) {
			if(!singleSuccess) {
				try {
					rollback();
				} catch (IOException e) {
					e.printStackTrace();
					throw e;
				}
			}
		}
		closeIndexer();
		return true;
	}

	@Override
	public boolean dropAll() throws Exception {
		reopenIndexer();
		LuceneSpec spec = new LuceneUpdateSpec();
		Boolean[] success = new Boolean[] {false};
		try {
			success = execute(new LuceneSpec[] {spec}, Cluster.DROP_ALL_METHOD, boolean.class, null)
					.toArray(new Boolean[] {});
		} catch (InterruptedException | ExecutionException | TimeoutException e1) {
			e1.printStackTrace();
			throw e1;
		}
		for(boolean singleSuccess : success) {
			if(!singleSuccess) {
				try {
					rollback();
				} catch (IOException e) {
					e.printStackTrace();
					throw e;
				}
			}
		}
		closeIndexer();

		return true;
	}

	@Override
	public boolean drop(String graph) throws Exception {
		// delete all triples with graph
		reopenIndexer();
		LuceneSpec spec = new LuceneUpdateSpec(graph);
		Boolean[] success = new Boolean[] {false};
		try {
			success = execute(new LuceneSpec[] {spec}, Cluster.DROP_METHOD, boolean.class, null)
					.toArray(new Boolean[] {});
		} catch (InterruptedException | ExecutionException | TimeoutException e1) {
			e1.printStackTrace();
			throw e1;
		}
		for(boolean singleSuccess : success) {
			if(!singleSuccess) {
				try {
					rollback();
				} catch (IOException e) {
					e.printStackTrace();
					throw e;
				}
			}
		}
		closeIndexer();
		return true;

	}

	@Override
	public boolean delete(Triple<String>[] triples) throws Exception {
		reopenIndexer();
		LuceneSpec spec = new LuceneUpdateSpec(triples);
		Boolean[] success = new Boolean[] {false};
		try {
			success = execute(new LuceneSpec[] {spec}, Cluster.DELETE_METHOD, boolean.class, null)
					.toArray(new Boolean[] {});
		} catch (InterruptedException | ExecutionException | TimeoutException e1) {
			e1.printStackTrace();
			throw e1;
		}
		for(boolean singleSuccess : success) {
			if(!singleSuccess) {
				try {
					rollback();
				} catch (IOException e) {
					e.printStackTrace();
					throw e;
				}
			}
		}
		closeIndexer();

		return true;
	}
	
	@Override
	public String[][] explore(String term) throws InterruptedException, ExecutionException, TimeoutException {
		reopenSearcher();
		LuceneSpec spec= new LuceneSearchSpec(new String[] {term}, null, null);
		String[][][] unmergedResults =  execute(new LuceneSpec[] {spec}, Cluster.EXPLORE_METHOD, String[][][].class, null)
				.toArray(new String[][][] {});
		String[][] mergedResults=mergeExploreResults(unmergedResults);
		closeSearcher();
		return mergedResults;
	}
	
	private String[][] mergeExploreResults(String[][][] unmerged){
		int len = 0;
		for(String[][] results : unmerged) {
			len+=results.length;
		}
		String[][] merged = new String[len][];
		int i=0;
		for(String[][] results : unmerged) {
			for(String[] result : results) {
				merged[i++] = result;
			}
		}
		return merged;
	}
	
	@Override
	public boolean quadUpdate(String[] oldTerms, String[] newTerms) throws Exception {
		LuceneSpec spec = new LuceneQuadUpdateSpec(oldTerms, newTerms);
		try {
			execute(new LuceneSpec[] {spec}, Cluster.QUAD_UPDATE, boolean.class, null);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
			throw e;
		}
		return true;
	}
	
	private void rollback() throws IOException {
		for(LuceneIndexer index : indexer) {
			index.rollback();
		}
	}
	

	private LuceneIndexer[] createIndexerOnTheFly(String dir) throws IOException {
		LuceneIndexer[] indexer = new LuceneIndexer[clusterSize];
		for (int i = 0; i < clusterSize; i++) {
			indexer[i] = new LuceneIndexer(dir + File.separator + i);
		}
		return indexer;
	}

	private LuceneSearcher[] createSearcherOnTheFly(String dir) throws IOException {
		LuceneSearcher[] searcher = new LuceneSearcher[clusterSize];
		for (int i = 0; i < clusterSize; i++) {
			searcher[i] = new LuceneSearcher(dir + File.separator + i, maxSearch );
		}
		return searcher;
	}

	protected void closeIndexer() {
		closeIndexer(indexer);
	}
	
	protected void closeIndexer(LuceneIndexer[] indexer) {
		for (LuceneIndexer index : indexer) {
			index.close();
		}
	}

	protected void reopenIndexer() {
		reopenIndexer(indexer);
	}
	
	protected void reopenIndexer(LuceneIndexer[] indexer) {
		for (LuceneIndexer index : indexer) {
			try {
				index.reopen();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected void closeSearcher() {
		closeSearcher(searcher);
	}
	
	protected void closeSearcher(LuceneSearcher[] searcher) {
		for (LuceneSearcher search : searcher) {
			try {
				search.close();
			} catch (IOException e) {

			}
		}
	}

	protected void reopenSearcher() {
		reopenSearcher(searcher);
	}
	
	protected void reopenSearcher(LuceneSearcher[] searcher) {
		for (LuceneSearcher search : searcher) {
			try {
				search.reopen();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> execute(LuceneSpec[] specs, int methodIdentifier, Class T, SearchStats[] stats)
			throws InterruptedException, ExecutionException, TimeoutException {
		List<Future<T>> futures = new LinkedList<Future<T>>();
		List<T> results = new LinkedList<T>();

		// create executorservice for threading
		ExecutorService service = Executors.newFixedThreadPool(indexer.length);
		// put query into each cluster using the according lucenesearcher
		for (int i = 0; i < indexer.length; i++) {
			LuceneSpec spec;
			if(specs.length==1) {
				spec=specs[0];
			}
			else {
				spec=specs[i];
			}
			SearchStats stat = new SearchStats();
			if(stats!=null) {
				stat = stats[i];
			}
			futures.add((Future<T>) service.submit(new Cluster(spec, searcher[i], indexer[i], methodIdentifier, stat, ignoreErrors)));
		}
		// shutdown and await termination of threads
		service.shutdown();
		service.awaitTermination(timeout, TimeUnit.SECONDS);

		// start cluster as thread using pool
		for (int i = 0; i < indexer.length; i++) {

			results.add(futures.get(i).get(timeout, TimeUnit.SECONDS));
		}
		service.shutdown();
		service.awaitTermination(timeout, TimeUnit.SECONDS);
		return results;
	}

	@Override
	public int getClusterSize() {
		return this.clusterSize;
	}
	
}
