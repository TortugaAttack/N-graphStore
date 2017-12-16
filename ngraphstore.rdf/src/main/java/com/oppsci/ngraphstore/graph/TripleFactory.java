package com.oppsci.ngraphstore.graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

public class TripleFactory {

	public static Triple<String>[] parseTriples(String triples) throws IOException {
		return parseTriples(triples, null);
	}

	public static Triple<String>[] parseTriples(String triples, String graph) throws IOException {
		BufferedReader reader = new BufferedReader(new StringReader(triples));

		Model m = ModelFactory.createDefaultModel();
		m.read(reader, null, "TTL");
		StmtIterator statements = m.listStatements();
		List<Triple<String>> tripleList = new LinkedList<Triple<String>>();
		while (statements.hasNext()) {
			Statement stmt = statements.next();
			Triple<String> triple = new Triple<String>();
			triple.setSubject(parseSubject(stmt.getSubject().asNode()));
			triple.setPredicate(parsePredicate(stmt.getPredicate().asNode()));	
			triple.setObject(parseObject(stmt.getObject().asNode()));
			triple.setGraph(graph);
			tripleList.add(triple);
		}
		return tripleList.toArray(new Triple[] {});
	}
	
	public static String parseSubject(Node node) {
		if (node.isURI()) {
			return "<" + node.getURI() + ">";
		} else {
			// make bnode unique but usable
			return "_:" + node.toString(); 
		}
	}
	
	public static String parsePredicate(Node node) {
		return "<" + node.getURI() + ">";
	}
	
	public static String parseObject(Node node) {
		String object;
		if (node.isLiteral()) {
			String lang = node.getLiteralLanguage();
			object = node.toString(true);
			if (lang.isEmpty()
					&& !node.getLiteralDatatypeURI().equals("http://www.w3.org/2001/XMLSchema#string")) {
				object = object.substring(0, object.lastIndexOf("^^") + 2) + "<" + node.getLiteralDatatypeURI()+ ">";
			}
		} else if (node.isBlank()) {
			object = "_:" + node.toString();
		} else {
			object = "<" + node.getURI() + ">";
		}
		return object;
	}
}
