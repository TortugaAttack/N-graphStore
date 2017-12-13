package com.oppsci.ngraphstore.query.planner.merger.impl;

import com.oppsci.ngraphstore.graph.elements.Node;
import com.oppsci.ngraphstore.query.planner.merger.AbstractMerger;
import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

/**
 * Adds second Table to first
 * 
 * @author f.conrads
 *
 */
public class AddMerger extends AbstractMerger {

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

}
