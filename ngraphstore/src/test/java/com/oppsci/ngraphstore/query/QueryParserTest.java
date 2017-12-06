package com.oppsci.ngraphstore.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.oppsci.ngraphstore.query.parser.QueryParser;
import com.oppsci.ngraphstore.query.sparql.elements.BGPElement;

public class QueryParserTest {

	@Test
	public void prefixTest() {
		String prefix = "PREFIX abc: <urn:bla>";
		QueryParser parser = new QueryParser();
		Map<String, String> prefixes = parser.retrievePrefixMapping(prefix);
		assertEquals("<urn:bla>", prefixes.get("abc"));
		prefix = "  PREFIX abc: <urn:bla> PREFIX abc2: <urn:bla2>";
		prefixes = parser.retrievePrefixMapping(prefix);
		assertEquals("<urn:bla>", prefixes.get("abc"));
		assertEquals("<urn:bla2>", prefixes.get("abc2"));
	}
	
	@Test
	public void bgpTest() throws Exception {
		String bgp = "{?s <urn> \"test\"}";
		QueryParser parser = new QueryParser();
		List<BGPElement> elements = parser.retrieveBGPElements(bgp);
		assertTrue("?s".equals(elements.get(0).getSubject()));
		assertTrue(elements.get(0).getPredicate().equals("<urn>"));
		assertTrue(elements.get(0).getObject().equals("\"test\""));
		
		bgp = "{?s <urn> \"test\" ; <urn1> <bla> , <test> . <a> <b> \"lang\"@de, \"xsd\"^^<xsd:test>}";
		elements = parser.retrieveBGPElements(bgp);
		assertTrue("?s".equals(elements.get(0).getSubject()));
		assertTrue(elements.get(0).getPredicate().equals("<urn>"));
		assertTrue(elements.get(0).getObject().equals("\"test\""));
		assertTrue("?s".equals(elements.get(1).getSubject()));
		assertTrue("<urn1>".equals(elements.get(1).getPredicate()));
		assertTrue("<bla>".equals(elements.get(1).getObject()));
		assertTrue("?s".equals(elements.get(2).getSubject()));
		assertTrue("<urn1>".equals(elements.get(2).getPredicate()));
		assertTrue("<test>".equals(elements.get(2).getObject()));
		assertTrue("<a>".equals(elements.get(3).getSubject()));
		assertTrue("<b>".equals(elements.get(3).getPredicate()));
		assertTrue("\"lang\"@de".equals(elements.get(3).getObject()));
		assertTrue("<a>".equals(elements.get(4).getSubject()));
		assertTrue("<b>".equals(elements.get(4).getPredicate()));
		assertTrue("\"xsd\"^^<xsd:test>".equals(elements.get(4).getObject()));
	}
	
}
