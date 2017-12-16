package com.oppsci.nraphstore.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.oppsci.ngraphstore.graph.TripleFactory;
import com.oppsci.ngraphstore.graph.elements.Node;
import com.oppsci.ngraphstore.query.planner.impl.QueryPlannerImpl;
import com.oppsci.ngraphstore.storage.cluster.overseer.impl.ClusterOverseerImpl;
import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

@RunWith(Parameterized.class)
public class QueryPlannerTest {

	private String uuid;
	private ClusterOverseerImpl overseer;
	private String queryString;
	private File expectedFile;
	
	@Parameters
	public static Collection<Object[]> data() {
		List<Object[]> testConfigs = new ArrayList<Object[]>();

		testConfigs.add(new Object[] { "SELECT ?s ?p {?s ?p ?o}", "src/test/resources/full.tsv"});
		testConfigs.add(new Object[] { "SELECT DISTINCT ?g {Graph ?g {?s ?p ?o}}", "src/test/resources/graph.tsv"});
		testConfigs.add(new Object[] { "SELECT * {{?s <urn://b> ?o} UNION {?s <urn://b2> ?o}}", "src/test/resources/union.tsv"});
		testConfigs.add(new Object[] { "SELECT ?v ?s ?o  {?s <http://www.w3.org/2000/01/rdf-schema#label> ?o . <u://book1> ?v ?s}", "src/test/resources/join.tsv"});
		testConfigs.add(new Object[] { "SELECT ?p ?o {<urn://not> ?p ?o}", "src/test/resources/empty.tsv"});
		
		return testConfigs;
	}
	
	public QueryPlannerTest(String queryString, String expectedFile) throws MalformedURLException {
		this.queryString=queryString;
		this.expectedFile = new File(expectedFile);
	}
	
	@Before
	public void init() throws Exception {
		uuid = UUID.randomUUID().toString();
		//load datatest
		overseer = new ClusterOverseerImpl(uuid, 2, 180, false);
		//load files as String and load it into the database
		String data = FileUtils.readFileToString(new File("src/test/resources/test1.nt"), Charset.forName("UTF-8"));
		overseer.load(TripleFactory.parseTriples(data, "<urn://a>"));
		data = FileUtils.readFileToString(new File("src/test/resources/test2.nt"), Charset.forName("UTF-8"));
		overseer.add(TripleFactory.parseTriples(data, "<urn://b>"));
	}
	
	@After
	public void close() throws IOException {
		FileUtils.deleteDirectory(new File(uuid));
	}
	
	@Test
	public void queryTest() throws Exception {
		//read expected from given file
		//SimpleResultSet to model
		QueryPlannerImpl planner = new QueryPlannerImpl(overseer);
		SimpleResultSet res = planner.select(queryString);
		List<String> csvContent = FileUtils.readLines(expectedFile, "UTF-8");
		assertTrue(csvContent.size()==res.getRows().size());
		for(Node[] nodes : res.getRows()) {
			//check if nodes in expected Files.
			String[] strNode = node2String(nodes);
			StringBuilder builder = new StringBuilder();
			for(int i=0;i<strNode.length-1;i++) {
				builder.append(strNode[i]).append("\t");
			}
			builder.append(strNode[strNode.length-1]);
			assertTrue(csvContent.contains(builder.toString()));
		}
	}
	
	private String[] node2String(Node[] nodes) {
		String[] ret = new String[nodes.length];
		int i=0;
		for(Node node : nodes) {
			ret[i++]  = node.getNode();
		}
		return ret;
	}
}
