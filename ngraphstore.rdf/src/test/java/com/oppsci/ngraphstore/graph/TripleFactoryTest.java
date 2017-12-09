package com.oppsci.ngraphstore.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.jena.riot.RiotException;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TripleFactoryTest {
	
	private String[][] expected;
	private String nTriples;
	private boolean exceptionExpected;

	@Parameters
	public static Collection<Object[]> data() {
		List<Object[]> testConfigs = new ArrayList<Object[]>();

		testConfigs.add(new Object[] { new String[][] {
				{ "<http://dbpedia.org/ontology/birthDate>", "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>",
						"<http://www.w3.org/2002/07/owl#FunctionalProperty>", null },
				{ "<http://dbpedia.org/ontology/deathDate>", "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>",
						"<http://www.w3.org/2002/07/owl#FunctionalProperty>", null } },
				"<http://dbpedia.org/ontology/birthDate>	<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	<http://www.w3.org/2002/07/owl#FunctionalProperty> ."
						+ "<http://dbpedia.org/ontology/deathDate>	<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	<http://www.w3.org/2002/07/owl#FunctionalProperty> .",
				false });
		testConfigs.add(new Object[] {
				new String[][] { { "_:b1", "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>",
						"<http://www.w3.org/2002/07/owl#FunctionalProperty>", null },
						{ "<http://dbpedia.org/ontology/deathDate>",
								"<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", "_:b1", null } },
				"_:b1	<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	<http://www.w3.org/2002/07/owl#FunctionalProperty> ."
						+ "<http://dbpedia.org/ontology/deathDate>	<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	_:b1.",
				false });
		testConfigs.add(new Object[] {
				new String[][] { { "<http://dbpedia.org/ontology/birthDate>",
						"<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", "\"test\"", null },
						{ "<http://dbpedia.org/ontology/deathDate>",
								"<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", "\"test\"@en", null } },
				"<http://dbpedia.org/ontology/birthDate>	<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	\"test\" ."
						+ "<http://dbpedia.org/ontology/deathDate>	<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	\"test\"@en.",
				false });
		testConfigs.add(new Object[] {
				new String[][] { { "<http://dbpedia.org/ontology/deathDate>",
						"<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", "\"test\"^^<urn://test>", null } },
				"<http://dbpedia.org/ontology/deathDate>	<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	\"test\"^^<urn://test>",
				false });
		testConfigs.add(new Object[] {
				new String[][] { { "http://dbpedia.org/ontology/deathDate>",
						"<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", "\"test\"^^<urn://test>", null } },
				"http://dbpedia.org/ontology/deathDate>	<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	\"test\"^^<urn://test>",
				true });

		return testConfigs;
	}

	public TripleFactoryTest(String[][] expected, String nTriples, boolean exceptionExpected) {
		this.expected = expected;
		this.nTriples = nTriples;
		this.exceptionExpected = exceptionExpected;
	}

	@Test
	public void checkTriples() throws ParseException, IOException {
		Triple<String>[] actual;
		try {
			actual = TripleFactory.parseTriples(nTriples);
		} catch (RiotException e) {
			assertTrue(exceptionExpected);
			return;

		}
		assertEquals(expected.length, actual.length);
		for (Triple<String> triple : actual) {
			boolean contains = false;
			for (String[] expectedTriple : expected) {
				boolean equals;
				if (expectedTriple[0].startsWith("_:")) {
					equals = triple.getSubject().startsWith("_:");
				} else {
					equals = expectedTriple[0].equals(triple.getSubject());
				}
				equals &= expectedTriple[1].equals(triple.getPredicate());
				if (expectedTriple[2].startsWith("_:")) {
					equals &= triple.getObject().startsWith("_:");
				} else {
					equals &= expectedTriple[2].equals(triple.getObject());
				}
				contains = equals;
				if (contains)
					break;
			}
			assertTrue(contains);
		}
	}

}
