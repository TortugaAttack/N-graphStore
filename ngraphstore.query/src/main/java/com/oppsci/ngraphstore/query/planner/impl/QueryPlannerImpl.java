package com.oppsci.ngraphstore.query.planner.impl;

import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.syntax.ElementWalker;

import com.oppsci.ngraphstore.query.planner.QueryPlanner;
import com.oppsci.ngraphstore.query.planner.merger.impl.AddMerger;
import com.oppsci.ngraphstore.query.planner.step.Step;
import com.oppsci.ngraphstore.storage.cluster.overseer.ClusterOverseer;
import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

public class QueryPlannerImpl implements QueryPlanner {

	private ClusterOverseer<SimpleResultSet> overseer;
	private long internalLimit = 40000;

	public QueryPlannerImpl(ClusterOverseer<SimpleResultSet> overseer) {
		this.overseer = overseer;
	}

	public SimpleResultSet select(String queryString) throws Exception {
		return select(QueryFactory.create(queryString));
	}

	public SimpleResultSet select(Query query) throws Exception {
		// 1. create Steps & merger (this is the actual queryplan)
		Step rootStep = createSteps(query);
		boolean constraintsMet = true;
		//set limit to internal limit and check if actual limit is smaller than internal
		Long limit = internalLimit;
		if (query.getLimit() > 0) {
			limit = Math.min(internalLimit, query.getLimit());
		}
		// execute first round
		SimpleResultSet results = new SimpleResultSet();
		do {
			constraintsMet = true;
			SimpleResultSet newResults = rootStep.execute(overseer);
			// remove all non projection vars
			newResults.removeNonProjection(query.getResultVars());
			// merge results with old results
			if(results.getRows().isEmpty()) {
				results = newResults;
			}else {
				results = new AddMerger().merge(results, newResults);
			}
			
			// apply modifier.
			if (query.isDistinct()) {
				results.distinct();
			}
			if (results.getRows().size() < limit &&
				!(results.getRows().size()>0 && query.isReduced())){
					constraintsMet = false;
			}
			
			// does constraints still met?

		} while (rootStep.isRemembered() && !constraintsMet);
		
		if(results.getRows().size()>limit) {
			results.setRows(((List)results.getRows()).subList(0, limit.intValue()));
		}
		return results;
	}

	private Step createSteps(Query query) {
		// create steps and merger
		SimpleElementVisitor elVisitor = new SimpleElementVisitor();
		elVisitor.setElementWhere(query.getQueryPattern());
		ElementWalker.walk(query.getQueryPattern(), elVisitor);
		//return root step
		return elVisitor.getRootStep();
	}

	/**
	 * Executes an ASK query and returns the answer as a boolean
	 * 
	 * @param queryString the ask query
	 * @return the answer to the ask query
	 * @throws Exception
	 */
	public boolean ask(String queryString) throws Exception {
		return ask(QueryFactory.create(queryString));
	}

	public boolean ask(Query query) throws Exception {
		// 1. create Steps & merger (this is the actual queryplan)
		Step rootStep = createSteps(query);
		// execute until either no more triples can be considered or the the results are not empty
		SimpleResultSet results = new SimpleResultSet();
		do {
			results = rootStep.execute(overseer);
		}while(rootStep.isRemembered()&&results.getRows().isEmpty());
		//if the results are empty return true, otherwise false
		return !results.getRows().isEmpty();
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
