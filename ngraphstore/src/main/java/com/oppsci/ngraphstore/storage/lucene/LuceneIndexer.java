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
import org.apache.lucene.index.MergePolicy.OneMerge;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneIndexer {
	private static final Logger LOGGER = LoggerFactory.getLogger(LuceneIndexer.class);

	private IndexWriter writer;
	private Directory dir;
	private String path;

	public LuceneIndexer(String path) throws IOException {
		open(path);
	}
	
	public void open(String path) throws IOException {
		this.path = path;
		dir = FSDirectory.open(new File(path));
		Analyzer analyzer = new KeywordAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		
		writer = new IndexWriter(dir, config);
	}

	//TODO update existin segments instead of creating new ones
	public void reopen() throws IOException {
		open(path);
	}
	
	public void close() {
		try {
			writer.commit();
			writer.close();
			dir.close();
		} catch (IOException e) {
			LOGGER.error("Error occured during closing Index Writer", e);
		}
	}

	public void commit() throws IOException {
		try {
			writer.commit();
			writer.close(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (IndexWriter.isLocked(dir)) {
				IndexWriter.unlock(dir);
			}

		}

	}

	public void index(String subject, String predicate, String object, String graph) throws IOException {
		indexTriple(subject, predicate, object, graph);
	}

	public void indexTriple(String subject, String predicate, String object, String graph) throws IOException {
		Document doc = convertTerm(subject, predicate, object, graph);
		//check before adding if already existent
		writer.addDocument(doc);
	}

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

}
