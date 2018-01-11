package com.oppsci.nraphstore.query;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryVisitor;
import org.apache.jena.sparql.core.Prologue;
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

public class QueryVisitorImpl implements ElementVisitor {


	@Override
	public void visit(ElementTriplesBlock el) {
		System.out.println(0+": "+el);

	}

	@Override
	public void visit(ElementPathBlock el) {
		System.out.println(1+": "+el);
		System.out.println(el.getPattern().getList().get(0).getPath());
	}

	@Override
	public void visit(ElementFilter el) {
		System.out.println(2+": "+el);
	}

	@Override
	public void visit(ElementAssign el) {
		System.out.println(3+": "+el);
	}

	@Override
	public void visit(ElementBind el) {
		System.out.println(4+": "+el);
	}

	@Override
	public void visit(ElementData el) {
		System.out.println(5+": "+el);
	}

	@Override
	public void visit(ElementUnion el) {
		System.out.println(6+": "+el);
	}

	@Override
	public void visit(ElementOptional el) {
		System.out.println(7+": "+el.getOptionalElement());
	}

	@Override
	public void visit(ElementGroup el) {
//		System.out.println(8+": "+el);
	}

	@Override
	public void visit(ElementDataset el) {
		System.out.println(9+": "+el);
	}

	@Override
	public void visit(ElementNamedGraph el) {
		System.out.println(10+": "+el.getElement());
	}

	@Override
	public void visit(ElementExists el) {
		System.out.println(11+": "+el);
	}

	@Override
	public void visit(ElementNotExists el) {
		System.out.println(12+": "+el);
	}

	@Override
	public void visit(ElementMinus el) {
		System.out.println(13+": "+el);
	}

	@Override
	public void visit(ElementService el) {
		System.out.println(14+": "+el);
	}

	@Override
	public void visit(ElementSubQuery el) {
		System.out.println(15+": "+el);
	}

}
