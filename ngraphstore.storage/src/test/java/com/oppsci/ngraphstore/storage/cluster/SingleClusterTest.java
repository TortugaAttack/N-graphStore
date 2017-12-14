package com.oppsci.ngraphstore.storage.cluster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.AlreadyClosedException;
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
import com.oppsci.ngraphstore.storage.lucene.spec.impl.LuceneQuadUpdateSpec;
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
	public void close() {
		try {
			indexer.close();
			searcher.close();
			FileUtils.deleteDirectory(new File(folder));
		} catch (IOException e) {
			e.printStackTrace();
		} catch(AlreadyClosedException e) {
			//nothing to do here
		}
		

	}

	@Test
	public void addTest() throws Exception {
		indexer.reopen();
		SearchStats stats = new SearchStats();
		LuceneUpdateSpec spec = new LuceneUpdateSpec();

		Triple<String>[] triples = TripleFactory.parseTriples(globalTriples, "<urn://abc>");
		spec.setTriples(triples);

		Cluster cluster = new Cluster(spec, searcher, indexer, Cluster.INSERT_METHOD, stats, false);
		cluster.call();
		assertTrue(updateCheck(spec.getTriples(), triplesBefore - 1));
	}

	@Test
	public void dropAllTest() throws Exception {
		indexer.reopen();
		Cluster cluster = new Cluster(null, searcher, indexer, Cluster.DROP_ALL_METHOD, new SearchStats(), false);
		cluster.call();
		indexer.close();
		searcher.reopen();
		assertTrue(searcher.getAllRecords(new boolean[] { true, true, true, true }, new SearchStats()).isEmpty());
	}

	@Test
	public void dropTest() throws Exception {
		LuceneUpdateSpec spec = new LuceneUpdateSpec("<abc://abc2>");
		indexer.reopen();
		Cluster cluster = new Cluster(spec, searcher, indexer, Cluster.DROP_METHOD, new SearchStats(), false);
		cluster.call();
		indexer.close();
		searcher.reopen();
		Collection<Node[]> results = searcher.getAllRecords(new boolean[] { true, true, true, true },
				new SearchStats());
		assertTrue(results.size() == 1);
		Node[] result = results.iterator().next();
		assertTrue(checkNode(new String[] { "<abc://abc>", "<abc://abc>", "<abc://abc>", "<urn://abc>" }, result));
	}

	@Test
	public void deleteTest() throws Exception {
		LuceneUpdateSpec spec = new LuceneUpdateSpec();
		Triple<String>[] triples = TripleFactory.parseTriples("<abc://abc2> <abc://abc2> <abc://abc2>.",
				"<abc://abc2>");
		spec.setTriples(triples);
		indexer.reopen();
		Cluster cluster = new Cluster(spec, searcher, indexer, Cluster.DELETE_METHOD, new SearchStats(), false);
		cluster.call();
		indexer.close();
		searcher.reopen();
		Collection<Node[]> results = searcher.getAllRecords(new boolean[] { true, true, true, true },
				new SearchStats());
		assertTrue(results.size() == 1);
		Node[] result = results.iterator().next();
		assertTrue(checkNode(new String[] { "<abc://abc>", "<abc://abc>", "<abc://abc>", "<urn://abc>" }, result));
	}

	@Test
	public void loadTest() throws Exception {
		Triple<String>[] triples = TripleFactory.parseTriples(globalTriples, "<urn://abc>");
		LuceneUpdateSpec spec = new LuceneUpdateSpec(triples);
		indexer.reopen();
		Cluster cluster = new Cluster(spec, searcher, indexer, Cluster.LOAD_METHOD, new SearchStats(), false);
		cluster.call();
		indexer.close();
		assertTrue(updateCheck(spec.getTriples(), triplesBefore - 2));
	}

	@Test
	public void quadUpdateTest() throws Exception {
		String[] oldTerms = new String[] { "<abc://abc>", "<abc://abc>", "<abc://abc>", "<urn://abc>" };
		String[] newTerms = new String[] { "<abc://abc3>", "<abc://abc3>", "<abc://abc3>", "<urn://abc3>" };
		String[] notUpdated = new String[] { "<abc://abc2>", "<abc://abc2>", "<abc://abc2>", "<abc://abc2>" };
		LuceneQuadUpdateSpec spec = new LuceneQuadUpdateSpec(oldTerms, newTerms);
		
		Cluster cluster = new Cluster(spec, searcher, indexer, Cluster.QUAD_UPDATE, new SearchStats(), false);
		cluster.call();
		indexer.close();
		searcher.reopen();
		Collection<Node[]> results = searcher.getAllRecords(new boolean[] { true, true, true, true },
				new SearchStats());
		assertTrue(results.size() == 2);
		Iterator<Node[]> it = results.iterator();
		Node[] node1 = it.next();
		Node[] node2 = it.next();
		boolean newChecked = false;
		newChecked = checkNode(newTerms, node1);
		if (!newChecked) {
			assertTrue(checkNode(newTerms, node2));
			assertTrue(checkNode(notUpdated, node1));
		}
		else {
			assertTrue(checkNode(notUpdated, node2));
		}
	}

	private boolean checkNode(String[] expected, Node[] actual) {
		boolean check = expected[0].equals(actual[0].getNode());
		check &= expected[1].equals(actual[1].getNode());
		check &= expected[2].equals(actual[2].getNode());
		check &= expected[3].equals(actual[3].getNode());
		return check;
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
	public void select() throws Exception {
		boolean[] objectsFlag = new boolean[] { true, true, true, true };
		String[] searchFields = new String[] { LuceneConstants.SUBJECT, LuceneConstants.GRAPH };
		String[] uris = new String[] { "<abc://abc>", "<urn://abc>" };
		LuceneSearchSpec spec = new LuceneSearchSpec(uris, objectsFlag, searchFields);
		Cluster cluster = new Cluster(spec, searcher, indexer, Cluster.SEARCH_METHOD, new SearchStats(), false);
		searcher.reopen();
		SimpleResultSet res = (SimpleResultSet) cluster.call();
		assertTrue(res.getVars().size() == 4);
		assertTrue(res.getRows().size() == 1);
		Node[] node = res.getRows().iterator().next();
		assertEquals("<abc://abc>", node[0].getNode());
		assertEquals("<abc://abc>", node[1].getNode());
		assertEquals("<abc://abc>", node[2].getNode());
		assertEquals("<urn://abc>", node[3].getNode());

	}

	@Test
	public void selectAll() throws Exception {
		boolean[] objectsFlag = new boolean[] { true, true, true, true };
		LuceneSearchSpec spec = new LuceneSearchSpec(null, objectsFlag, null);
		Cluster cluster = new Cluster(spec, searcher, indexer, Cluster.SEARCH_ALL_METHOD, new SearchStats(), false);
		searcher.reopen();
		SimpleResultSet res = (SimpleResultSet) cluster.call();
		searcher.close();
		assertTrue(res.getRows().size() == 2);
	}

	@Test
	public void explore() throws Exception {
		addTest();
		LuceneSearchSpec spec = new LuceneSearchSpec(new String[] { "<urn://a>" }, null, null);
		Cluster cluster = new Cluster(spec, searcher, indexer, Cluster.EXPLORE_METHOD, new SearchStats(), false);
		searcher.reopen();
		String[][] explore = (String[][]) cluster.call();
		assertTrue(explore.length == 1);
		assertEquals("<urn://a>", explore[0][0]);
		assertEquals("<urn://b>", explore[0][1]);
		assertEquals("<urn://c>", explore[0][2]);
		assertEquals("<urn://abc>", explore[0][3]);

		spec = new LuceneSearchSpec(new String[] { "<urn://b>" }, null, null);
		cluster = new Cluster(spec, searcher, indexer, Cluster.INSERT_METHOD, new SearchStats(), false);
		explore = cluster.explore();
		assertTrue(explore.length == 1);
		assertEquals("<urn://a>", explore[0][0]);
		assertEquals("<urn://b>", explore[0][1]);
		assertEquals("<urn://c>", explore[0][2]);
		assertEquals("<urn://abc>", explore[0][3]);

		spec = new LuceneSearchSpec(new String[] { "<urn://c>" }, null, null);
		cluster = new Cluster(spec, searcher, indexer, Cluster.INSERT_METHOD, new SearchStats(), false);
		explore = cluster.explore();
		assertTrue(explore.length == 1);
		assertEquals("<urn://a>", explore[0][0]);
		assertEquals("<urn://b>", explore[0][1]);
		assertEquals("<urn://c>", explore[0][2]);
		assertEquals("<urn://abc>", explore[0][3]);

	}

}
