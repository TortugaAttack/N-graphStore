package com.oppsci.ngraphstore.storage.lucene;

import java.io.File;
import java.io.IOException;
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

	/**
	 * Creates and opens a Lucene Searcher according to the given path
	 * @param indexDirectoryPath the path in which the indexing files should be.
	 * @throws IOException
	 */
	public LuceneSearcher(String indexDirectoryPath) throws IOException {
		open(indexDirectoryPath);
	}
	
	/**
	 * Will open the Lucene Searcher at the given path
	 * @param indexDirectoryPath the path in which the indexing files should be.
	 * @throws IOException
	 */
	public void open(String indexDirectoryPath) throws IOException {
		this.path  = indexDirectoryPath;
		indexDirectory = FSDirectory.open(new File(indexDirectoryPath));
		indexReader = DirectoryReader.open(indexDirectory);
		indexSearcher = new IndexSearcher(indexReader);
	}
	
	/**
	 * Will reopen the previously opened searcher
	 * @throws IOException
	 */
	public void reopen() throws IOException {
		open(path);
	}
	
	/**
	 * Will search the docs in which the searchQuery term will occur at the searchField
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
	 * Will search the docs in which the searchQueries term will occur at the according searchFields
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
		BooleanQuery finalQuery = new BooleanQuery();
		
		for(int i=0;i<searchQueries.length;i++) {
			if(searchQueries[i].equals("_:")) {
				//bnode
				RegexpQuery query = new RegexpQuery(new Term(searchFields[i], "^_:.+$"));
				finalQuery.add(query, Occur.MUST);
			}
			else {
				TermQuery query = new TermQuery(new Term(searchFields[i], searchQueries[i]));
				finalQuery.add(query, Occur.MUST); 
			}
		}
		if(stats.getLastDoc()!=null)
			return indexSearcher.searchAfter(stats.getLastDoc(), finalQuery, LuceneConstants.MAX_SEARCH);
		return indexSearcher.search(finalQuery, LuceneConstants.MAX_SEARCH);
		
	}
	
	private TopDocs searchTerm(String searchQuery, String searchField, SearchStats stats) throws IOException {
		Query query;
		if(searchQuery.equals("_:")) {
			//bnode
			query = new RegexpQuery(new Term(searchField, "^_:.+$"));
		}
		else {
			query = new TermQuery(new Term(searchField, searchQuery));
		}
		if(stats.getLastDoc()!=null)
			return indexSearcher.searchAfter(stats.getLastDoc(), query, LuceneConstants.MAX_SEARCH);
		return indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
	}

	private Document getDocument(ScoreDoc scoreDoc) throws CorruptIndexException, IOException {
		return indexSearcher.doc(scoreDoc.doc);
	}

	/**
	 * Will close the searcher quietly
	 * @throws IOException
	 */
	public void close() throws IOException {
		IOUtils.closeQuietly(indexReader);
		IOUtils.closeQuietly(indexDirectory);
	}

	/**
	 * Will search the term (uri) in the searchField and returns the 4 values (subject, predicate, object, graph)
	 * according to the objectsFlag.<br/><br/>
	 * F.e. objectFlag: (true, false, false, true)
	 * will return subject, graph
	 * 
	 * @param uri the term to search 
	 * @param objectsFlag
	 * @param searchField the searchField to search in
	 * @param stats 
	 * @return
	 * @throws IOException
	 */
	public Collection<Node[]> search(String uri, boolean[] objectsFlag, String searchField, SearchStats stats) throws IOException {
		return searchRelation(uri, objectsFlag, searchField, stats);
	}

	/**
	 * Will search the term (uri) in the searchField and returns the 4 values (subject, predicate, object, graph)
	 * according to the objectsFlag.<br/><br/>
	 * F.e. objectFlag: (true, false, false, true)
	 * will return subject, graph
	 * 
	 * @param uri the term to search 
	 * @param objectsFlag
	 * @param searchField the searchField to search in
	 * @param stats 
	 * @return
	 * @throws IOException
	 */
	public Collection<Node[]> searchRelation(String uri, boolean[] objectsFlag, String searchField, SearchStats stats) throws IOException {
		TopDocs docs;
		docs = searchTops(uri, searchField, stats);
		return searchTopDocs(docs, objectsFlag, stats);
	}
	
	/**
	 * Will search the terms (uris) in the searchFields and returns the 4 values (subject, predicate, object, graph)
	 * according to the objectsFlag.<br/><br/>
	 * F.e. objectFlag: (true, false, false, true)
	 * will return subject, graph
	 * 
	 * @param uris the terms to search 
	 * @param objectsFlag
	 * @param searchFields the searchFields to search in
	 * @param stats 
	 * @return
	 * @throws CorruptIndexException 
	 * @throws IOException
	 */
	public Collection<Node[]> searchRelation(String[] uris, boolean[] objectsFlag, String[] searchFields, SearchStats stats) throws CorruptIndexException, IOException {
			TopDocs docs; 
			docs = searchTops(uris, searchFields, stats);
			return searchTopDocs(docs, objectsFlag, stats);
	}
	
	/**
	 * Gets the 4 values (subject, predicate, object, graph) of all records 
	 * <br/><br/>
	 * F.e. objectFlag: (true, false, false, true)
	 * will return subject, graph
	 * @param objectsFlag
	 * @param stats 
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public Collection<Node[]> getAllRecords(boolean[] objectsFlag, SearchStats stats) throws CorruptIndexException, IOException{
		MatchAllDocsQuery query = new MatchAllDocsQuery();
		return searchTopDocs(indexSearcher.search(query, LuceneConstants.MAX_SEARCH), objectsFlag, stats);
	}
	
	
	private Collection<Node[]> searchTopDocs(TopDocs docs, boolean[] objectsFlag, SearchStats stats) throws CorruptIndexException, IOException{
		Collection<Node[]> triples = new HashSet<Node[]>();
		
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
		stats.setLastDoc(docs.scoreDocs[docs.scoreDocs.length]);
		stats.setLastHit(docs.scoreDocs.length);
		stats.setTotalHits(docs.totalHits);
		return triples;
	}

}