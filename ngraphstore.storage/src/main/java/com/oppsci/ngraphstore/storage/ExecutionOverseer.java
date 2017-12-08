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

import com.oppsci.ngraphstore.storage.lucene.LuceneIndexer;
import com.oppsci.ngraphstore.storage.lucene.LuceneSearcher;
import com.oppsci.ngraphstore.storage.lucene.spec.LuceneSpec;
import com.oppsci.ngraphstore.storage.lucene.spec.SearchStats;

/**
 * @author f.conrads
 *
 */
public class ExecutionOverseer {

	private long timeout = 180;
	private int clusterSize;
	private LuceneSearcher[] searcher;
	private LuceneIndexer[] indexer;
	private boolean ignoreErrors;

	public ExecutionOverseer(String rootFolder, int clusterSize, long timeout, boolean ignoreErrors) throws IOException {
		this.timeout = timeout;
		this.clusterSize=clusterSize;
		this.indexer = createIndexerOnTheFly(rootFolder);
		closeIndexer(this.indexer);
		this.searcher = createSearcherOnTheFly(rootFolder);
		closeSearcher(this.searcher);
		this.ignoreErrors=ignoreErrors;
	}
	
	public ExecutionOverseer(LuceneIndexer[] indexer, LuceneSearcher[] searcher, long timeout) {
		this.timeout = timeout;
		this.indexer = indexer;
		this.searcher = searcher;
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
			searcher[i] = new LuceneSearcher(dir + File.separator + i);
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

	@SuppressWarnings("unchecked")
	public <T> List<T> execute(LuceneSpec spec, int methodIdentifier, Class T, SearchStats stats)
			throws InterruptedException, ExecutionException {
		List<Future<T>> futures = new LinkedList<Future<T>>();
		List<T> results = new LinkedList<T>();

		// create executorservice for threading
		ExecutorService service = Executors.newFixedThreadPool(indexer.length);
		// put query into each cluster using the according lucenesearcher
		for (int i = 0; i < indexer.length; i++) {
			futures.add((Future<T>) service.submit(new Cluster(spec, searcher[i], indexer[i], methodIdentifier, stats, ignoreErrors)));
		}
		// shutdown and await termination of threads
		service.shutdown();
		service.awaitTermination(timeout, TimeUnit.SECONDS);

		// start cluster as thread using pool
		for (int i = 0; i < indexer.length; i++) {
			results.add(futures.get(i).get());
		}
		return results;
	}
	
	public void rollback() throws IOException {
		for(LuceneIndexer index : indexer) {
			index.rollback();
		}
	}
}
