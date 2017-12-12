package com.oppsci.ngraphstore.query.planner.impl;

import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.syntax.ElementWalker;

import com.oppsci.ngraphstore.query.planner.QueryPlanner;
import com.oppsci.ngraphstore.query.planner.merger.Merger;
import com.oppsci.ngraphstore.query.planner.merger.impl.JoinMerger;
import com.oppsci.ngraphstore.query.planner.step.Step;
import com.oppsci.ngraphstore.storage.cluster.overseer.ClusterOverseer;
import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

public class QueryPlannerImpl implements QueryPlanner {

	private ClusterOverseer<SimpleResultSet> overseer;

	private List<List<Step>> steps;
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
		constraintsMet = true;
		// check if all constraints met (could be LIMIT exceeds, could be no step is
		// remembered any more,...)
		while (!constraintsMet) {
			boolean rememberedOccuredAlready = false;
			for (List<Step> groupSteps : steps) {
				for (Step step : groupSteps) {
					if (step.isRemembered() && !rememberedOccuredAlready) {
						rememberedOccuredAlready = true;
					} else {
						step.reset();
					}
				}
			}
			results = startAt(0);
			// TODO checkConstraints();
			constraintsMet = true;
		}

		return results;
	}

	private SimpleResultSet startAt(int j) throws Exception {
		SimpleResultSet oldRS = null;
		for (List<Step> groupSteps : steps) {
			for (Step step : groupSteps) {
				SimpleResultSet newRS = step.execute(overseer);
				if (oldRS != null) {
					oldRS = new JoinMerger().merge(oldRS, newRS);
				} else {
					oldRS = newRS;
				}
			}
		}
		return oldRS;
	}

	private void createSteps(Query query, List<List<Step>> steps2, Merger[] merger2) {
		// TODO walk the query
		// create steps and merger
		SimpleElementVisitor elVisitor = new SimpleElementVisitor();
		ElementWalker.walk(query.getQueryPattern(), elVisitor);
		steps2 = elVisitor.getSteps();

	}

	public boolean ask(String queryString) throws Exception {
		return ask(QueryFactory.create(queryString));
	}

	public boolean ask(Query query) {
		// get where clausev from query
		SimpleResultSet currentResults = null;
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
