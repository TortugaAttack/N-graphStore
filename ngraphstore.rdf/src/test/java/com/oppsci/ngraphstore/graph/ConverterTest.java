package com.oppsci.ngraphstore.graph;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ConverterTest {

	private String expected;
	private String nTriples;

	@Parameters
	public static Collection<Object[]> data() {
		List<Object[]> testConfigs = new ArrayList<Object[]>();

		testConfigs.add(new Object[] {
				"{\"@graph\":[{\"@type\":\"http:\\/\\/www.w3.org\\/2002\\/07\\/owl#FunctionalProperty\",\"@id\":\"http:\\/\\/dbpedia.org\\/ontology\\/birthDate\"},{\"@type\":\"http:\\/\\/www.w3.org\\/2002\\/07\\/owl#FunctionalProperty\",\"@id\":\"http:\\/\\/dbpedia.org\\/ontology\\/deathDate\"}]}",
				"<http://dbpedia.org/ontology/birthDate>	<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	<http://www.w3.org/2002/07/owl#FunctionalProperty> ."
						+ "<http://dbpedia.org/ontology/deathDate>	<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	<http://www.w3.org/2002/07/owl#FunctionalProperty> ." });

		return testConfigs;
	}

	public ConverterTest(String expected, String nTriples) {
		this.expected = expected;
		this.nTriples = nTriples;

	}

	@Test
	public void checkConversion() throws ParseException {
		Model model = ModelFactory.createDefaultModel();
		model.read(new StringReader(nTriples), null, "N-TRIPLE");
		JSONObject actual = Model2JSONConverter.convert(model);
		assertEquals(expected, actual.toJSONString());
	}

}
