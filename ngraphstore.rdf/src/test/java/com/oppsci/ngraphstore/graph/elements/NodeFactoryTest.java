package com.oppsci.ngraphstore.graph.elements;

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

import com.oppsci.ngraphstore.graph.elements.impl.URINode;

@RunWith(Parameterized.class)
public class NodeFactoryTest {

	private String node;
	private String json;
	private int type;
	
	@Parameters
	public static Collection<Object[]> data() {
		List<Object[]> testConfigs = new ArrayList<Object[]>();

		testConfigs.add(new Object[] {"<urn://test>", "{ \"value\":\"<urn://test>\", \"type\":\"uri\" }", 0});
		testConfigs.add(new Object[] {"_:b1", "{ \"value\":\"_:b1\", \"type\":\"bnode\" }", 1});
		testConfigs.add(new Object[] {"\"test\"", "{ \"value\":\"test\", \"type\":\"literal\" }", 2});
		testConfigs.add(new Object[] {"\"test\"^^<urn://test>", "{ \"value\":\"test\", \"datatype\":\"urn://test\" \"type\":\"typed-literal\" }", 2});
		testConfigs.add(new Object[] {"\"test\"@en", "{ \"value\":\"test\", \"langTag\":\"en\", \"type\":\"literal\" }", 2});
		testConfigs.add(new Object[] {"\"test\"@en", "{ \"value\":\"test\", \"langTag\":\"en\", \"type\":\"literal\" }", 2});
		testConfigs.add(new Object[] {"'''test'''", "{ \"value\":\"test\", \"type\":\"literal\" }", 2});
		testConfigs.add(new Object[] {"'test'", "{ \"value\":\"test\", \"type\":\"literal\" }", 2});
		
		return testConfigs;
	}

	
	public NodeFactoryTest(String node, String json, int type) {
		this.node =node;
		this.json=json;
		this.type=type;
	}
	
	@Test
	public void nodeCreationTest() throws ParseException {
		Node actualNode = NodeFactory.parseNode(node);
		if(type==0) {
			uriTest(actualNode);
		}
		else if(type==1) { 
			bnodeTest(actualNode);
		}
		else if(type==2) {
			literalTest(actualNode);
		}
		String actual = actualNode.asJSONString();
		System.out.println(actual);
		JSONParser parser = new JSONParser();
		
		assertEquals(parser.parse(json), parser.parse(actual));
	}
	
	private void uriTest(Node actualNode) {
		assertTrue(actualNode.isURI());
		assertFalse(actualNode.isBlankNode());
		assertFalse(actualNode.isLiteral());
		URINode uri = ((URINode)actualNode);
		assertEquals(node.replace("<", "").replace(">",""),uri.getValue());
		assertEquals(node, uri.getNode());
	}
	
	private void bnodeTest(Node actualNode) {
		assertFalse(actualNode.isURI());
		assertTrue(actualNode.isBlankNode());
		assertFalse(actualNode.isLiteral());
		assertEquals(node, actualNode.getValue());
		assertEquals(node, actualNode.getNode());
	}
	
	
	private void literalTest(Node actualNode) {
		assertFalse(actualNode.isURI());
		assertFalse(actualNode.isBlankNode());
		assertTrue(actualNode.isLiteral());
	}
}
