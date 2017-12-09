package com.oppsci.ngraphstore.storage.lucene.spec.impl;

import com.oppsci.ngraphstore.storage.lucene.spec.LuceneSpec;

public class LuceneSearchSpec implements LuceneSpec {

	private String[] uris;
	private boolean[] objectsFlags;
	private String[] searchFields;
	
	public LuceneSearchSpec(String[] uris, boolean[] objectFlags, String[] searchFields) {
		this.setUris(uris);
		this.setObjectsFlags(objectFlags);
		this.setSearchFields(searchFields);
	}

	public boolean isSimple() {
		return uris.length==1;
	}
	
	/**
	 * @return the uris
	 */
	public String[] getUris() {
		return uris;
	}

	/**
	 * @param uris the uris to set
	 */
	public void setUris(String[] uris) {
		this.uris = uris;
	}

	/**
	 * @return the objectsFlags
	 */
	public boolean[] getObjectsFlags() {
		return objectsFlags;
	}

	/**
	 * @param objectsFlags the objectsFlags to set
	 */
	public void setObjectsFlags(boolean[] objectsFlags) {
		this.objectsFlags = objectsFlags;
	}

	/**
	 * @return the searchFields
	 */
	public String[] getSearchFields() {
		return searchFields;
	}

	/**
	 * @param searchFields the searchFields to set
	 */
	public void setSearchFields(String[] searchFields) {
		this.searchFields = searchFields;
	}
}
