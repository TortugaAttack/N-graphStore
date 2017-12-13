package com.oppsci.ngraphstore.storage.cluster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.CorruptIndexException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.oppsci.ngraphstore.graph.Triple;
import com.oppsci.ngraphstore.graph.TripleFactory;
import com.oppsci.ngraphstore.graph.elements.Node;
import com.oppsci.ngraphstore.storage.lucene.LuceneConstants;
import com.oppsci.ngraphstore.storage.lucene.LuceneIndexer;
import com.oppsci.ngraphstore.storage.lucene.LuceneSearcher;
import com.oppsci.ngraphstore.storage.lucene.spec.SearchStats;
import com.oppsci.ngraphstore.storage.lucene.spec.impl.LuceneSearchSpec;
import com.oppsci.ngraphstore.storage.lucene.spec.impl.LuceneUpdateSpec;
import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

public class SingleClusterTest {

	private String folder;
	private LuceneSearcher searcher;
	private LuceneIndexer indexer;
	private String globalTriples = "<urn://a> <urn://b> <urn://c> . \n <urn://d> <urn://e> <urn://f> . \n <abc://abc> <abc://abc> <abc://abc> .";
	private int triplesBefore;

	@Before
	public void init() throws IOException {
		folder = UUID.randomUUID().toString();
		indexer = new LuceneIndexer(folder);
		// initial indexing
		indexer.index("<abc://abc>", "<abc://abc>", "<abc://abc>", "<urn://abc>");
		indexer.index("<abc://abc2>", "<abc://abc2>", "<abc://abc2>", "<abc://abc2>");
		triplesBefore = 2;
		indexer.close();
		searcher = new LuceneSearcher(folder);
		searcher.close();
	}

	@After
	public void close() throws IOException {
		indexer.close();
		searcher.close();
		FileUtils.deleteDirectory(new File(folder));

	}


	@Test
	public void addTest() throws CorruptIndexException, IOException {
		SearchStats stats = new SearchStats();
		LuceneUpdateSpec spec = new LuceneUpdateSpec();

		Triple<String>[] triples = TripleFactory.parseTriples(globalTriples, "<urn://abc>");
		spec.setTriples(triples);

		Cluster cluster = new Cluster(spec, searcher, indexer, Cluster.INSERT_METHOD, stats, false);
		cluster.add();
		assertTrue(updateCheck(spec.getTriples(), triplesBefore - 1));
	}

	private boolean updateCheck(List<Triple<String>> triples, int offset) throws CorruptIndexException, IOException {
		boolean[] objectsFlag = new boolean[] { true, true, true, true };
		String[] searchFields = new String[] { LuceneConstants.SUBJECT, LuceneConstants.PREDICATE,
				LuceneConstants.OBJECT, LuceneConstants.GRAPH };
		boolean check = true;
		for (Triple<String> triple : triples) {
			String[] uris = new String[] { triple.getSubject(), triple.getPredicate(), triple.getObject(),
					triple.getGraph() };
			searcher.reopen();
			Collection<Node[]> actual = searcher.searchRelation(uris, objectsFlag, searchFields, new SearchStats());

			// it is sufficient to check if actual has a non null object
			check &= actual.size() == 1;
		}
		check &= searcher.getAllRecords(objectsFlag, new SearchStats()).size() == offset + triples.size();
		searcher.close();
		// only true if for each triple the triple could be found.
		// and non prev. added were overwritten.
		return check;
	}

	@Test
	public void select() throws CorruptIndexException, IOException {
		boolean[] objectsFlag = new boolean[] { true, true, true, true };
		String[] searchFields = new String[] { LuceneConstants.SUBJECT, LuceneConstants.GRAPH };
		String[] uris = new String[] { "<abc://abc>", "<urn://abc>" };
		LuceneSearchSpec spec = new LuceneSearchSpec(uris, objectsFlag, searchFields);
		Cluster cluster = new Cluster(spec, searcher, indexer, Cluster.INSERT_METHOD, new SearchStats(), false);
		searcher.reopen();
		SimpleResultSet res = cluster.select();
		searcher.close();
		assertTrue(res.getVars().size() == 4);
		assertTrue(res.getRows().size() == 1);
		Node[] node = res.getRows().iterator().next();
		assertEquals("<abc://abc>", node[0].getNode());
		assertEquals("<abc://abc>", node[1].getNode());
		assertEquals("<abc://abc>", node[2].getNode());
		assertEquals("<urn://abc>", node[3].getNode());

	}

	@Test
	public void selectAll() throws IOException {
		boolean[] objectsFlag = new boolean[] { true, true, true, true };
		LuceneSearchSpec spec = new LuceneSearchSpec(null, objectsFlag, null);
		Cluster cluster = new Cluster(spec, searcher, indexer, Cluster.INSERT_METHOD, new SearchStats(), false);
		searcher.reopen();
		SimpleResultSet res = cluster.selectAll();
		searcher.close();
		assertTrue(res.getRows().size() == 2);
	}


}
