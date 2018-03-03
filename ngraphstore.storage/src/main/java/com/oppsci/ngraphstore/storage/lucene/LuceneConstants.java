package com.oppsci.ngraphstore.storage.lucene;

/**
 * Constants for lucene
 * 
 * @author f.conrads
 *
 */
public class LuceneConstants {

	/**
	 * The Lucene internal MAX_SEARCH amount
	 */
	public static int MAX_SEARCH=1000000;
	
	/**
	 * The lucene field name for subjects
	 */
	public final static String SUBJECT="subject";	
	/**
	 * The lucene field name for predicates
	 */
	public final static String PREDICATE="predicate";
	/**
	 * The lucene field name for objects
	 */
	public final static String OBJECT="object";
	/**
	 * The lucene field name for graphs
	 */
	public static final String GRAPH = "graph";
}
