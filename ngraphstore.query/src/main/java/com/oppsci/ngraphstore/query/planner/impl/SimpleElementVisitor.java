package com.oppsci.ngraphstore.query.planner.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.ElementAssign;
import org.apache.jena.sparql.syntax.ElementBind;
import org.apache.jena.sparql.syntax.ElementData;
import org.apache.jena.sparql.syntax.ElementDataset;
import org.apache.jena.sparql.syntax.ElementExists;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementMinus;
import org.apache.jena.sparql.syntax.ElementNamedGraph;
import org.apache.jena.sparql.syntax.ElementNotExists;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementService;
import org.apache.jena.sparql.syntax.ElementSubQuery;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.apache.jena.sparql.syntax.ElementUnion;
import org.apache.jena.sparql.syntax.ElementVisitor;

import com.oppsci.ngraphstore.query.planner.merger.Merger;
import com.oppsci.ngraphstore.query.planner.merger.impl.JoinMerger;
import com.oppsci.ngraphstore.query.planner.step.Step;
import com.oppsci.ngraphstore.query.planner.step.impl.PatternStep;


public class SimpleElementVisitor implements ElementVisitor {

	
	private List<List<Step>> steps = new LinkedList<List<Step>>();
	private List<Step> groupSteps = new LinkedList<Step>();
	
	private List<List<Merger>> merger = new LinkedList<List<Merger>>();
	private List<Merger> groupMerger = new LinkedList<Merger>();
	
	@Override
	public void visit(ElementTriplesBlock el) {
		// TODO Auto-generated method stub
		System.out.println(0);
	}

	@Override
	public void visit(ElementPathBlock el) {
		System.out.println(1);
		for(TriplePath pattern : el.getPattern()) {
			if(pattern.getPredicate()!=null) {
				// not path, but plain predicate
				PatternStep patternStep = new PatternStep();
				patternStep.setPattern(pattern);
				groupSteps.add(patternStep);
				groupMerger.add(new JoinMerger());
			}
		}
		
	}

	@Override
	public void visit(ElementFilter el) {
		// TODO Auto-generated method stub
		System.out.println(2);
	}

	@Override
	public void visit(ElementAssign el) {
		// TODO Auto-generated method stub
		System.out.println(3);
	}

	@Override
	public void visit(ElementBind el) {
		// TODO Auto-generated method stub
		System.out.println(4);
	}

	@Override
	public void visit(ElementData el) {
		// TODO Auto-generated method stub
		System.out.println(5);
	}

	@Override
	public void visit(ElementUnion el) {
		// TODO Auto-generated method stub
		System.out.println(6);
	}

	@Override
	public void visit(ElementOptional el) {
		// TODO Auto-generated method stub
		System.out.println(7);
	}

	@Override
	public void visit(ElementGroup el) {
		//group is finished
		//add steps
		if(!groupSteps.isEmpty()) {
			steps.add(groupSteps);
			groupSteps = new LinkedList<Step>();
		}
		//add merger
		if(!merger.isEmpty()) {
			merger.add(groupMerger);
			groupMerger = new LinkedList<Merger>();
		}
		System.out.println(8);
	}

	@Override
	public void visit(ElementDataset el) {
		// TODO Auto-generated method stub
		System.out.println(9);
	}

	@Override
	public void visit(ElementNamedGraph el) {
		// graph always appears after according group. 
		// set graph to previous groupStep
		
		//get last Group of steps (previously added)
		List<Step> lastGroup = steps.get(steps.size()-1);
		for(Step step : lastGroup) {
			//add graph to each step
			if(el.getGraphNameNode().isURI()) {
				step.setGraph("<"+el.getGraphNameNode().getURI()+">", false);
			}
			else {
				// graph is var
				step.setGraph(el.getGraphNameNode().getName(), true);
			}
		}
		System.out.println(10);
	}

	@Override
	public void visit(ElementExists el) {
		// TODO Auto-generated method stub
		System.out.println(11);
	}

	@Override
	public void visit(ElementNotExists el) {
		// TODO Auto-generated method stub
		System.out.println(12);
	}

	@Override
	public void visit(ElementMinus el) {
		// TODO Auto-generated method stub
		System.out.println(13);
	}

	@Override
	public void visit(ElementService el) {
		// TODO Auto-generated method stub
		System.out.println(14);
	}

	@Override
	public void visit(ElementSubQuery el) {
		// TODO Auto-generated method stub
		System.out.println(15);
	}

	/**
	 * @return the steps
	 */
	public List<List<Step>> getSteps() {
		return steps;
	}

	/**
	 * @param steps the steps to set
	 */
	public void setSteps(List<List<Step>> steps) {
		this.steps = steps;
	}

}
