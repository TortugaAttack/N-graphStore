package com.oppsci.ngraphstore.query;

import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;

import com.oppsci.ngraphstore.results.SimpleResultSet;
import com.oppsci.ngraphstore.sparql.elements.Filter;
import com.oppsci.ngraphstore.storage.lucene.LuceneSearcher;

public class QueryPlanner {

	private LuceneSearcher searcher;
	
	public SimpleResultSet search(String query){
		Query q = QueryFactory.create(query);
		//sort BGP according to restrictive first principle
		
//		List<BGPElement> rfpSortedBGPs = sortToRfp(q.);
		//apply BGP Lucene Search
//		List<String[]> currentResults = applyBGPs(rfpSortedBGPs);
		//apply in memory Filter search
//		currentResults = applyFilter(currentResults, q.get);
		//return results
//		return new SimpleResultSet(q.getResultVars(), currentResults);
		return null;
	}
	
//	public List<BGPElement> sortToRfp(List<BGPElement> bgps){
//		return null;
//		
//	}
//	
//	public List<String[]> applyBGPs(List<BGPElement> rfpSortedBGPs){
//		return null;		
//	}
	
	public List<String[]> applyFilter(List<String[]> currentResults, List<Filter> filter){
		return null;
	}
	
}
