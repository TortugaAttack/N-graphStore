package com.oppsci.ngraphstore.query.planner.merger.impl;

import java.util.LinkedList;
import java.util.List;

import com.oppsci.ngraphstore.graph.elements.Node;
import com.oppsci.ngraphstore.query.planner.merger.Merger;
import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

/**
 * Adds second Table to first
 * 
 * @author f.conrads
 *
 */
public class AddMerger implements Merger {

	@Override
	public SimpleResultSet merge(SimpleResultSet oldRS, SimpleResultSet newRS) {
		SimpleResultSet res = new SimpleResultSet();
		//set joined vars
		res.setVars(joinVars(oldRS.getVars(), newRS.getVars()));
		for(Node[] node : oldRS.getRows()) {
			//add nulls for empty results
			Node[] nullifiedNode = new Node[res.getVars().size()]; 
			for(int i=0;i<node.length;i++) {
				nullifiedNode[i]=node[i];
			}
			res.addRow(nullifiedNode);
		}
		for(Node[] node : newRS.getRows()) {
			//add nulls for empty results
			Node[] nullifiedNode = new Node[res.getVars().size()]; 
			int offset=res.getVars().size()-newRS.getVars().size();
			for(int i=0;i<node.length;i++) {
				
				nullifiedNode[i+offset]=node[i];
			}
			res.addRow(nullifiedNode);
		}
		return res;
	}
	
	/**
	 * Joins two variable sets
	 * 
	 * @param vars1
	 * @param vars2
	 * @return
	 */
	public List<String> joinVars(List<String> vars1, List<String> vars2) {
		List<String> join = new LinkedList<String>();

		join.addAll(vars1);
		for (String varIn2 : vars2) {
			if (!join.contains(varIn2)) {
				join.add(varIn2);
			}
		}
		return join;
	}



}
