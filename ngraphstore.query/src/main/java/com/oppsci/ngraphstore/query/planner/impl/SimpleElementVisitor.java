package com.oppsci.ngraphstore.query.planner.impl;

import org.apache.jena.sparql.syntax.Element;
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
import org.apache.jena.sparql.syntax.ElementUnion;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.RecursiveElementVisitor;

import com.oppsci.ngraphstore.query.planner.step.Step;

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

	public void setElementWhere(Element el) {
		this.where = el;
	}

	public void startElement(ElementDataset el) {
	}

	public void endElement(ElementDataset el) {
	}

	public void startElement(ElementFilter el) {
	}

	public void endElement(ElementFilter el) {
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

	public void startElement(ElementUnion el) {
	}

	public void endElement(ElementUnion el) {
	}

	public void startElement(ElementGroup el) {
		if (el.equals(where)) {
			//root element found
			started = true;
			//set initial merger
		}
		if (started)
			System.out.println(7 + "S: " + el);
	}

	public void endElement(ElementGroup el) {
		if (started)
			System.out.println(7 + "E: " + el);
	}

	public void startElement(ElementOptional el) {
	}

	public void endElement(ElementOptional el) {
	}

	public void startElement(ElementNamedGraph el) {
		if (started)
			System.out.println(5 + "S: " + el);
	}

	public void endElement(ElementNamedGraph el) {
		if (started)
			System.out.println(5 + "E: " + el);
	}

	public void startElement(ElementService el) {
	}

	public void endElement(ElementService el) {
	}

	public void startElement(ElementExists el) {
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
		if (started)
			System.out.println(1 + "E: " + el);
	}

	public void startElement(ElementPathBlock el) {
		if (started)
			System.out.println(1 + "S: " + el);
	}

	/**
	 * @return the steps
	 */
	public Step getRootStep() {
		return rootStep;
	}


}
