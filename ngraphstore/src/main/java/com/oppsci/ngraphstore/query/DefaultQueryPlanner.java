package com.oppsci.ngraphstore.query;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.oppsci.ngraphstore.graph.Graph;
import com.oppsci.ngraphstore.query.sparql.elements.BGPElement;
import com.oppsci.ngraphstore.query.sparql.elements.Filter;
import com.oppsci.ngraphstore.storage.ClusterOverseer;
import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

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
		return currentResults;
	}
	
	public boolean ask(String queryString) throws Exception {
		return ask(parser.parse(queryString));
	}
	
	public boolean ask(Query query){
		//sort BGP according to restrictive first principle
		List<BGPElement> rfpSortedBGPs = sortToRfp(query.getElements());
		//apply BGP Lucene Search
		SimpleResultSet currentResults = applyBGPs(rfpSortedBGPs);
		//apply in memory Filter search
		currentResults = applyFilter(currentResults, query.getFilter());
		return currentResults.getRows().size()>0;
	}
	
	public Graph describe(String queryString) throws Exception {
		return describe(parser.parse(queryString));
	}
	
	public Graph describe(Query query){
		return null;
	}
	
	public Graph construct(String queryString) throws Exception {
		return construct(parser.parse(queryString));
	}
	
	public Graph construct(Query query){
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
