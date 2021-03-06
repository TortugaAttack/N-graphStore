package com.oppsci.ngraphstore.query.planner.merger.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.oppsci.ngraphstore.graph.elements.Node;
import com.oppsci.ngraphstore.query.planner.merger.Merger;
import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

/**
 * Joins two ResultSets into one. For all values of the second table check which
 * ones of the first fits. add merged rows to new table
 * 
 * @author f.conrads
 *
 */
public class JoinMerger implements Merger {

	/**
	 * Joins two variable sets
	 * 
	 * @param vars1
	 * @param vars2
	 * @return
	 */
	public List<String> joinVars(List<String> vars1, List<String> vars2) {
		List<String> join = new LinkedList<String>();
		if(vars1.isEmpty() || vars2.isEmpty()) {
			return join;
		}
		join.addAll(vars1);
		for (String varIn2 : vars2) {
			if (!join.contains(varIn2)) {
				join.add(varIn2);
			}
		}
		return join;
	}
	
	@Override
	public SimpleResultSet merge(SimpleResultSet oldRS, SimpleResultSet newRS) {
		SimpleResultSet join = new SimpleResultSet();
		join.setVars(joinVars(oldRS.getVars(), newRS.getVars()));
		join.setRows(joinTables(oldRS, newRS));
		return join;
	}

	protected List<Node[]> joinTables(SimpleResultSet oldRS, SimpleResultSet newRS) {
		List<Node[]> join = new LinkedList<Node[]>();
		Collection<Node[]> table1 = oldRS.getRows();
		Collection<Node[]> table2 = newRS.getRows();
		// get indexes where oldRS and newRS are the same.
		List<Integer[]> indexMapping = indexMapping(oldRS.getVars(), newRS.getVars());
		if (indexMapping.isEmpty() && !table1.isEmpty() && !table2.isEmpty()) {
			// no variables maps -> is cartesian product.
			return cartesianJoin(oldRS, newRS);
		}
		for (Node[] node1 : table1) {

			for (Node[] node2 : table2) {
				boolean nodeDoesMatch = true;
				// check if match for all index matches
				for (Integer[] indexMatch : indexMapping) {
					if (!node1[indexMatch[0]].getNode().equals(node2[indexMatch[1]].getNode())) {
						// as soon as no match break
						nodeDoesMatch = false;
						break;
					}
				}
				if (nodeDoesMatch) {
					// only true if node matches in all indexMappings
					join.add(joinNodes(node1, node2, indexMapping));
				}
			}
		}
		return join;
	}

	protected List<Node[]> cartesianJoin(SimpleResultSet oldRS, SimpleResultSet newRS) {
		List<Node[]> cartesianProduct = new LinkedList<Node[]>();
		for (Node[] node1 : oldRS.getRows()) {
			for (Node[] node2 : newRS.getRows()) {
				cartesianProduct.add(joinNodes(node1, node2, new LinkedList<Integer[]>()));
			}
		}
		
		return cartesianProduct;
	}

	protected Node[] joinNodes(Node[] node1, Node[] node2, List<Integer[]> indexMapping) {
		Node[] join = new Node[node1.length + node2.length - indexMapping.size()];
		int i = 0;
		for (; i < node1.length; i++) {
			// add all from node1
			join[i] = node1[i];
		}
		// add all missing from node2
		int actualIndex = 0;
		for (int j = 0; j < node2.length; j++) {
			boolean notMapped = true;
			for (Integer[] indexMatch : indexMapping) {
				notMapped = !indexMatch[1].equals(j);
				if (!notMapped) {
					// mapping exists, no need to check further
					break;
				}
			}
			if (notMapped) {
				join[i + actualIndex] = node2[j];
				actualIndex++;
			}
		}

		return join;
	}

	protected List<Integer[]> indexMapping(List<String> vars1, List<String> vars2) {
		List<Integer[]> indexMapping = new LinkedList<Integer[]>();
		for (int i = 0; i < vars2.size(); i++) {
			String varIn2 = vars2.get(i);
			if (vars1.contains(varIn2)) {
				indexMapping.add(new Integer[] { vars1.indexOf(varIn2), i });
			}
		}
		return indexMapping;
	}



}
