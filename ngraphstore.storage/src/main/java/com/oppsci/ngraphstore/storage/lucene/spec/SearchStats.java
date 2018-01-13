package com.oppsci.ngraphstore.storage.lucene.spec;

import org.apache.lucene.search.ScoreDoc;

public class SearchStats {

	private ScoreDoc lastDoc;
	private int lastHit;

	private long totalHits;

	public boolean hasMoreResults() {
		return totalHits > lastHit;
	}

	/**
	 * @return the lastDoc
	 */
	public ScoreDoc getLastDoc() {
		return lastDoc;
	}

	/**
	 * @param lastDoc
	 *            the lastDoc to set
	 */
	public void setLastDoc(ScoreDoc lastDoc) {
		this.lastDoc = lastDoc;
	}

	/**
	 * @return the totalHits
	 */
	public long getTotalHits() {
		return totalHits;
	}

	/**
	 * @param totalHits2
	 *            the totalHits to set
	 */
	public void setTotalHits(long totalHits2) {
		this.totalHits = totalHits2;
	}

	/**
	 * @return the lastHit
	 */
	public int getLastHit() {
		return lastHit;
	}

	/**
	 * @param lastHit
	 *            the lastHit to set
	 */
	public void setLastHit(int lastHit) {
		this.lastHit = lastHit;
	}

}
