package com.oppsci.ngraphstore.storage;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.lucene.index.CorruptIndexException;

import com.oppsci.ngraphstore.graph.Node;
import com.oppsci.ngraphstore.query.Query;
import com.oppsci.ngraphstore.query.QueryPlanner;
import com.oppsci.ngraphstore.results.SimpleResultSet;
import com.oppsci.ngraphstore.storage.lucene.LuceneSearcher;

/**
 * One of the Nth Cluster 
 * 
 * <br/>
 * Will 
 * 
 * @author f.conrads
 *
 */
public class Cluster implements Callable<SimpleResultSet> {

	private LuceneSearcher searcher;
	private LuceneSpec spec;
	
	public Cluster(LuceneSpec spec, LuceneSearcher searcher) {
		this.spec = spec;
		this.searcher = searcher;
	}
	
	public SimpleResultSet select() throws CorruptIndexException, IOException{
		//check if Lucene spec is simple
		if(spec.isSimple()) {
			return convertLuceneResults(searcher.searchRelation(spec.getUris()[0], spec.getObjectsFlags(), spec.getSearchFields()[0]));
		}
			return convertLuceneResults(searcher.searchRelation(spec.getUris(), spec.getObjectsFlags(), spec.getSearchFields()));
	}

	private SimpleResultSet convertLuceneResults(Collection<Node[]> results) {
		SimpleResultSet resultSet = new SimpleResultSet();
		List<String> vars = new LinkedList<String>();
		resultSet.addRows(results);
		if(spec.getObjectsFlags()[0])
			vars.add("subject");
		if(spec.getObjectsFlags()[1])
			vars.add("predicate");
		if(spec.getObjectsFlags()[2])
			vars.add("object");
		resultSet.setVars(vars);
		return resultSet;
	}
	
	@Override
	public SimpleResultSet call() throws Exception {
		return select();
	}



}
