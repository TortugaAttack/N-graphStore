package com.oppsci.ngraphstore.query.planner.impl;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.syntax.ElementWalker;

import com.oppsci.ngraphstore.query.planner.QueryPlanner;
import com.oppsci.ngraphstore.query.planner.step.Step;
import com.oppsci.ngraphstore.storage.cluster.overseer.ClusterOverseer;
import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

public class QueryPlannerImpl implements QueryPlanner {

	private ClusterOverseer<SimpleResultSet> overseer;


	public QueryPlannerImpl(ClusterOverseer<SimpleResultSet> overseer) {
		this.overseer = overseer;
	}

	public SimpleResultSet select(String queryString) throws Exception {
		return select(QueryFactory.create(queryString));
	}

	public SimpleResultSet select(Query query) throws Exception {
		// 1. create Steps & merger (this is the actual queryplan)
		Step rootStep = createSteps(query);
		SimpleResultSet results = rootStep.execute(overseer);
		//TODO black boxed remembered

		return results;
	}


	private Step createSteps(Query query) {
		// create steps and merger
		SimpleElementVisitor elVisitor = new SimpleElementVisitor();
		elVisitor.setElementWhere(query.getQueryPattern());
		ElementWalker.walk(query.getQueryPattern(), elVisitor);
		return elVisitor.getRootStep();
	}

	public boolean ask(String queryString) throws Exception {
		return ask(QueryFactory.create(queryString));
	}

	public boolean ask(Query query) {
		// get where clausev from query
		SimpleResultSet currentResults = new SimpleResultSet();
		// apply plan with limit 1
		// check if results is not empty
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

}
