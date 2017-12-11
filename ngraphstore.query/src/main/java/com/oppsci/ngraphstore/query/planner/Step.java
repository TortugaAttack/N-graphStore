package com.oppsci.ngraphstore.query.planner;

import com.oppsci.ngraphstore.storage.cluster.overseer.ClusterOverseer;
import com.oppsci.ngraphstore.storage.lucene.spec.SearchStats;
import com.oppsci.ngraphstore.storage.lucene.spec.impl.LuceneSearchSpec;
import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

/**
 * One Step of a query plan
 * 
 * @author f.conrads
 *
 */
public class Step {

	private SearchStats stats = new SearchStats();

	private boolean isRemembered = false;

	private LuceneSearchSpec spec;

	public SimpleResultSet execute(ClusterOverseer<SimpleResultSet> overseer) throws Exception {

		SimpleResultSet results = overseer.search(spec, stats);
		this.stats = results.getStats();
		if (stats.hasMoreResults()) {
			this.setRemembered(true);
		} else {
			this.setRemembered(false);
		}
		return results;

	}

	/**
	 * @return the isRemembered
	 */
	public boolean isRemembered() {
		return isRemembered;
	}

	/**
	 * @param isRemembered
	 *            the isRemembered to set
	 */
	public void setRemembered(boolean isRemembered) {
		this.isRemembered = isRemembered;
	}

	public void reset() {
		this.isRemembered = false;
		this.stats = new SearchStats();
	}
}
