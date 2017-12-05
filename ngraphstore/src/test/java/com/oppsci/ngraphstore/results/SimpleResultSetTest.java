package com.oppsci.ngraphstore.results;

import java.io.File;
import java.net.MalformedURLException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.junit.Test;

public class SimpleResultSetTest {

	@Test
	public void jsonTest() throws MalformedURLException {
		Model m = ModelFactory.createDefaultModel();
		m.read(new File("data.nt").toURI().toURL().toString());
		StmtIterator statements = m.listStatements();
		while(statements.hasNext()) {
			Statement stmt = statements.next();
			String subject = "<"+stmt.getSubject().getURI()+">";
			String predicate = stmt.getPredicate().getURI();
			//Tags??
			String object;
			if(stmt.getObject().isLiteral()) {
				 object = stmt.getObject().asNode().toString(true);
				if(stmt.getObject().asLiteral().getLanguage().isEmpty()&&!stmt.getObject().asLiteral().getDatatypeURI().equals("http://www.w3.org/2001/XMLSchema#string")) {
					object = object.substring(0, object.lastIndexOf("^^")+2)+"<"+stmt.getObject().asLiteral().getDatatypeURI()+">";
				}
			}
			else {
				object = stmt.getObject().asNode().toString(true);
			}
			System.out.println(subject+" "+predicate+" "+object);
		}
	}
	
}
