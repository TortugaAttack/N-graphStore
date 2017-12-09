package com.oppsci.ngraphstore.storage;

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

import com.oppsci.ngraphstore.storage.lucene.LuceneIndexer;
import com.oppsci.ngraphstore.storage.lucene.LuceneSearcher;
import com.oppsci.ngraphstore.storage.lucene.spec.LuceneQuadUpdateSpec;
import com.oppsci.ngraphstore.storage.lucene.spec.LuceneSearchSpec;
import com.oppsci.ngraphstore.storage.lucene.spec.LuceneSpec;
import com.oppsci.ngraphstore.storage.lucene.spec.LuceneUpdateSpec;
import com.oppsci.ngraphstore.storage.lucene.spec.SearchStats;
import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

/**
 * TODO: make code more lucid, updates into Cluster, add Triples as subarray The
 * overseer of the n cluster. will execute a provided query on N Cluster
 * 
 * @author f.conrads
 *
 */
public class ClusterOverseer extends ExecutionOverseer {

	public ClusterOverseer(String rootFolder, int clusterSize, long timeout, boolean ignoreErrors) throws IOException {
		super(rootFolder, clusterSize, timeout, ignoreErrors);

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
	public SimpleResultSet search(LuceneSpec spec, SearchStats stats) throws Exception {
		reopenSearcher();
		SimpleResultSet[] results = super.execute(spec, Cluster.SEARCH_METHOD, SimpleResultSet.class, stats)
				.toArray(new SimpleResultSet[] {});
		// sync results and return
		closeSearcher();
		return mergeSyncedResults(results);
	}
	
	public SimpleResultSet searchAll(LuceneSpec spec, SearchStats stats) throws Exception {
		reopenSearcher();
		SimpleResultSet[] results = super.execute(spec, Cluster.SEARCH_ALL_METHOD, SimpleResultSet.class, stats)
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
	 */
	public boolean add(Triple<String>[] triples, String graph) {
		reopenIndexer();
		LuceneSpec spec = new LuceneUpdateSpec(graph, triples);
		Boolean[] success = new Boolean[] {false};
		try {
			success = super.execute(spec, Cluster.INSERT_METHOD, boolean.class, new SearchStats())
					.toArray(new Boolean[] {});
		} catch (InterruptedException | ExecutionException | TimeoutException e1) {
			e1.printStackTrace();
		}
		for(boolean singleSuccess : success) {
			if(!singleSuccess) {
				try {
					rollback();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		closeIndexer();
		return true;
	}

	/**
	 * Adds triples to index cluster
	 * 
	 * @param triples
	 * @param graph 
	 * @return
	 */
	public boolean load(Triple<String>[] triples, String graph) {
		reopenIndexer();
		LuceneSpec spec = new LuceneUpdateSpec(graph, triples);
		Boolean[] success = new Boolean[] {false};
		try {
			success = super.execute(spec, Cluster.LOAD_METHOD, boolean.class, new SearchStats())
					.toArray(new Boolean[] {});
		} catch (InterruptedException | ExecutionException | TimeoutException e1) {
			e1.printStackTrace();
		}
		for(boolean singleSuccess : success) {
			if(!singleSuccess) {
				try {
					rollback();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		closeIndexer();
		return true;
	}

	public boolean dropAll() {
		reopenIndexer();
		LuceneSpec spec = new LuceneUpdateSpec();
		Boolean[] success = new Boolean[] {false};
		try {
			success = super.execute(spec, Cluster.DROP_ALL_METHOD, boolean.class, new SearchStats())
					.toArray(new Boolean[] {});
		} catch (InterruptedException | ExecutionException | TimeoutException e1) {
			e1.printStackTrace();
		}
		for(boolean singleSuccess : success) {
			if(!singleSuccess) {
				try {
					rollback();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		closeIndexer();

		return true;
	}

	public boolean drop(String graph) {
		// delete all triples with graph
		reopenIndexer();
		LuceneSpec spec = new LuceneUpdateSpec(graph);
		Boolean[] success = new Boolean[] {false};
		try {
			success = super.execute(spec, Cluster.DROP_METHOD, boolean.class, new SearchStats())
					.toArray(new Boolean[] {});
		} catch (InterruptedException | ExecutionException | TimeoutException e1) {
			e1.printStackTrace();
		}
		for(boolean singleSuccess : success) {
			if(!singleSuccess) {
				try {
					rollback();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		closeIndexer();
		return true;

	}

	public boolean delete(Triple<String>[] triples, String graph) {
		reopenIndexer();
		LuceneSpec spec = new LuceneUpdateSpec(graph, triples);
		Boolean[] success = new Boolean[] {false};
		try {
			success = super.execute(spec, Cluster.DELETE_METHOD, boolean.class, new SearchStats())
					.toArray(new Boolean[] {});
		} catch (InterruptedException | ExecutionException | TimeoutException e1) {
			e1.printStackTrace();
		}
		for(boolean singleSuccess : success) {
			if(!singleSuccess) {
				try {
					rollback();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		closeIndexer();

		return true;
	}
	
	public String[][] explore(String term) throws InterruptedException, ExecutionException, TimeoutException {
		reopenSearcher();
		LuceneSpec spec= new LuceneSearchSpec(new String[] {term}, null, null);
		String[][][] unmergedResults =  super.execute(spec, Cluster.EXPLORE_METHOD, String[][][].class, new SearchStats())
				.toArray(new String[][][] {});
		//TODO mergeResults
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
	
	public boolean quadUpdate(String[] oldTerms, String[] newTerms) {
		LuceneSpec spec = new LuceneQuadUpdateSpec(oldTerms, newTerms);
		try {
			super.execute(spec, Cluster.QUAD_UPDATE, boolean.class, new SearchStats());
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
