package com.oppsci.ngraphstore.storage.cluster;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.lucene.index.CorruptIndexException;

import com.oppsci.ngraphstore.graph.Triple;
import com.oppsci.ngraphstore.graph.elements.Node;
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
 * One of the Nth Cluster
 * 
 * <br/>
 * Will
 * 
 * @author f.conrads
 *
 */
public class Cluster implements Callable<Object> {

	public static final int SEARCH_METHOD = 0;
	public static final int SEARCH_ALL_METHOD = 1;
	public static final int INSERT_METHOD = 2;
	public static final int DELETE_METHOD = 3;
	public static final int DROP_METHOD = 4;
	public static final int LOAD_METHOD = 5;
	public static final int DROP_ALL_METHOD = 6;
	public static final int EXPLORE_METHOD = 7;
	public static final int QUAD_UPDATE = 8;

	private LuceneSearcher searcher;
	private LuceneIndexer indexer;
	private LuceneSpec spec;
	private int methodIdentifier;
	private SearchStats stats;

	private boolean ignoreErrors;

	public Cluster(LuceneSpec spec, LuceneSearcher searcher, LuceneIndexer indexer, int methodIdentifier,
			SearchStats stats, boolean ignoreErrors) {
		this.spec = spec;
		this.searcher = searcher;
		this.indexer = indexer;
		this.methodIdentifier = methodIdentifier;
		this.stats = stats;
		this.ignoreErrors = ignoreErrors;
	}

	public SimpleResultSet select() throws CorruptIndexException, IOException {
		LuceneSearchSpec spec = (LuceneSearchSpec) this.spec;
		// check if Lucene spec is simple

		if (spec.isSimple()) {

			return convertLuceneResults(searcher.searchRelation(spec.getUris()[0], spec.getObjectsFlags(),
					spec.getSearchFields()[0], stats), stats);
		}
		return convertLuceneResults(
				searcher.searchRelation(spec.getUris(), spec.getObjectsFlags(), spec.getSearchFields(), stats), stats);
	}

	public SimpleResultSet selectAll() throws CorruptIndexException, IOException {
		LuceneSearchSpec spec = (LuceneSearchSpec) this.spec;

		return convertLuceneResults(searcher.getAllRecords(spec.getObjectsFlags(), stats), stats);
	}

	public boolean add() {
		LuceneUpdateSpec spec = (LuceneUpdateSpec) this.spec;
		boolean[] flags = new boolean[] { true, false, false, false };
		String[] fields = new String[] { LuceneConstants.SUBJECT, LuceneConstants.PREDICATE, LuceneConstants.OBJECT,
				LuceneConstants.GRAPH };
		indexer.close();
		try {
			searcher.reopen();
			List<Triple<String>> triples = spec.getTriples();
			for (int j=0;j<triples.size();j++) {
				Triple<String> triple = triples.get(j);
				String[] quad = new String[] { triple.getSubject(), triple.getPredicate(), triple.getObject(),
						spec.getGraph() };
				if (!searcher.searchRelation(quad, flags, fields, stats).isEmpty()) {
					triples.remove(j);
					continue;
				}

			}
			searcher.close();

			indexer.reopen();
			for (Triple<String> triple : triples) {
				try {

					indexer.index(triple.getSubject(), triple.getPredicate(), triple.getObject(), spec.getGraph());
				} catch (IOException e) {
					if (!ignoreErrors)
						return false;
				}
			}
			indexer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean load() {
		LuceneUpdateSpec spec = (LuceneUpdateSpec) this.spec;

		for (Triple<String> triple : spec.getTriples()) {
			try {
				indexer.deleteAll();
				indexer.index(triple.getSubject(), triple.getPredicate(), triple.getObject(), spec.getGraph());
			} catch (IOException e) {
				if (!ignoreErrors)
					return false;
			}
		}
		return true;
	}

	public boolean drop() {
		LuceneUpdateSpec spec = (LuceneUpdateSpec) this.spec;

		try {

			indexer.delete(new String[] { spec.getGraph() }, new String[] { LuceneConstants.GRAPH });
		} catch (IOException e) {
			if (!ignoreErrors)
				return false;
		}

		return true;
	}

	public boolean dropAll() {
		try {
			indexer.deleteAll();
		} catch (IOException e) {
			if (!ignoreErrors)
				return false;
		}
		return true;
	}

	public boolean delete() {
		LuceneUpdateSpec spec = (LuceneUpdateSpec) this.spec;

		for (Triple<String> triple : spec.getTriples()) {
			try {
				indexer.delete(triple.getSubject(), triple.getPredicate(), triple.getObject(), spec.getGraph());
			} catch (IOException e) {
				if (!ignoreErrors)
					return false;
			}
		}
		return true;
	}

	public String[][] explore() throws IOException {
		LuceneSearchSpec spec = (LuceneSearchSpec) this.spec;

		return searcher.explore(spec.getUris()[0]);
	}

	private SimpleResultSet convertLuceneResults(Collection<Node[]> results, SearchStats stats) {
		LuceneSearchSpec spec = (LuceneSearchSpec) this.spec;

		SimpleResultSet resultSet = new SimpleResultSet();
		List<String> vars = new LinkedList<String>();
		resultSet.addRows(results);
		if (spec.getObjectsFlags()[0])
			vars.add("subject");
		if (spec.getObjectsFlags()[1])
			vars.add("predicate");
		if (spec.getObjectsFlags()[2])
			vars.add("object");
		resultSet.setVars(vars);
		resultSet.setStats(stats);
		return resultSet;
	}

	/**
	 * @return the stats
	 */
	public SearchStats getStats() {
		return stats;
	}

	/**
	 * @param stats
	 *            the stats to set
	 */
	public void setStats(SearchStats stats) {
		this.stats = stats;
	}

	@Override
	public Object call() throws Exception {
		switch (methodIdentifier) {
		case SEARCH_METHOD:
			return select();
		case SEARCH_ALL_METHOD:
			return selectAll();
		case INSERT_METHOD:
			return add();
		case DELETE_METHOD:
			return delete();
		case DROP_METHOD:
			return drop();
		case LOAD_METHOD:
			return load();
		case DROP_ALL_METHOD:
			return dropAll();
		case EXPLORE_METHOD:
			return explore();
		case QUAD_UPDATE:
			return quadUpdate();
		default:
			//TODO log and throw error 
		}
		return null;
	}

	private boolean quadUpdate() {
		LuceneQuadUpdateSpec spec = (LuceneQuadUpdateSpec) this.spec;
		try {
			// check if exists
			searcher.reopen();
			String[] searchFields = new String[] { LuceneConstants.SUBJECT, LuceneConstants.PREDICATE,
					LuceneConstants.OBJECT, LuceneConstants.GRAPH };
			if (searcher.searchRelation(spec.getCurrentTerms(), new boolean[] { true, true, true, true }, searchFields,
					new SearchStats()).isEmpty()) {
				searcher.close();
				return false;
			}
			searcher.close();
			indexer.reopen();
			indexer.update(spec.getCurrentTerms(), spec.getNewTerms());
			indexer.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
