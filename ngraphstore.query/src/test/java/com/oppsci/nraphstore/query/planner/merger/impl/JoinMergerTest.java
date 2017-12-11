package com.oppsci.nraphstore.query.planner.merger.impl;

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.oppsci.ngraphstore.graph.elements.Node;
import com.oppsci.ngraphstore.graph.elements.NodeFactory;
import com.oppsci.ngraphstore.query.planner.merger.impl.JoinMerger;
import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

public class JoinMergerTest {

	@Test
	public void emptyTest() {
		SimpleResultSet res = new SimpleResultSet();
		JoinMerger joinMerger = new JoinMerger();
		SimpleResultSet join = joinMerger.merge(res, res);
		assertTrue(join.getRows().isEmpty());
		assertTrue(join.getVars().isEmpty());
	}

	@Test
	public void oneEmptyTest() {
		SimpleResultSet empty = new SimpleResultSet();
		SimpleResultSet res = create(new String[] { "o", "s" },
				new String[][] { new String[] { "urn://test", "urn://test2" } });
		JoinMerger joinMerger = new JoinMerger();
		SimpleResultSet join = joinMerger.merge(empty, res);
		assertTrue(join.getRows().isEmpty());
		assertTrue(join.getVars().isEmpty());
		join = joinMerger.merge(res, empty);
		assertTrue(join.getRows().isEmpty());
		assertTrue(join.getVars().isEmpty());
	}

	@Test
	public void checkJoin() {
		SimpleResultSet res1 = create(new String[] { "o", "s" }, new String[][] {
				new String[] { "urn://test", "urn://test2" }, new String[] { "urn://abc", "urn://abc" } });
		SimpleResultSet res2 = create(new String[] { "p", "s" },
				new String[][] { new String[] { "urn://test1", "urn://test" },
						new String[] { "urn://test1", "urn://test2" }, new String[] { "urn://testABC", "urn://test2" } });
		JoinMerger joinMerger = new JoinMerger();

		// result set must be ?s ?p ?o -> [<urn://test2> <urn://test1> <urn://test>][<urn://test2> <urn://testABC> <urn://test>]
		SimpleResultSet join = joinMerger.merge(res1, res2);
		assertTrue(join.getVars().size()==3);
		assertTrue(join.getVars().contains("s"));
		assertTrue(join.getVars().contains("p"));
		assertTrue(join.getVars().contains("o"));
				
		assertTrue(join.getRows().size()==2);
		List<String> expected = new LinkedList<String>();
		expected.add("<urn://test2>");
		expected.add("<urn://test1>");
		expected.add("<urn://test>");
		assertTrue(checkRow(expected, join.getRows()));
		expected = new LinkedList<String>();
		expected.add("<urn://test2>");
		expected.add("<urn://testABC>");
		expected.add("<urn://test>");
		assertTrue(checkRow(expected, join.getRows()));
		
	}

	private boolean checkRow(List<String> expected, Collection<Node[]> nodes) {
		for(Node[] node : nodes) {
			boolean equals=expected.size()==node.length;
			for(Node n : node) {
				equals&=expected.contains(n.getNode());
			}
			if(equals) {
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
