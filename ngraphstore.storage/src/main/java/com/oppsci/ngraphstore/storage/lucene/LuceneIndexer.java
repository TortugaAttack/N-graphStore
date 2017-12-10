package com.oppsci.ngraphstore.storage.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RegexpQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.store.AlreadyClosedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Lucene Indexer. Will update and load data into the provided path
 * 
 * TODO add semaphore and mutex
 * 
 * @author f.conrads
 *
 */
public class LuceneIndexer {
	private static final Logger LOGGER = LoggerFactory.getLogger(LuceneIndexer.class);

	private IndexWriter writer;
	private Directory dir;
	private String path;

	/**
	 * Creates and opens a LuceneIndexer according to the given path
	 * @param path the path in which the indexing files should be.
	 * @throws IOException
	 */
	public LuceneIndexer(String path) throws IOException {
		open(path);
	}
	
	/**
	 * Will open the Lucene Indexer at the given path
	 * @param path the path in which the indexing files should be.
	 * @throws IOException
	 */
	public void open(String path) throws IOException {
		this.path = path;
		dir = FSDirectory.open(new File(path));
		Analyzer analyzer = new KeywordAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		
		writer = new IndexWriter(dir, config);
	}

	/**
	 * Will reopen the previously opened indexer
	 * @throws IOException
	 */
	public void reopen() throws IOException {
//		open(path);
		dir = FSDirectory.open(new File(path));
		Analyzer analyzer = new KeywordAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		
		writer = new IndexWriter(dir, config);
	}
	
	/**
	 * Will commit and close the Indexer
	 */
	public void close() {
		try {
			writer.commit();
			writer.close();
			dir.close();
		} catch (AlreadyClosedException | IOException e) {
			LOGGER.error("Error occured during closing Index Writer", e);
		}
	}

	/**
	 * Will only commit the current changes.
	 * @throws IOException
	 */
	public void commit() throws IOException {
			writer.commit();
	}

	/**
	 * Will add the triple (quad) into the indexing segments. 
	 * 
	 * @param subject 
	 * @param predicate
	 * @param object
	 * @param graph
	 * @throws IOException
	 */
	public void index(String subject, String predicate, String object, String graph) throws IOException {
		indexTriple(subject, predicate, object, graph);
	}

	/**
	 * Will add the triple (quad) into the indexing segments. 
	 * 
	 * @param subject 
	 * @param predicate
	 * @param object
	 * @param graph
	 * @throws IOException
	 */
	public void indexTriple(String subject, String predicate, String object, String graph) throws IOException {
		Document doc = convertTerm(subject, predicate, object, graph);
		//check before adding if already existent
		writer.addDocument(doc);
	}

	
	/**
	 * Shortcut for {@link #delete(String[], String[])}. 
	 * Will delete only the occurrences which fits all terms 
	 * <br/>
	 * WILL NOT WORK WITH BLANK NODES!
	 * 
	 * @param subject The subject term 
	 * @param predicate The predicate term
	 * @param object the object term
	 * @param graph the graph term
	 * @throws IOException
	 */
	public void delete(String subject, String predicate, String object, String graph) throws IOException {
		BooleanQuery finalQuery = new BooleanQuery();
		TermQuery query = new TermQuery(new Term(LuceneConstants.SUBJECT, subject));
		finalQuery.add(query, Occur.MUST);
		query = new TermQuery(new Term(LuceneConstants.PREDICATE, predicate));
		finalQuery.add(query, Occur.MUST);
		query = new TermQuery(new Term(LuceneConstants.OBJECT, object));
		finalQuery.add(query, Occur.MUST);
		query = new TermQuery(new Term(LuceneConstants.GRAPH, graph));
		finalQuery.add(query, Occur.MUST);
		writer.deleteDocuments(finalQuery);
	}

	
	/**
	 * Will delete each record with the occurrence of the specific nodes at the search fields.
	 * <br/>
	 * F.e.: DROP graphURI:
	 * <br/>
	 * luceneIndexer.delete(new String[]{graphURI}, new String[]{LuceneConstants.GRAPH});
	 * 
	 * @param nodes
	 * @param searchFields
	 * @throws IOException
	 */
	public void delete(String[] nodes, String[] searchFields) throws IOException {
		BooleanQuery finalQuery = new BooleanQuery();
		for(int i=0;i<nodes.length;i++) { 
			Query query;
			if(nodes[i].startsWith("_:")) {
				// bnode
				query = new RegexpQuery(new Term(searchFields[i], "_:[^ ]+"));
			} else {
				query = new TermQuery(new Term(searchFields[i], nodes[i]));
			}
			finalQuery.add(query, Occur.MUST);
		}
		writer.deleteDocuments(finalQuery);
	}
	
	/**
	 * Will delete every record (same as DROP)
	 * @throws IOException
	 */
	public void deleteAll() throws IOException {
		writer.deleteAll();
	}

	private Document convertTerm(String subject, String predicate, String object, String graph) {
		Document document = new Document();
		Field subjectField = new StringField(LuceneConstants.SUBJECT, subject, Field.Store.YES);
		Field predicateField = new StringField(LuceneConstants.PREDICATE, predicate, Field.Store.YES);
		Field objectField = new StringField(LuceneConstants.OBJECT, object, Field.Store.YES);
		Field graphField = new StringField(LuceneConstants.GRAPH, graph, Field.Store.YES);
		
		document.add(subjectField);
		document.add(predicateField);
		document.add(objectField);
		document.add(graphField);
		return document;
	}

	
	public void update(String[] oldTerms, String[] nodes) throws IOException {
		//delete document according to old Terms 
		BooleanQuery finalQuery = new BooleanQuery();
		TermQuery querySubject = new TermQuery(new Term(LuceneConstants.SUBJECT, oldTerms[0]));
		TermQuery queryPredicate = new TermQuery(new Term(LuceneConstants.PREDICATE, oldTerms[1]));
		TermQuery queryObject = new TermQuery(new Term(LuceneConstants.OBJECT, oldTerms[2]));
		TermQuery queryGraph = new TermQuery(new Term(LuceneConstants.GRAPH, oldTerms[3]));
		finalQuery.add(querySubject, Occur.MUST);
		finalQuery.add(queryPredicate, Occur.MUST);
		finalQuery.add(queryObject, Occur.MUST);
		finalQuery.add(queryGraph, Occur.MUST);
		writer.deleteDocuments(finalQuery);
		//adding new Document. 
		index(nodes[0], nodes[1], nodes[2], nodes[3]);
	}
	
	/**
	 * Will roll back every change since the last commit
	 * @throws IOException
	 */
	public void rollback() throws IOException {
		writer.rollback();
	}

}
