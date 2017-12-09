package com.oppsci.ngraphstore.storage.cluster.overseer.impl;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.oppsci.ngraphstore.graph.Triple;
import com.oppsci.ngraphstore.storage.cluster.Cluster;
import com.oppsci.ngraphstore.storage.cluster.overseer.AbstractClusterOverseer;
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
public class ClusterOverseerImpl extends AbstractClusterOverseer {

	public ClusterOverseerImpl(String rootFolder, int clusterSize, long timeout, boolean ignoreErrors) throws IOException {
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
	@Override
	public SimpleResultSet search(LuceneSpec spec, SearchStats stats) throws Exception {
		reopenSearcher();
		SimpleResultSet[] results = super.execute(spec, Cluster.SEARCH_METHOD, SimpleResultSet.class, stats)
				.toArray(new SimpleResultSet[] {});
		// sync results and return
		closeSearcher();
		return mergeSyncedResults(results);
	}
	
	@Override
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
	 * @throws Exception 
	 */
	@Override
	public boolean add(Triple<String>[] triples, String graph) throws Exception {
		reopenIndexer();
		LuceneSpec spec = new LuceneUpdateSpec(graph, triples);
		Boolean[] success = new Boolean[] {false};
		try {
			success = super.execute(spec, Cluster.INSERT_METHOD, boolean.class, new SearchStats())
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

	/**
	 * Adds triples to index cluster
	 * 
	 * @param triples
	 * @param graph 
	 * @return
	 * @throws Exception 
	 */
	@Override
	public boolean load(Triple<String>[] triples, String graph) throws Exception {
		reopenIndexer();
		LuceneSpec spec = new LuceneUpdateSpec(graph, triples);
		Boolean[] success = new Boolean[] {false};
		try {
			success = super.execute(spec, Cluster.LOAD_METHOD, boolean.class, new SearchStats())
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
			success = super.execute(spec, Cluster.DROP_ALL_METHOD, boolean.class, new SearchStats())
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
			success = super.execute(spec, Cluster.DROP_METHOD, boolean.class, new SearchStats())
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
	public boolean delete(Triple<String>[] triples, String graph) throws Exception {
		reopenIndexer();
		LuceneSpec spec = new LuceneUpdateSpec(graph, triples);
		Boolean[] success = new Boolean[] {false};
		try {
			success = super.execute(spec, Cluster.DELETE_METHOD, boolean.class, new SearchStats())
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
		String[][][] unmergedResults =  super.execute(spec, Cluster.EXPLORE_METHOD, String[][][].class, new SearchStats())
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
			super.execute(spec, Cluster.QUAD_UPDATE, boolean.class, new SearchStats());
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
			throw e;
		}
		return true;
	}
}
