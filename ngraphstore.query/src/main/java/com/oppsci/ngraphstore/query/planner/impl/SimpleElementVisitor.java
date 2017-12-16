package com.oppsci.ngraphstore.query.planner.impl;

import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementAssign;
import org.apache.jena.sparql.syntax.ElementBind;
import org.apache.jena.sparql.syntax.ElementData;
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
import org.apache.jena.sparql.syntax.ElementUnion;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.RecursiveElementVisitor;

import com.oppsci.ngraphstore.query.planner.merger.impl.AddMerger;
import com.oppsci.ngraphstore.query.planner.merger.impl.JoinMerger;
import com.oppsci.ngraphstore.query.planner.merger.impl.OptionalMerger;
import com.oppsci.ngraphstore.query.planner.step.Step;
import com.oppsci.ngraphstore.query.planner.step.impl.GroupStep;
import com.oppsci.ngraphstore.query.planner.step.impl.PatternStep;

/**
 * Element Visitor to walk through a query with.<br/>
 * Will set the current steps and mergers as a linked tree.
 * 
 * @author f.conrads
 *
 */
public class SimpleElementVisitor extends RecursiveElementVisitor {

	/**
	 * Will simply creates a ElementVisitorBase as a base for the recursive 
	 * elemental visitor
	 */
	public SimpleElementVisitor() {
		//create a simple empty visitor base to override
		super(new ElementVisitorBase());
	}

	private boolean started = false;
	private Element where;
	private Step rootStep;
	private Step lastStep;

	/**
	 * Sets the complete where clause element 
	 * @param el
	 */
	public void setElementWhere(Element el) {
		this.where = el;
	}


	public void endElement(ElementFilter el) {
		//TODO create filter step
		
		//TODO add filter merger
	}

	public void startElement(ElementAssign el) {
	}

	public void endElement(ElementAssign el) {
	}

	public void startElement(ElementBind el) {
	}

	public void endElement(ElementBind el) {
	}

	public void startElement(ElementData el) {
	}

	public void endElement(ElementData el) {
	}


	public void endElement(ElementUnion el) {
		//lastStep is current union group, parent needs add merger
		if(started)
			lastStep.getMerger().add(new AddMerger());
	}

	public void startElement(ElementGroup el) {
		if (!started && el.equals(where)) {
			//root element found
			started = true;
			//set initial merger
			rootStep = new GroupStep();
			lastStep = rootStep;
			
		}
		else if (started) {
			GroupStep group = new GroupStep();
			group.setParent(lastStep);
			lastStep.getChildSteps().add(group);
			lastStep = group;
			
		}
	}

	public void endElement(ElementGroup el) {
		//set back to parent
		if(started)
			this.lastStep = lastStep.getParent();
	}

	public void endElement(ElementOptional el) {
		//lastStep is current optional group, parent needs optional merger
		if(started) 
			lastStep.getMerger().add(new OptionalMerger());
	}


	public void endElement(ElementNamedGraph el) {
		if (started) {
			if(el.getGraphNameNode().isURI()) {
				lastStep.setGraph(el.getGraphNameNode().getURI(), false);
			}
			else {
				lastStep.setGraph("?"+el.getGraphNameNode().getName(), true);
			}
		}
	}

	public void endElement(ElementService el) {
	}

	public void endElement(ElementExists el) {
	}

	public void startElement(ElementNotExists el) {
		if (started)
			System.out.println(4 + "S: " + el);
	}

	public void endElement(ElementNotExists el) {
		if (started)
			System.out.println(4 + "E: " + el);
	}

	public void startElement(ElementMinus el) {
		if (started)
			System.out.println(3 + "S: " + el);
	}

	public void endElement(ElementMinus el) {
		if (started)
			System.out.println(3 + "E: " + el);
	}

	public void endElement(ElementSubQuery el) {
		if (started)
			System.out.println(2 + "E: " + el);
	}

	public void startElement(ElementSubQuery el) {
		if (started)
			System.out.println(2 + "S: " + el);
	}

	public void endElement(ElementPathBlock el) {
		if (started) {
			for(TriplePath path : el.getPattern().getList()) {
				if(path.getPredicate()!=null) {
					//plain predicate
					PatternStep step = new PatternStep();
					step.setPattern(path);
					step.setParent(lastStep);
					lastStep.getChildSteps().add(step);
				}
			}
			for(int i=0; i<el.getPattern().size()-1;i++)
				lastStep.getMerger().add(new JoinMerger());
		}
	}


	/**
	 * @return the steps
	 */
	public Step getRootStep() {
		return rootStep;
	}


}
