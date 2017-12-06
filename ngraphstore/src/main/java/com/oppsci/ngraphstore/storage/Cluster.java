package com.oppsci.ngraphstore.storage;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.lucene.index.CorruptIndexException;
import org.springframework.beans.factory.annotation.Autowired;

import com.oppsci.ngraphstore.graph.Triple;
import com.oppsci.ngraphstore.graph.elements.Node;
import com.oppsci.ngraphstore.storage.lucene.LuceneConstants;
import com.oppsci.ngraphstore.storage.lucene.LuceneIndexer;
import com.oppsci.ngraphstore.storage.lucene.LuceneSearcher;
import com.oppsci.ngraphstore.storage.lucene.spec.LuceneSearchSpec;
import com.oppsci.ngraphstore.storage.lucene.spec.LuceneSpec;
import com.oppsci.ngraphstore.storage.lucene.spec.LuceneUpdateSpec;
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

	private LuceneSearcher searcher;
	private LuceneIndexer indexer;
	private LuceneSpec spec;
	private int methodIdentifier;

	@Autowired
	private boolean ignoreErrors;

	public Cluster(LuceneSpec spec, LuceneSearcher searcher, LuceneIndexer indexer, int methodIdentifier) {
		this.spec = spec;
		this.searcher = searcher;
		this.indexer = indexer;
		this.methodIdentifier = methodIdentifier;
	}

	public SimpleResultSet select() throws CorruptIndexException, IOException {
		LuceneSearchSpec spec = (LuceneSearchSpec) this.spec;
		// check if Lucene spec is simple
		if (spec.isSimple()) {
			return convertLuceneResults(
					searcher.searchRelation(spec.getUris()[0], spec.getObjectsFlags(), spec.getSearchFields()[0]));
		}
		return convertLuceneResults(
				searcher.searchRelation(spec.getUris(), spec.getObjectsFlags(), spec.getSearchFields()));
	}

	public SimpleResultSet selectAll() throws CorruptIndexException, IOException {
		LuceneSearchSpec spec = (LuceneSearchSpec) this.spec;
		return convertLuceneResults(searcher.getAllRecords(spec.getObjectsFlags()));
	}

	public boolean add() {
		int i = 0;
		LuceneUpdateSpec spec = (LuceneUpdateSpec) this.spec;

		for (Triple<String> triple : spec.getTriples()) {
			try {
				indexer.index(triple.getSubject(), triple.getPredicate(), triple.getObject(), spec.getGraph());
			} catch (IOException e) {
				if (!ignoreErrors)
					return false;
			}
		}
		return true;
	}

	public boolean load() {
		int i = 0;
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
		int i = 0;
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

	private SimpleResultSet convertLuceneResults(Collection<Node[]> results) {
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
		return resultSet;
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

		}
		return null;
	}

}
