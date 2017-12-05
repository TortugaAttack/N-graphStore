package com.oppsci.ngraphstore.storage.lucene;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * Bulk Loading Files into lucene folder
 * (files itself should fit in memory) <br/>
 * TODO: NTriple load method for files who will not fit in mem.
 * 
 * @author f.conrads
 *
 */
public class LuceneBulkLoader {
	
	private LuceneIndexer indexer;
	
	/**
	 * Will bulk load the given files into 
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if(args.length<2) {
			System.out.println("Usage: bulkload.sh folder file1 file2...");
			return;
		}
		String folder = args[0];
		//TODO use folder as prefix and make N folders. which can be used for clusters
		LuceneBulkLoader loader = new LuceneBulkLoader();
		loader.setIndexer(new LuceneIndexer(folder));
		loader.load(getFiles(args));
	}

	/**
	 * @return the indexer
	 */
	public LuceneIndexer getIndexer() {
		return indexer;
	}

	/**
	 * @param indexer the indexer to set
	 */
	public void setIndexer(LuceneIndexer indexer) {
		this.indexer = indexer;
	}
	
	/**
	 * Will convert arguments to files
	 * 
	 * @param args
	 * @return
	 */
	private static File[] getFiles(String[] args) {
		File[] files = new File[args.length-1];
		for(int i=1; i<args.length;i++) {
			files[i-1] = new File(args[i]);
		}
		return files;
	}
	
	/**
	 * @param files
	 */
	public void load(File... files ) {
		for(File file : files) {
			
			try {
				loadSingle(file);
				System.out.println(file.getName()+" successfully loaded.");
			} catch (IOException e) {
				System.err.println("Could not load "+file.getName()+" due to following exception, ");
				e.printStackTrace();
			}	
		}
		indexer.close();
	}
	
	private void loadSingle(File file) throws IOException {
		Model m = ModelFactory.createDefaultModel();
		m.read(file.toURI().toURL().toString());
		StmtIterator statements = m.listStatements();
		while(statements.hasNext()) {
			Statement stmt = statements.next();
			String subject = stmt.getSubject().getURI();
			String predicate = stmt.getPredicate().getURI();
			String object = stmt.getObject().asNode().toString(true);
			indexer.index(subject, predicate, object);
		}
	}
}
