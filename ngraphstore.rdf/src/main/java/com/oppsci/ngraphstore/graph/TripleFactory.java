package com.oppsci.ngraphstore.graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

public class TripleFactory {

	public static Triple<String>[] parseTriples(String triples) throws IOException {
		BufferedReader reader = new BufferedReader(new StringReader(triples));

		Model m = ModelFactory.createDefaultModel();
		m.read(reader, null, "TTL");
		StmtIterator statements = m.listStatements();
		List<Triple<String>> tripleList = new LinkedList<Triple<String>>();
		while (statements.hasNext()) {
			Statement stmt = statements.next();
			Triple<String> triple = new Triple<String>();
			if (stmt.getSubject().isURIResource()) {
				triple.setSubject("<" + stmt.getSubject().getURI() + ">");
			} else if (stmt.getSubject().isAnon()) {
				// make bnode unique but usable
				triple.setSubject("_:"+stmt.getSubject().toString());
			}
			if (stmt.getPredicate().isURIResource()) {
				triple.setPredicate("<" + stmt.getPredicate().getURI() + ">");
			} else if (stmt.getPredicate().isAnon()) {
				// make bnode unique but usable
				triple.setPredicate("_:"+stmt.getPredicate().toString());
			}
			String object;
			if (stmt.getObject().isLiteral()) {
				Literal literal = stmt.getObject().asLiteral();
				object = stmt.getObject().asNode().toString(true);
				if (literal.getLanguage().isEmpty()
						&& !literal.getDatatypeURI().equals("http://www.w3.org/2001/XMLSchema#string")) {
					object = object.substring(0, object.lastIndexOf("^^") + 2) + "<" + literal.getDatatypeURI() + ">";
				}
			} else if (stmt.getObject().isAnon()) {
				object = "_:"+stmt.getObject().asNode().toString();
			} else {
				object = "<" + stmt.getObject().asNode().getURI() + ">";
			}
			triple.setObject(object);
			tripleList.add(triple);
		}
		return tripleList.toArray(new Triple[] {});
	}
}
