package com.oppsci.ngraphstore.graph.elements.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.oppsci.ngraphstore.graph.elements.impl.Literal;
import com.oppsci.ngraphstore.graph.elements.impl.URINode;

@RunWith(Parameterized.class)
public class LiteralTest {

	
	@Parameters
	public static Collection<Object[]> data() {
		List<Object[]> testConfigs = new ArrayList<Object[]>();

		testConfigs.add(new Object[] {"\"test\"", "test", null, true});
		testConfigs.add(new Object[] {"\"test'\"", "test'", null, true});
		testConfigs.add(new Object[] {"'test\"'", "test\"", null, true});

		testConfigs.add(new Object[] {"\"test\"@en", "test", "en", true});
		testConfigs.add(new Object[] {"\"test'\"@en", "test'", "en", true});
		testConfigs.add(new Object[] {"'test\"'@en", "test\"", "en", true});
		
		testConfigs.add(new Object[] {"\"test\"^^<urn://test>", "test", "urn://test", false});
		testConfigs.add(new Object[] {"\"test'\"^^<urn://test>", "test'", "urn://test", false});
		testConfigs.add(new Object[] {"'test\"'^^<urn://test>", "test\"", "urn://test", false});

		
		
		return testConfigs;
	}



	private String value;

	private String node;

	private boolean isLang;

	private String tag;

	
	
	public LiteralTest(String node, String value, String tag, boolean isLang) {
		this.node=node;
		this.value=value;
		this.tag=tag;
		this.isLang=isLang;
	}
	

	
	@Test
	public void literalTest() {
		Literal l = new Literal(value);
		assertEquals(value, l.getValue());
		assertEquals(null, l.getLangTag());
		assertEquals(null, l.getDatatype());
		
		l = new Literal(value, tag, isLang);
		assertEquals(value, l.getValue());
		assertEquals(node, l.getNode());
		if(isLang) {
			assertEquals(tag, l.getLangTag());
			assertEquals(null, l.getDatatype());
		}
		else {
			assertEquals(null, l.getLangTag());
			assertEquals(tag, l.getDatatype());
		}
		
		

	}
}
