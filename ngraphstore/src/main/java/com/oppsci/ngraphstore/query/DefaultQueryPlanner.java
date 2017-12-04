package com.oppsci.ngraphstore.query;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.oppsci.ngraphstore.results.SimpleResultSet;
import com.oppsci.ngraphstore.sparql.elements.BGPElement;
import com.oppsci.ngraphstore.sparql.elements.Filter;
import com.oppsci.ngraphstore.storage.ClusterOverseer;
import com.oppsci.ngraphstore.storage.lucene.LuceneSearcher;

public class DefaultQueryPlanner implements QueryPlanner{

	@Autowired
	private QueryParser parser;
	
	@Autowired
	private ClusterOverseer overseer;
	
	public SimpleResultSet select(String queryString) throws Exception {
		return select(parser.parse(queryString));
	}
	
	public SimpleResultSet select(Query query){
		//sort BGP according to restrictive first principle
		List<BGPElement> rfpSortedBGPs = sortToRfp(query.getElements());
		//apply BGP Lucene Search
		SimpleResultSet currentResults = applyBGPs(rfpSortedBGPs);
		//apply in memory Filter search
		currentResults = applyFilter(currentResults, query.getFilter());
		//return results
		currentResults.setVars(query.getProjectionVars());
		return null;
	}
	
	public List<BGPElement> sortToRfp(List<BGPElement> bgps){
		return null;
		
	}
	
	public SimpleResultSet applyBGPs(List<BGPElement> rfpSortedBGPs){
		//create specs for each bgpelement
		
		//execute spec against overseer
		
		//use results for next bgps if possible
		return null;		
	}
	
	public SimpleResultSet applyFilter(SimpleResultSet currentResults, List<Filter> filter){
		return null;
	}
	
}
