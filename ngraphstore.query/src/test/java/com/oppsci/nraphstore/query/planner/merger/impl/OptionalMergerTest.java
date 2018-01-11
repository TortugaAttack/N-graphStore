package com.oppsci.nraphstore.query.planner.merger.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.oppsci.ngraphstore.graph.elements.Node;
import com.oppsci.ngraphstore.graph.elements.NodeFactory;
import com.oppsci.ngraphstore.query.planner.merger.impl.OptionalMerger;
import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

/**
 * Unit Test for the add merger
 * 
 * @author f.conrads
 *
 */
public class OptionalMergerTest {

	@Test
	public void emptyTest() {
		SimpleResultSet res = new SimpleResultSet();
		OptionalMerger OptionalMerger = new OptionalMerger();
		SimpleResultSet join = OptionalMerger.merge(res, res);
		assertTrue(join.getRows().isEmpty());
		assertTrue(join.getVars().isEmpty());
	}

	@Test
	public void oneEmptyTest() {
		SimpleResultSet empty = new SimpleResultSet();
		SimpleResultSet res = create(new String[] { "o", "s" },
				new String[][] { new String[] { "urn://test", "urn://test2" } });
		OptionalMerger OptionalMerger = new OptionalMerger();
		SimpleResultSet join = OptionalMerger.merge(res, empty);
		
		List<String> expected = new LinkedList<String>();
		expected.add("<urn://test2>");
		expected.add("<urn://test>");
		assertTrue(checkRow(expected, join.getRows()));
		assertEquals(2, join.getVars().size());
		assertTrue(join.getVars().contains("s"));
		assertTrue(join.getVars().contains("o"));
		join = OptionalMerger.merge(empty, res);
		assertTrue(join.getRows().isEmpty());
		assertTrue(join.getVars().isEmpty());
	}

	@Test
	public void checkJoin() {
		SimpleResultSet res1 = create(new String[] { "o", "s" }, new String[][] {
				new String[] { "urn://test", "urn://test2" }, new String[] { "urn://abc", "urn://abc" } });
		SimpleResultSet res2 = create(new String[] { "p", "s" },
				new String[][] { new String[] { "urn://test1", "urn://test" },
						new String[] { "urn://test1", "urn://test2" },
						new String[] { "urn://testABC", "urn://test2" } });
		OptionalMerger OptionalMerger = new OptionalMerger();

		// result set must be ?s ?p ?o -> [<urn://test2> <urn://test1>
		// <urn://test>][<urn://test2> <urn://testABC> <urn://test>]
		SimpleResultSet join = OptionalMerger.merge(res1, res2);
		assertEquals(3, join.getVars().size());
		assertTrue(join.getVars().contains("s"));
		assertTrue(join.getVars().contains("p"));
		assertTrue(join.getVars().contains("o"));

		assertEquals(3, join.getRows().size());
		List<String> expected = new LinkedList<String>();
		expected.add("<urn://test2>");
		expected.add("<urn://test1>");
		expected.add("<urn://test>");
		assertTrue(checkRow(expected, join.getRows()));
		expected = new LinkedList<String>();
		expected.add("<urn://abc>");
		expected.add(null);
		expected.add("<urn://abc>");
		assertTrue(checkRow(expected, join.getRows()));
		expected = new LinkedList<String>();
		expected.add("<urn://test2>");
		expected.add("<urn://testABC>");
		expected.add("<urn://test>");
		assertTrue(checkRow(expected, join.getRows()));
	}


	private boolean checkRow(List<String> expected, Collection<Node[]> nodes) {
		for (Node[] node : nodes) {
			boolean equals = expected.size() == node.length;
			for (Node n : node) {
				if(expected!=null && n!=null) {
					equals &= expected.contains(n.getNode());
				}
				else if(expected!=null) {
					
				}
				else {
					equals &= n==null;
				}
			}
			if (equals) {
				return true;
			}
		}
		return false;
	}

	private SimpleResultSet create(String[] varsArr, String[][] uris) {
		SimpleResultSet res = new SimpleResultSet();
		List<String> vars = new LinkedList<String>();
		for (String var : varsArr) {
			vars.add(var);
		}
		List<Node[]> rows = new LinkedList<Node[]>();
		for (int i = 0; i < uris.length; i++) {
			Node[] node = new Node[uris[i].length];
			for (int j = 0; j < uris[i].length; j++) {
				node[j] = NodeFactory.createURI(uris[i][j]);
			}
			rows.add(node);
		}
		res.addRows(rows);
		res.setVars(vars);
		return res;
	}

}
