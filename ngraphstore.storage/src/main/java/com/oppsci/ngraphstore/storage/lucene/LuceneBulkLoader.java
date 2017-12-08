package com.oppsci.ngraphstore.storage.lucene;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bulk Loading Files into lucene folder
 * (files itself should fit in memory) <br/>
 * 
 * @author f.conrads
 *
 */
public class LuceneBulkLoader {
	
	private static final  Logger LOGGER = LoggerFactory.getLogger(LuceneBulkLoader.class);

	
	private List<LuceneIndexer> indexer = new LinkedList<LuceneIndexer>();


	private boolean ignoreErrors=false;


	private Integer clusterSize;
	
	

	/**
	 * Will bulk load the given files into N Cluster
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if(args.length<5) {
			System.out.println("Usage: bulkload.sh N ignoreErrors folder graphURI file1 file2...");
			return;
		}
		Integer clusterSize = Integer.parseInt(args[0]);
		Boolean ignoreErrors = Boolean.parseBoolean(args[1]);
		String folder = args[2];

		LuceneBulkLoader loader = new LuceneBulkLoader(clusterSize, ignoreErrors);
		for(int i=0;i<clusterSize;i++) {
			loader.addIndexer(new LuceneIndexer(folder+File.separator+i));
		}
		String graph=args[3];
		loader.load(graph, getFiles(args));
	}
	
	/**
	 * Will create a LuceneBulkLoader
	 * 
	 * @param clusterSize the amount of clusters to use
	 * @param ignoreErrors if true will ignore every error, if false will rollback the changes from one file
	 */
	public LuceneBulkLoader(Integer clusterSize, boolean ignoreErrors) {
		this.clusterSize=clusterSize;
	}

	private void addIndexer(LuceneIndexer luceneIndexer) {
		this.indexer.add(luceneIndexer);
	}

	/**
	 * @return the indexer
	 */
	public List<LuceneIndexer> getIndexer() {
		return indexer;
	}

	/**
	 * @param indexer the indexer to set
	 */
	public void setIndexer(List<LuceneIndexer> indexer) {
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
		for(int i=4; i<args.length;i++) {
			files[i-1] = new File(args[i]);
		}
		return files;
	}
	
	/**
	 * Loads all files using the graphURI into the lucene cluster
	 * <br/>
	 * It will put the Kth statement into the K mod N Cluster (whereas N is the amount of cluster choosen)
	 * <br/>
	 * F.e.: With N=2 Cluster and 10 Statements to load<br/>
	 * Every odd Statement will be in the first Cluster, where as every even Statement will be in the second Cluster.
	 * 
	 * @param graph the graphURI to load into
 	 * @param files The RDF Files to load
	 * @throws IOException
	 */
	public void load(String graph, File... files ) {
		for(File file : files) {
			
			try {
				loadSingle(file, graph);
				for(LuceneIndexer luceneIndexer : indexer) {
					luceneIndexer.commit();
				}
				LOGGER.info(file.getName()+" successfully loaded.");
			} catch (IOException e) {
				// will only be thrown if ignoreErrors is false
				for(LuceneIndexer luceneIndexer : indexer) {
					LOGGER.error("Could not load "+file.getName()+" due to following exception, ", e);
					try {
						LOGGER.info("Will rollback changes from file.");
						luceneIndexer.rollback();
					} catch (IOException e1) {
						LOGGER.error("Could not rollback file.", e1);
					}
				}
			}	
		}
		for(LuceneIndexer luceneIndexer : indexer) {
			luceneIndexer.close();
		}
	}
	
	/**
	 * Loads a single file using the graphURI into the lucene cluster
	 * <br/>
	 * It will put the Kth statement into the K mod N Cluster (whereas N is the amount of cluster choosen)
	 * <br/>
	 * F.e.: With N=2 Cluster and 10 Statements to load<br/>
	 * Every odd Statement will be in the first Cluster, where as every even Statement will be in the second Cluster.
	 * 
	 * @param file The RDF File to load
	 * @param graph the graphURI to load into
	 * @throws IOException if errors will not be ignored and one happend 
	 */
	private void loadSingle(File file, String graph) throws IOException  {
		Model m = ModelFactory.createDefaultModel();
		m.read(file.toURI().toURL().toString());
		StmtIterator statements = m.listStatements();
		int i=0;
		while(statements.hasNext()) {
			Statement stmt = statements.next();
			String subject="";
			String predicate="";
			if(stmt.getSubject().isURIResource()) {
				subject = "<" + stmt.getSubject().getURI() + ">";
			}
			else if(stmt.getSubject().isAnon()) {
				subject = "_:"+stmt.getSubject().toString();
			}
			if(stmt.getPredicate().isURIResource()) {
				predicate = "<" + stmt.getPredicate().getURI() + ">";
			}
			else if(stmt.getPredicate().isAnon()) {
				predicate = "_:"+stmt.getPredicate().toString();
			}
			String object;
			if (stmt.getObject().isLiteral()) {
				Literal literal = stmt.getObject().asLiteral();
				object = stmt.getObject().asNode().toString(true);
				if (literal.getLanguage().isEmpty()
						&& !literal.getDatatypeURI().equals("http://www.w3.org/2001/XMLSchema#string")) {
					object = object.substring(0, object.lastIndexOf("^^") + 2) + "<" + literal.getDatatypeURI() + ">";
				}
			} else if (stmt.getObject().isAnon()) {
				object = "_:"+stmt.getObject().asNode().toString();
			} else {
				object = "<" + stmt.getObject().asNode().getURI() + ">";
			}
			
			try {
				indexer.get(i++).index(subject, predicate, object, graph);
			} catch (IOException e) {
				if(!ignoreErrors) {
					throw e;
				}
				LOGGER.info("[ERROR found] will gracefully ignore it. {{}} {{}} {{}} {{}}", subject, predicate, object, graph);
			}
			if(i>=clusterSize) {
				i=0;
			}
		}
	}
}
