package com.oppsci.ngraphstore.query.planner.merger.impl;


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.oppsci.ngraphstore.graph.elements.Node;
import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

/**
 * Merger which locates variable and adds Values (in new column) to the row
 * @author f.conrads
 *
 */
public class OptionalMerger extends JoinMerger{

	@Override
	public SimpleResultSet merge(SimpleResultSet oldRS, SimpleResultSet newRS) {
		SimpleResultSet optionalMerge = new SimpleResultSet();
		//join variables
		optionalMerge.setVars(joinVars(oldRS.getVars(), newRS.getVars()));
		optionalMerge.setRows(joinTables(oldRS, newRS));
		
		
		return optionalMerge;
	}
	

	/**
	 * Joins two variable sets
	 * 
	 * @param vars1
	 * @param vars2
	 * @return
	 */
	@Override
	public List<String> joinVars(List<String> vars1, List<String> vars2) {
		List<String> join = new LinkedList<String>();
		if(vars1.isEmpty()) {
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
	public List<Node[]> joinTables(SimpleResultSet oldRS, SimpleResultSet newRS) {
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
			boolean noOptionalFlag = true;
			int size2 = 0;
			for (Node[] node2 : table2) {
				size2=node2.length;
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
					noOptionalFlag=false;
				}
			}
			if(noOptionalFlag) {
				Node[] noOptionalRow = new Node[node1.length + size2 - indexMapping.size()];
				int i=0;
				for(Node node : node1) {
					noOptionalRow[i++]=node;
				}
				join.add(noOptionalRow);
			}
		}
		return join;
	}

}
