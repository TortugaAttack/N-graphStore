package com.oppsci.ngraphstore.results;

import java.io.File;
import java.net.MalformedURLException;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.junit.Test;

public class SimpleResultSetTest {
	
	@Test
	public void sandbox() {
		Query q = QueryFactory.create("SELECT * {?s ?p ?o}");
		QueryExecution qexec = QueryExecutionFactory.createServiceRequest("http://localhost:9098/ngraphstore/api/sparql", q);
		ResultSet res = qexec.execSelect();
		while(res.hasNext()) {
			System.out.println(res.next().toString());
		}
		
		UpdateRequest request = new UpdateRequest();
		request.add("INSERT DATA { <http://example/egbook3> <dc:title>  \"This is an example title\" }" );
		
		UpdateProcessor exec = UpdateExecutionFactory.createRemote(request, "http://localhost:9098/ngraphstore/api/auth/update");
		exec.execute();
	}

//	@Test
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
