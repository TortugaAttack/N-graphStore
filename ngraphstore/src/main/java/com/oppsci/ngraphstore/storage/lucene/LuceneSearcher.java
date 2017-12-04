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
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LuceneSearcher {

	private IndexSearcher indexSearcher;
	private Directory indexDirectory;
	private IndexReader indexReader;

	/**
	 * 
	 * @param indexDirectoryPath
	 * @param searchField
	 *            SUBJECT, PROPERTY, OBJECT
	 * @throws IOException
	 */
	public LuceneSearcher(String indexDirectoryPath) throws IOException {
		indexDirectory = FSDirectory.open(new File(indexDirectoryPath));
		indexReader = DirectoryReader.open(indexDirectory);
		indexSearcher = new IndexSearcher(indexReader);
	}

	public TopDocs searchTops(String searchQuery, String searchField) throws IOException {
		return searchTerm(searchQuery, searchField);
	}
	
	public TopDocs searchTops(String[] searchQueries, String[] searchFields) throws IOException {
		return searchTerms(searchQueries, searchFields);
	}

	private TopDocs searchTerms(String[] searchQueries, String[] searchFields) throws IOException {
		BooleanQuery finalQuery = new BooleanQuery();
		
		for(int i=0;i<searchQueries.length;i++) {
			TermQuery query = new TermQuery(new Term(searchFields[i], searchQueries[i]));
			finalQuery.add(query, Occur.MUST); 
		}
		return indexSearcher.search(finalQuery, LuceneConstants.MAX_SEARCH);
		
	}
	
	private TopDocs searchTerm(String searchQuery, String searchField) throws IOException {
		TermQuery query = new TermQuery(new Term(searchField, searchQuery));
		return indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
	}

	public Document getDocument(ScoreDoc scoreDoc) throws CorruptIndexException, IOException {
		return indexSearcher.doc(scoreDoc.doc);
	}

	public void close() throws IOException {
		IOUtils.closeQuietly(indexReader);
		IOUtils.closeQuietly(indexDirectory);
	}

	public Collection<String[]> search(String uri, boolean[] objectsFlag, String searchField) throws IOException {
		return searchRelation(uri, objectsFlag, searchField);
	}

	/**
	 * 
	 * @param uri
	 * @param getObjectsFlag
	 * @return
	 * @throws IOException
	 */
	public Collection<String[]> searchRelation(String uri, boolean[] objectsFlag, String searchField) throws IOException {
		TopDocs docs;
		docs = searchTops(uri, searchField);
		return searchTopDocs(docs, objectsFlag);
	}
	
	public Collection<String[]> searchRelation(String[] uris, boolean[] objectsFlag, String[] searchFields) throws CorruptIndexException, IOException {
			TopDocs docs; 
			docs = searchTops(uris, searchFields);
			return searchTopDocs(docs, objectsFlag);
	}
	
	private Collection<String[]> searchTopDocs(TopDocs docs, boolean[] objectsFlag) throws CorruptIndexException, IOException{
		Collection<String[]> triples = new HashSet<String[]>();
		for (ScoreDoc scoreDoc : docs.scoreDocs) {
			Document doc;
			doc = getDocument(scoreDoc);
			List<String> triple = new LinkedList<String>();
			if (objectsFlag[0])
				triple.add(doc.get(LuceneConstants.SUBJECT));
			if (objectsFlag[1])
				triple.add(doc.get(LuceneConstants.PREDICATE));
			if (objectsFlag[2])
				triple.add(doc.get(LuceneConstants.OBJECT));

			triples.add(triple.toArray(new String[] {}));
		}
		return triples;
	}

}