package com.oppsci.ngraphstore.storage.lucene;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RegexpQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.NIOFSDirectory;

import com.oppsci.ngraphstore.graph.elements.Node;
import com.oppsci.ngraphstore.graph.elements.NodeFactory;
import com.oppsci.ngraphstore.storage.lucene.spec.SearchStats;

/**
 * The Lucene Searcher to find records in the lucene segments.
 * 
 * TODO add semaphore and mutex
 * 
 * 
 * @author f.conrads
 *
 */
public class LuceneSearcher {

	private IndexSearcher indexSearcher;
	private Directory indexDirectory;
	private IndexReader indexReader;
	private String path;
	
	private int maxSearch = LuceneConstants.MAX_SEARCH;

	/**
	 * Creates and opens a Lucene Searcher according to the given path
	 * 
	 * @param indexDirectoryPath
	 *            the path in which the indexing files should be.
	 * @throws IOException
	 */
	public LuceneSearcher(String indexDirectoryPath) throws IOException {
		open(indexDirectoryPath);
	}
	
	/**
	 * Creates and opens a Lucene Searcher according to the given path
	 * 
	 * @param indexDirectoryPath
	 *            the path in which the indexing files should be.
	 * @param maxSearch the internal lucene maximal documents at a time search
	 * @throws IOException
	 */
	public LuceneSearcher(String indexDirectoryPath, int maxSearch) throws IOException {
		this(indexDirectoryPath);
		this.maxSearch=maxSearch;
	}

	/**
	 * Will open the Lucene Searcher at the given path
	 * 
	 * @param indexDirectoryPath
	 *            the path in which the indexing files should be.
	 * @throws IOException
	 */
	public void open(String indexDirectoryPath) throws IOException {
		this.path = indexDirectoryPath;
		indexDirectory = MMapDirectory.open(new File(path).toPath());
		indexReader = DirectoryReader.open(indexDirectory);
		indexSearcher = new IndexSearcher(indexReader);
	}

	/**
	 * Will reopen the previously opened searcher
	 * 
	 * @throws IOException
	 */
	public void reopen() throws IOException {
		indexDirectory = MMapDirectory.open(new File(path).toPath());

		indexReader = DirectoryReader.open(indexDirectory);
		indexSearcher = new IndexSearcher(indexReader);
	}

	/**
	 * Will search the docs in which the searchQuery term will occur at the
	 * searchField
	 * 
	 * @param searchQuery
	 * @param searchField
	 * @return
	 * @throws IOException
	 */
	private TopDocs searchTops(String searchQuery, String searchField, SearchStats stats) throws IOException {
		return searchTerm(searchQuery, searchField, stats);
	}

	/**
	 * Will search the docs in which the searchQueries term will occur at the
	 * according searchFields
	 * 
	 * @param searchQueries
	 * @param searchFields
	 * @return
	 * @throws IOException
	 */
	private TopDocs searchTops(String[] searchQueries, String[] searchFields, SearchStats stats) throws IOException {
		return searchTerms(searchQueries, searchFields, stats);
	}

	private TopDocs searchTerms(String[] searchQueries, String[] searchFields, SearchStats stats) throws IOException {
		Builder finalQuery = new BooleanQuery.Builder();

		for (int i = 0; i < searchQueries.length; i++) {
			if (searchQueries[i].startsWith("_:")) {
				// bnode
				RegexpQuery query = new RegexpQuery(new Term(searchFields[i], "_:[^ ]+"));
				finalQuery.add(query, Occur.MUST);
			} else {
				TermQuery query = new TermQuery(new Term(searchFields[i], searchQueries[i]));
				finalQuery.add(query, Occur.MUST);
			}
		}
		return getTopDocsAfterSave(stats, finalQuery.build());


	}

	private TopDocs searchTerm(String searchQuery, String searchField, SearchStats stats) throws IOException {
		Query query;
		if (searchQuery.startsWith("_:")) {
			// bnode
			query = new RegexpQuery(new Term(searchField, "_:[^ ]+"));
		} else {
			query = new TermQuery(new Term(searchField, searchQuery));
		}
		return getTopDocsAfterSave(stats, query);
		
	}

	private Document getDocument(ScoreDoc scoreDoc) throws CorruptIndexException, IOException {
		return indexSearcher.doc(scoreDoc.doc);
	}

	/**
	 * Will close the searcher quietly
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		IOUtils.closeQuietly(indexReader);
		IOUtils.closeQuietly(indexDirectory);
	}

	/**
	 * Will search the term (uri) in the searchField and returns the 4 values
	 * (subject, predicate, object, graph) according to the objectsFlag.<br/>
	 * <br/>
	 * F.e. objectFlag: (true, false, false, true) will return subject, graph
	 * 
	 * @param uri
	 *            the term to search
	 * @param objectsFlag
	 * @param searchField
	 *            the searchField to search in
	 * @param stats
	 * @return
	 * @throws IOException
	 */
	public Collection<Node[]> search(String uri, boolean[] objectsFlag, String searchField, SearchStats stats)
			throws IOException {
		return searchRelation(uri, objectsFlag, searchField, stats);
	}

	/**
	 * Will search the term (uri) in the searchField and returns the 4 values
	 * (subject, predicate, object, graph) according to the objectsFlag.<br/>
	 * <br/>
	 * F.e. objectFlag: (true, false, false, true) will return subject, graph
	 * 
	 * @param uri
	 *            the term to search
	 * @param objectsFlag
	 * @param searchField
	 *            the searchField to search in
	 * @param stats
	 * @return
	 * @throws IOException
	 */
	public Collection<Node[]> searchRelation(String uri, boolean[] objectsFlag, String searchField, SearchStats stats)
			throws IOException {
		TopDocs docs;
		long start = Calendar.getInstance().getTimeInMillis();
		docs = searchTops(uri, searchField, stats);
		long end = Calendar.getInstance().getTimeInMillis();
		System.out.println("search Tops took "+(end-start)+"ms");
		return searchTopDocs(docs, objectsFlag, stats);
	}

	/**
	 * Will search the terms (uris) in the searchFields and returns the 4 values
	 * (subject, predicate, object, graph) according to the objectsFlag.<br/>
	 * <br/>
	 * F.e. objectFlag: (true, false, false, true) will return subject, graph
	 * 
	 * @param uris
	 *            the terms to search
	 * @param objectsFlag
	 * @param searchFields
	 *            the searchFields to search in
	 * @param stats
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public Collection<Node[]> searchRelation(String[] uris, boolean[] objectsFlag, String[] searchFields,
			SearchStats stats) throws CorruptIndexException, IOException {
		TopDocs docs;
		long start = Calendar.getInstance().getTimeInMillis();
		docs = searchTops(uris, searchFields, stats);
		long end = Calendar.getInstance().getTimeInMillis();
		System.out.println("search Tops took "+(end-start)+"ms");
		return searchTopDocs(docs, objectsFlag, stats);
	}

	/**
	 * Gets the 4 values (subject, predicate, object, graph) of all records <br/>
	 * <br/>
	 * F.e. objectFlag: (true, false, false, true) will return subject, graph
	 * 
	 * @param objectsFlag
	 * @param stats
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public Collection<Node[]> getAllRecords(boolean[] objectsFlag, SearchStats stats)
			throws CorruptIndexException, IOException {
		MatchAllDocsQuery query = new MatchAllDocsQuery();
		long start = Calendar.getInstance().getTimeInMillis();
		TopDocs docs = getTopDocsAfterSave(stats, query);
		long end = Calendar.getInstance().getTimeInMillis();
		System.out.println("Get All took "+(end-start)+"ms");
		return searchTopDocs(docs, objectsFlag, stats);
	}
	
	private TopDocs getTopDocsAfterSave(SearchStats stats, Query query) throws IOException {
		if (stats.getLastDoc() != null) {
			if(stats.getLastDoc().doc<stats.getTotalHits()) {
				return  indexSearcher.searchAfter(stats.getLastDoc(), query, maxSearch);
			}
			else {
				return null;
			}
		}
		else {
			return indexSearcher.search(query, maxSearch);
		}
	}

	private Collection<Node[]> searchTopDocs(TopDocs docs, boolean[] objectsFlag, SearchStats stats)
			throws CorruptIndexException, IOException {
		long start = Calendar.getInstance().getTimeInMillis();
		Collection<Node[]> triples = new HashSet<Node[]>();
		if(docs==null) {
			return triples;
		}
		for (ScoreDoc scoreDoc : docs.scoreDocs) {
			Document doc;
			doc = getDocument(scoreDoc);
			List<Node> triple = new LinkedList<Node>();
			if (objectsFlag[0])
				triple.add(NodeFactory.parseNode(doc.get(LuceneConstants.SUBJECT)));
			if (objectsFlag[1])
				triple.add(NodeFactory.parseNode(doc.get(LuceneConstants.PREDICATE)));
			if (objectsFlag[2])
				triple.add(NodeFactory.parseNode(doc.get(LuceneConstants.OBJECT)));
			if (objectsFlag[3]) {
				triple.add(NodeFactory.parseNode(doc.get(LuceneConstants.GRAPH)));
			}
			triples.add(triple.toArray(new Node[] {}));
		}
		if (docs.scoreDocs.length > 0) {
			stats.setLastDoc(docs.scoreDocs[docs.scoreDocs.length - 1]);
			stats.setLastHit(stats.getLastDoc().doc);
		}
		
		stats.setTotalHits(docs.totalHits);
		long end = Calendar.getInstance().getTimeInMillis();
		System.out.println("Search TopDocs took "+(end-start)+"ms");
		return triples;
	}

	public TopDocs searchORFields(String term, String[] searchFields) throws IOException {
		Builder finalQuery = new BooleanQuery.Builder();
		Builder wrapperQuery = new BooleanQuery.Builder();
		for (int i = 0; i < searchFields.length; i++) {
			TermQuery query = new TermQuery(new Term(searchFields[i], term));
			finalQuery.add(query, Occur.SHOULD);

		}
		wrapperQuery.add(finalQuery.build(), Occur.MUST);
		return indexSearcher.search(wrapperQuery.build(), maxSearch);
	}

	public String[][] explore(String term) throws IOException {
		String[] searchFields = new String[] { LuceneConstants.SUBJECT, LuceneConstants.PREDICATE,
				LuceneConstants.OBJECT };
		TopDocs docs = searchORFields(term, searchFields);
		int i = 0;
		String[][] exploreGraph = new String[docs.scoreDocs.length][];
		for (ScoreDoc scoreDoc : docs.scoreDocs) {
			String[] quad = new String[4];
			Document doc = getDocument(scoreDoc);
			quad[0] = doc.get(LuceneConstants.SUBJECT);
			quad[1] = doc.get(LuceneConstants.PREDICATE);
			quad[2] = doc.get(LuceneConstants.OBJECT);
			quad[3] = doc.get(LuceneConstants.GRAPH);
			exploreGraph[i++] = quad;
		}
		return exploreGraph;
	}

}