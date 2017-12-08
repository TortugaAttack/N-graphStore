package com.oppsci.ngraphstore.storage.lucene.spec;

import org.apache.lucene.search.ScoreDoc;

public class SearchStats {

	private ScoreDoc lastDoc;
	private int lastHit;

	private int totalHits;

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
	public int getTotalHits() {
		return totalHits;
	}

	/**
	 * @param totalHits
	 *            the totalHits to set
	 */
	public void setTotalHits(int totalHits) {
		this.totalHits = totalHits;
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
