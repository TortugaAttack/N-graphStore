package com.oppsci.ngraphstore.storage;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.oppsci.ngraphstore.graph.Triple;
import com.oppsci.ngraphstore.query.Query;
import com.oppsci.ngraphstore.query.QueryParser;
import com.oppsci.ngraphstore.results.SimpleResultSet;
import com.oppsci.ngraphstore.storage.lucene.LuceneIndexer;
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
	private File dir;
	private LuceneSearcher[] searcher;
	private LuceneIndexer[] indexer;

	public ClusterOverseer(int clusterSize, String rootFolder, long timeout) throws IOException {
		this.clusterSize = clusterSize;
		this.timeout = timeout;
		for (int i = 0; i < clusterSize; i++) {
			// create LuceneSearcher at rootFolder/0/ ... rootFolder/N/
			dir = new File(rootFolder + File.separator + i);
			indexer = createIndexerOnTheFly(dir.getAbsolutePath());
			closeIndexer(indexer);
			searcher = createSearcherOnTheFly(dir.getAbsolutePath());
			closeSearcher(searcher);
		}
	}

	private LuceneIndexer[] createIndexerOnTheFly(String dir) throws IOException {
		LuceneIndexer[] indexer = new LuceneIndexer[clusterSize];
		for (int i = 0; i < clusterSize; i++) {
			indexer[i] = new LuceneIndexer(dir);
		}
		return indexer;
	}

	private void closeIndexer(LuceneIndexer[] indexer) {
		for (LuceneIndexer index : indexer) {
			index.close();
		}
	}

	private void reopenIndexer(LuceneIndexer[] indexer) {
		for (LuceneIndexer index : indexer) {
			try {
				index.reopen();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void reopenSearcher(LuceneSearcher[] searcher) {
		for (LuceneSearcher search : searcher) {
			try {
				search.reopen();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void closeSearcher(LuceneSearcher[] searcher) {
		for (LuceneSearcher search : searcher) {
			try {
				search.close();
			} catch (IOException e) {

			}
		}
	}

	private LuceneSearcher[] createSearcherOnTheFly(String dir) throws IOException {
		LuceneSearcher[] searcher = new LuceneSearcher[clusterSize];
		for (int i = 0; i < clusterSize; i++) {
			searcher[i] = new LuceneSearcher(dir);
		}
		return searcher;
	}

	private SimpleResultSet mergeSyncedResults(SimpleResultSet[] results) {
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
	public SimpleResultSet search(LuceneSpec spec) throws Exception {
		reopenSearcher(searcher);
		List<Future<SimpleResultSet>> futures = new LinkedList<Future<SimpleResultSet>>();
		SimpleResultSet[] results = new SimpleResultSet[clusterSize];

		// create executorservice for threading
		ExecutorService service = Executors.newFixedThreadPool(clusterSize);
		// put query into each cluster using the according lucenesearcher
		for (int i = 0; i < clusterSize; i++) {
			futures.add(service.submit(new Cluster(spec, searcher[i])));
		}
		// shutdown and await termination of threads
		service.shutdown();
		service.awaitTermination(timeout, TimeUnit.SECONDS);

		// start cluster as thread using pool
		for (int i = 0; i < clusterSize; i++) {
			results[i] = futures.get(i).get();
		}
		// sync results and return
		closeSearcher(searcher);
		return mergeSyncedResults(results);
	}

	/**
	 * Adds triples to index cluster
	 * 
	 * @param triples
	 * @return
	 */
	public boolean add(Triple<String>[] triples) {
		reopenIndexer(indexer);
		int i = 0;

		for (Triple<String> triple : triples) {
			try {
				indexer[i++].index(triple.getSubject(), triple.getPredicate(), triple.getObject());
			} catch (IOException e) {
				//TODO rollback!
				return false;
			}
			if (i >= clusterSize) {
				i = 0;
			}
		}
		closeIndexer(indexer);
		return true;
	}

	/**
	 * Adds triples to index cluster
	 * 
	 * @param triples
	 * @return
	 */
	public boolean load(Triple<String>[] triples) {
		reopenIndexer(indexer);
		int i = 0;
		for (Triple<String> triple : triples) {
			try {
				indexer[i].deleteAll();
				indexer[i++].index(triple.getSubject(), triple.getPredicate(), triple.getObject());
			} catch (IOException e) {
				//TODO rollback!
				return false;
			}
			if (i >= clusterSize) {
				i = 0;
			}
		}
		closeIndexer(indexer);
		return true;
	}

	public boolean drop() {
		reopenIndexer(indexer);
		for (LuceneIndexer index : indexer) {
			try {
				index.deleteAll();
			} catch (IOException e) {
				//TODO rollback!
				return false;
			}
		}
		closeIndexer(indexer);

		return true;
	}

	public boolean delete(Triple<String>[] triples) {
		reopenIndexer(indexer);
		int i = 0;
		for (Triple<String> triple : triples) {
			try {
				indexer[i++].delete(triple.getSubject(), triple.getPredicate(), triple.getObject());
			} catch (IOException e) {
				//TODO rollback!
				return false;
			}
			if (i >= clusterSize) {
				i = 0;
			}
		}
		closeIndexer(indexer);

		return true;
	}
}
