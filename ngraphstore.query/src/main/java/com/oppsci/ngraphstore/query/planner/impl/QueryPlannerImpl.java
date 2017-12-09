package com.oppsci.ngraphstore.query.planner.impl;

import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;

import com.oppsci.ngraphstore.query.planner.QueryPlanner;
import com.oppsci.ngraphstore.query.planner.Step;
import com.oppsci.ngraphstore.query.planner.merger.Merger;
import com.oppsci.ngraphstore.query.sparql.elements.impl.BGPElement;
import com.oppsci.ngraphstore.query.sparql.elements.impl.Filter;
import com.oppsci.ngraphstore.storage.cluster.overseer.ClusterOverseer;
import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

//TODO use Jena Query (make own parser sometime else)
public class QueryPlannerImpl implements QueryPlanner {


	private ClusterOverseer<SimpleResultSet> overseer;

	private Step[] steps;
	private Merger[] merger;
	
	public QueryPlannerImpl(ClusterOverseer<SimpleResultSet> overseer) {
		this.overseer = overseer;
	}

	public SimpleResultSet select(String queryString) throws Exception {
		return select(QueryFactory.create(queryString));
	}

	public SimpleResultSet select(Query query) throws Exception {
		boolean constraintsMet = false;

		// 1. create Steps & merger (this is the actual queryplan)
		createSteps(query, steps, merger);
		SimpleResultSet results = startAt(0);
		// check if all constraints met (could be LIMIT exceeds, could be no step is
		// remembered any more,...)
		while (!constraintsMet) {
			boolean rememberedOccuredAlready = false;
			for (int i = steps.length - 1; i >= 0; i--) {

				if (steps[i].isRemembered() && !rememberedOccuredAlready) {
					rememberedOccuredAlready = true;
				} else {
					steps[i].reset();
				}
			}
			results = startAt(0);
			// TODO checkConstraints();
		}

		return results;
	}

	private SimpleResultSet startAt(int j) throws Exception {
		SimpleResultSet oldRS = null;
		for (int i=j; i < steps.length; i++) {
			SimpleResultSet newRS = steps[i].execute(overseer);
			if (oldRS != null)
				oldRS = merger[i].merge(oldRS, newRS);
		}
		return oldRS;
	}

	private void createSteps(Query query, Step[] steps2, Merger[] merger2) {
		// TODO Auto-generated method stub

	}

	public boolean ask(String queryString) throws Exception {
		return ask(QueryFactory.create(queryString));
	}

	public boolean ask(Query query) {
		//get where clausev from query
		SimpleResultSet currentResults =null;
		//apply plan with limit 1 
		//check if results is not empty
		return !currentResults.getRows().isEmpty();
	}

	public Model describe(String queryString) throws Exception {
		return describe(QueryFactory.create(queryString));
	}

	public Model describe(Query query) {
		return null;
	}

	public Model construct(String queryString) throws Exception {
		return construct(QueryFactory.create(queryString));
	}

	public Model construct(Query query) {
		return null;
	}

	public List<BGPElement> sortToRfp(List<BGPElement> bgps) {
		return null;

	}

	public SimpleResultSet applyBGPs(List<BGPElement> rfpSortedBGPs) {
		// create specs for each bgpelement

		// execute spec against overseer

		// use results for next bgps if possible
		return null;
	}

	public SimpleResultSet applyFilter(SimpleResultSet currentResults, List<Filter> filter) {
		return null;
	}


}
