package com.oppsci.ngraphstore.query.planner.merger;

import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

/**
 * Merger interface to merge two SimpleResultSets together
 * 
 * @author f.conrads
 *
 */
public interface Merger {

	public SimpleResultSet merge(SimpleResultSet oldRS, SimpleResultSet newRS);

}
