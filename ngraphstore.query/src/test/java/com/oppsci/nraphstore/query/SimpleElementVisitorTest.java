package com.oppsci.nraphstore.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.syntax.ElementWalker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.oppsci.ngraphstore.query.planner.impl.SimpleElementVisitor;
import com.oppsci.ngraphstore.query.planner.step.Step;
import com.oppsci.ngraphstore.query.planner.step.impl.GroupStep;
import com.oppsci.ngraphstore.query.planner.step.impl.PatternStep;

@RunWith(Parameterized.class)
public class SimpleElementVisitorTest {

	private Query query;
	private int childs;
	private Class[] classes;

	@Parameters
	public static Collection<Object[]> data() {
		List<Object[]> testConfigs = new ArrayList<Object[]>();

		testConfigs.add(new Object[] { "SELECT * {?s ?p ?o}", 1, new Class[] { PatternStep.class } });
		testConfigs.add(new Object[] { "SELECT * {?s ?p ?o . ?u ?v ?w}", 2, new Class[] {PatternStep.class, PatternStep.class } });
		testConfigs.add(new Object[] { "SELECT * {Graph ?g {?s ?p ?o}}", 1,
				new Class[] { GroupStep.class, PatternStep.class } });
		testConfigs.add(new Object[] { "SELECT * {Graph <urn://test> {?s ?p ?o}}", 1,
				new Class[] { GroupStep.class, PatternStep.class } });
		testConfigs.add(new Object[] { "SELECT * {{?s ?p ?o} UNION {?u ?v ?w}}", 2,
				new Class[] { GroupStep.class, PatternStep.class, GroupStep.class, PatternStep.class } });
		testConfigs.add(new Object[] { "SELECT * {?s ?p ?o OPTIONAL {?u ?v ?w}}", 2,
				new Class[] { PatternStep.class, GroupStep.class, PatternStep.class } });
		testConfigs.add(new Object[] { "SELECT * {?s ?p ?o OPTIONAL {{?u ?v ?w}}}", 2,
				new Class[] { PatternStep.class, GroupStep.class, GroupStep.class, PatternStep.class } });
		
		return testConfigs;
	}

	public SimpleElementVisitorTest(String queryString, int childs, Class[] classes) {
		this.query = QueryFactory.create(queryString);
		this.childs = childs;
		this.classes = classes;
	}

	@Test
	public void basicCheck() {
		SimpleElementVisitor visitor = new SimpleElementVisitor();
		visitor.setElementWhere(query.getQueryPattern());
		ElementWalker.walk(query.getQueryPattern(), visitor);
		assertEquals(GroupStep.class, visitor.getRootStep().getClass());
		assertEquals(childs, visitor.getRootStep().getChildSteps().size());
		Step root = visitor.getRootStep();

		assertEquals(GroupStep.class, root.getClass());
		recClassTest(root, 0);
	}

	private int recClassTest(Step rootStep, Integer i) {
		int j=i;
		for (Step step : rootStep.getChildSteps()) {

			assertEquals(classes[j], step.getClass());
			j++;
			if (!step.getChildSteps().isEmpty()) {
				j=recClassTest(step, j);
			}
			
		}
		return j;
	}
}
