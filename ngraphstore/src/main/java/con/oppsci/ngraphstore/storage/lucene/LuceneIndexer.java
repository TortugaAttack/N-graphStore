package con.oppsci.ngraphstore.storage.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneIndexer {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(LuceneIndexer.class);

	private IndexWriter writer;
	private Directory dir;
	

	public LuceneIndexer(String path) throws IOException{
			dir = FSDirectory.open(new File(path));
			Analyzer analyzer = new KeywordAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
			config.setOpenMode(OpenMode.CREATE);
			writer = new IndexWriter(dir, config);
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

	public void index(String subject, String predicate, String object) {
			indexTriple(subject, predicate, object);
	}


	public void indexTriple(String subject, String predicate, String object) {
		Document doc = convertTerm(subject, predicate, object);
			try {
				writer.addDocument(doc);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

	private Document convertTerm(String subject, String predicate, String object) {
		Document document = new Document();
		Field subjectField = new StringField(LuceneConstants.SUBJECT, subject, Field.Store.YES);
		Field predicateField = new StringField(LuceneConstants.PREDICATE, predicate, Field.Store.YES);
		Field objectField = new StringField(LuceneConstants.OBJECT, object, Field.Store.YES);
		
		document.add(subjectField);
		document.add(predicateField);
		document.add(objectField);
		return document;
	}

}
