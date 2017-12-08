package com.oppsci.ngraphstore.query.planner;

import org.apache.lucene.search.ScoreDoc;

import com.oppsci.ngraphstore.query.sparql.elements.Aggregation;
import com.oppsci.ngraphstore.storage.ClusterOverseer;
import com.oppsci.ngraphstore.storage.lucene.spec.LuceneSearchSpec;
import com.oppsci.ngraphstore.storage.lucene.spec.SearchStats;
import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

/**
 * One Step of a query plan
 * 
 * @author f.conrads
 *
 */
public class Step {

	// basic BGP
	private boolean isBasic;

	//
	private boolean isFilter;

	private Aggregation[] aggregations;

	private SearchStats stats = new SearchStats();

	private boolean isRemembered = false;

	private LuceneSearchSpec spec;

	public SimpleResultSet execute(ClusterOverseer overseer) throws Exception {
		if (isBasic) {

			SimpleResultSet results = overseer.search(spec, stats);
			this.stats = results.getStats();
			if (stats.hasMoreResults()) {
				this.setRemembered(true);
			} else {
				this.setRemembered(false);
			}
			return results;
		}
		return null;
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
