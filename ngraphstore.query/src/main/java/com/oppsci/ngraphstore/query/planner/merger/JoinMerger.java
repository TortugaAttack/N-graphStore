package com.oppsci.ngraphstore.query.planner.merger;

import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

/**
 * Joins two ResultSets into one.
 * For all values of the second table check which ones of the first fits. 
 * add merged rows to new table
 * 
 * @author f.conrads
 *
 */
public class JoinMerger implements Merger{

	@Override
	public SimpleResultSet merge(SimpleResultSet oldRS, SimpleResultSet newRS) {
		// TODO Auto-generated method stub
		return null;
	}

}
