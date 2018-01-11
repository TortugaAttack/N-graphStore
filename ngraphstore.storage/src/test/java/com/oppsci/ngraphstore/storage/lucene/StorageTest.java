package com.oppsci.ngraphstore.storage.lucene;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.oppsci.ngraphstore.graph.elements.Node;
import com.oppsci.ngraphstore.storage.lucene.spec.SearchStats;

public class StorageTest {

	@Test
	public void singleStoreAndSearchTest() throws IOException {
		String uuid = UUID.randomUUID().toString();
		File tmpFolder = new File(uuid);
		LuceneIndexer indexer = new LuceneIndexer(tmpFolder.getAbsolutePath());

		String subject = "<urn://S>";
		String predicate = "<urn://P>";
		String object = "<urn://O>";
		String graph = "<graph://test>";
		indexer.index(subject, predicate, object, graph);
		indexer.close();
		boolean[] objectsFlag = new boolean[] { true, true, true, true };
		LuceneSearcher searcher = new LuceneSearcher(tmpFolder.getAbsolutePath());
		Collection<Node[]> nodes = searcher.search(subject, objectsFlag, LuceneConstants.SUBJECT, new SearchStats());
		assertTrue(nodes.size() == 1);
		Node[] node = nodes.iterator().next();
		assertEquals(subject, node[0].getNode());
		assertEquals(predicate, node[1].getNode());
		assertEquals(object, node[2].getNode());
		assertEquals(graph, node[3].getNode());
		searcher.close();

		FileUtils.deleteDirectory(tmpFolder);

	}

	@Test
	public void mulitStoreAndSearchTest() throws IOException {
		String uuid = UUID.randomUUID().toString();
		File tmpFolder = new File(uuid);
		LuceneIndexer indexer = new LuceneIndexer(tmpFolder.getAbsolutePath());
		String[] quad1 = new String[] { "<urn://S>", "<urn://P>", "<urn://O1>", "<urn://G1>" };
		String[] quad2 = new String[] { "<urn://S>", "<urn://P1>", "<urn://O1>", "<urn://G1>" };
		String[] quad3 = new String[] { "<urn://S>", "<urn://P1>", "<urn://O>", "<urn://G2>" };
		indexer.index(quad1[0], quad1[1], quad1[2], quad1[3]);
		indexer.index(quad2[0], quad2[1], quad2[2], quad2[3]);
		indexer.index(quad3[0], quad3[1], quad3[2], quad3[3]);
		indexer.commit();
		indexer.close();
		boolean[] objectsFlag = new boolean[] { true, true, true, true };
		LuceneSearcher searcher = new LuceneSearcher(tmpFolder.getAbsolutePath());
		Collection<Node[]> nodes = searcher.search(quad2[1], objectsFlag, LuceneConstants.PREDICATE, new SearchStats());
		assertTrue(nodes.size() == 2);
		assertTrue(checkQuad(quad2, nodes));
		assertTrue(checkQuad(quad3, nodes));
		nodes = searcher.searchRelation(new String[] { quad2[2], quad2[3] }, objectsFlag,
				new String[] { LuceneConstants.OBJECT, LuceneConstants.GRAPH }, new SearchStats());
		assertTrue(nodes.size() == 2);
		assertTrue(checkQuad(quad1, nodes));
		assertTrue(checkQuad(quad2, nodes));

		searcher.close();

		FileUtils.deleteDirectory(tmpFolder);

	}

	@Test
	public void bNodeTest() throws IOException {
		String uuid = UUID.randomUUID().toString();
		File tmpFolder = new File(uuid);
		LuceneIndexer indexer = new LuceneIndexer(tmpFolder.getAbsolutePath());
		String[] quad1 = new String[] { "<urn://S>", "<urn://P>", "_:abc1", "<urn://G1>" };
		String[] quad2 = new String[] { "<urn://S>", "<urn://P>", "_:other", "<urn://G1>" };
		String[] quad3 = new String[] { "_:other", "<urn://P>", "<urn://O>", "<urn://G1>" };
		indexer.index(quad1[0], quad1[1], quad1[2], quad1[3]);
		indexer.index(quad2[0], quad2[1], quad2[2], quad2[3]);
		indexer.index(quad3[0], quad3[1], quad3[2], quad3[3]);
		indexer.close();
		boolean[] objectsFlag = new boolean[] { true, true, true, true };
		LuceneSearcher searcher = new LuceneSearcher(tmpFolder.getAbsolutePath());
		Collection<Node[]> nodes = searcher.search("_:other", objectsFlag, LuceneConstants.OBJECT, new SearchStats());
		assertTrue(nodes.size() == 2);
		assertTrue(checkQuad(quad1, nodes));
		assertTrue(checkQuad(quad2, nodes));

		searcher.close();

		FileUtils.deleteDirectory(tmpFolder);
	}

	@Test
	public void objectFlagTest() throws IOException {
		String uuid = UUID.randomUUID().toString();
		File tmpFolder = new File(uuid);
		LuceneIndexer indexer = new LuceneIndexer(tmpFolder.getAbsolutePath());
		String[] quad1 = new String[] { "<urn://S>", "<urn://P>", "_:abc1", "<urn://G1>" };
		indexer.index(quad1[0], quad1[1], quad1[2], quad1[3]);
		indexer.close();
		boolean[] objectsFlag = new boolean[] { false, false, true, true };
		LuceneSearcher searcher = new LuceneSearcher(tmpFolder.getAbsolutePath());
		Collection<Node[]> nodes = searcher.search("_:other", objectsFlag, LuceneConstants.OBJECT, new SearchStats());
		assertTrue(nodes.size() == 1);
		Node[] node = nodes.iterator().next();
		assertTrue(node.length == 2);
		assertEquals(quad1[2], node[0].getNode());
		assertEquals(quad1[3], node[1].getNode());

		searcher.close();

		FileUtils.deleteDirectory(tmpFolder);
	}

	@Test
	public void deleteSimpleTest() throws IOException {
		String uuid = UUID.randomUUID().toString();
		File tmpFolder = new File(uuid);
		LuceneIndexer indexer = new LuceneIndexer(tmpFolder.getAbsolutePath());
		String[] quad1 = new String[] { "<urn://O>", "<urn://P>", "_:abc1", "<urn://G1>" };
		String[] quad2 = new String[] { "<urn://S>", "<urn://P>", "_:other", "<urn://G1>" };
		String[] quad3 = new String[] { "_:other", "<urn://P>", "<urn://O>", "<urn://G1>" };
		String[] quad4 = new String[] { "_:other", "<urn://P1>", "<urn://O>", "<urn://G1>" };
		indexer.index(quad1[0], quad1[1], quad1[2], quad1[3]);
		indexer.index(quad2[0], quad2[1], quad2[2], quad2[3]);
		indexer.index(quad3[0], quad3[1], quad3[2], quad3[3]);
		indexer.index(quad4[0], quad4[1], quad4[2], quad4[3]);
		indexer.close();
		boolean[] objectsFlag = new boolean[] { true, true, true, true };
		LuceneSearcher searcher = new LuceneSearcher(tmpFolder.getAbsolutePath());
		Collection<Node[]> nodes = searcher.getAllRecords(objectsFlag, new SearchStats());
		assertTrue(nodes.size() == 4);
		assertTrue(checkQuad(quad1, nodes));
		assertTrue(checkQuad(quad2, nodes));
		assertTrue(checkQuad(quad3, nodes));
		assertTrue(checkQuad(quad4, nodes));
		searcher.close();
		FileUtils.deleteDirectory(tmpFolder);
	}

	@Test
	public void deleteTest() throws IOException {
		String uuid = UUID.randomUUID().toString();
		File tmpFolder = new File(uuid);
		LuceneIndexer indexer = new LuceneIndexer(tmpFolder.getAbsolutePath());
		String[] quad1 = new String[] { "<urn://O>", "<urn://P>", "_:abc1", "<urn://G1>" };
		String[] quad2 = new String[] { "<urn://S>", "<urn://P>", "_:other", "<urn://G1>" };
		String[] quad3 = new String[] { "_:other", "<urn://P>", "<urn://O>", "<urn://G1>" };
		String[] quad4 = new String[] { "_:other", "<urn://P1>", "<urn://O>", "<urn://G1>" };
		indexer.index(quad1[0], quad1[1], quad1[2], quad1[3]);
		indexer.index(quad2[0], quad2[1], quad2[2], quad2[3]);
		indexer.index(quad3[0], quad3[1], quad3[2], quad3[3]);
		indexer.index(quad4[0], quad4[1], quad4[2], quad4[3]);
		indexer.delete(quad4[0], quad4[1], quad4[2], quad4[3]);
		indexer.close();
		boolean[] objectsFlag = new boolean[] { true, true, true, true };
		LuceneSearcher searcher = new LuceneSearcher(tmpFolder.getAbsolutePath());
		Collection<Node[]> nodes = searcher.getAllRecords(objectsFlag, new SearchStats());
		assertTrue(nodes.size() == 3);
		assertTrue(checkQuad(quad1, nodes));
		assertTrue(checkQuad(quad2, nodes));
		assertTrue(checkQuad(quad3, nodes));
		searcher.close();
		indexer.reopen();
		indexer.delete(new String[] { "<urn://O>" }, new String[] { LuceneConstants.SUBJECT });
		indexer.delete(new String[] { "_:bnode" }, new String[] { LuceneConstants.OBJECT });
		indexer.close();
		searcher.reopen();
		nodes = searcher.getAllRecords(objectsFlag, new SearchStats());
		assertTrue(nodes.size() == 1);
		assertTrue(checkQuad(quad3, nodes));
		searcher.close();
		indexer.reopen();
		indexer.deleteAll();
		indexer.close();
		searcher.reopen();
		nodes = searcher.getAllRecords(objectsFlag, new SearchStats());
		assertTrue(nodes.size() == 0);
		searcher.close();
		FileUtils.deleteDirectory(tmpFolder);
	}
	
	@Test
	public void updateTest() throws Exception {
		String uuid = UUID.randomUUID().toString();
		File tmpFolder = new File(uuid);
		LuceneIndexer indexer = new LuceneIndexer(tmpFolder.getAbsolutePath());
		String[] quad1 = new String[] { "<urn://O>", "<urn://P>", "_:abc1", "<urn://G1>" };
		String[] quad2 = new String[] { "<urn://S>", "<urn://P>", "_:other", "<urn://G1>" };
		indexer.index(quad1[0], quad1[1], quad1[2], quad1[3]);
		indexer.close();
		indexer.reopen();
		indexer.update(quad1, quad2);
		indexer.close();
		boolean[] objectsFlag = new boolean[] { true, true, true, true };
		LuceneSearcher searcher = new LuceneSearcher(tmpFolder.getAbsolutePath());
		Collection<Node[]> nodes = searcher.getAllRecords(objectsFlag, new SearchStats());
		assertTrue(nodes.size() == 1);
		assertTrue(checkQuad(quad2, nodes));
		searcher.close();
		FileUtils.deleteDirectory(tmpFolder);
	}
	
	@Test
	public void rollbackTest() throws Exception {
		String uuid = UUID.randomUUID().toString();
		File tmpFolder = new File(uuid);
		LuceneIndexer indexer = new LuceneIndexer(tmpFolder.getAbsolutePath());
		String[] quad1 = new String[] { "<urn://O>", "<urn://P>", "_:abc1", "<urn://G1>" };
		indexer.index(quad1[0], quad1[1], quad1[2], quad1[3]);
		indexer.close();
		indexer.reopen();
		indexer.index("test", quad1[1], quad1[2], quad1[3]);
		indexer.rollback();
		indexer.close();
		boolean[] objectsFlag = new boolean[] { true, true, true, true };
		LuceneSearcher searcher = new LuceneSearcher(tmpFolder.getAbsolutePath());
		Collection<Node[]> nodes = searcher.getAllRecords(objectsFlag, new SearchStats());
		assertTrue(nodes.size() == 1);
		assertTrue(checkQuad(quad1, nodes));
		searcher.close();
		FileUtils.deleteDirectory(tmpFolder);
	}
	
	@Test
	public void searchStatsTest() throws IOException {
		String uuid = UUID.randomUUID().toString();
		File tmpFolder = new File(uuid);
		LuceneIndexer indexer = new LuceneIndexer(tmpFolder.getAbsolutePath());
		String[] quad1 = new String[] { "<urn://O>", "<urn://P>", "<urn://S>", "<urn://G1>" };
		String[] quad2 = new String[] { "<urn://O>", "<urn://P>", "<urn://Q>", "<urn://G1>" };
		indexer.index(quad1[0], quad1[1], quad1[2], quad1[3]);
		indexer.index(quad2[0], quad2[1], quad2[2], quad2[3]);
		indexer.close();
		boolean[] objectsFlag = new boolean[] { true, true, true, true };
		//check if SearchStats can provide insights
		LuceneConstants.MAX_SEARCH=1;
		LuceneSearcher searcher = new LuceneSearcher(tmpFolder.getAbsolutePath());
		SearchStats stats = new SearchStats();
		Collection<Node[]> nodes1 = searcher.search(quad1[0], objectsFlag, LuceneConstants.SUBJECT, stats);
		assertTrue(nodes1.size()==1);
		assertTrue(stats.getTotalHits()==2);
		assertTrue(stats.getLastHit()==0);
		Collection<Node[]> nodes2 = searcher.search(quad1[0], objectsFlag, LuceneConstants.SUBJECT, stats);
		assertTrue(nodes2.size()==1);
		nodes1.addAll(nodes2);
		assertTrue(checkQuad(quad1, nodes1));
		assertTrue(checkQuad(quad2, nodes1));
		searcher.close();
		FileUtils.deleteDirectory(tmpFolder);
		LuceneConstants.MAX_SEARCH=20000;	
	}
	
	@Test
	public void exploreTest() throws IOException {
		String uuid = UUID.randomUUID().toString();
		File tmpFolder = new File(uuid);
		LuceneIndexer indexer = new LuceneIndexer(tmpFolder.getAbsolutePath());
		String[] quad1 = new String[] { "<urn://O>", "<urn://P>", "<urn://S>", "<urn://G1>" };
		String[] quad2 = new String[] { "<urn://S>", "<urn://P>", "<urn://Q>", "<urn://G1>" };
		String[] quad3 = new String[] { "<urn://S1>", "<urn://S>", "<urn://Q>", "<urn://G1>" };
		String[] quad4 = new String[] { "<urn://S2>", "<urn://P>", "<urn://Q>", "<urn://G1>" };
		indexer.index(quad1[0], quad1[1], quad1[2], quad1[3]);
		indexer.index(quad2[0], quad2[1], quad2[2], quad2[3]);
		indexer.index(quad3[0], quad3[1], quad3[2], quad3[3]);
		indexer.index(quad4[0], quad4[1], quad4[2], quad4[3]);
		indexer.close();
		boolean[] objectsFlag = new boolean[] { true, true, true, true };
		LuceneSearcher searcher = new LuceneSearcher(tmpFolder.getAbsolutePath());
		String[][] nodes = searcher.explore(quad1[2]);
		assertTrue(nodes.length == 3);
		assertTrue(checkQuadStr(quad1, nodes));
		assertTrue(checkQuadStr(quad2, nodes));
		assertTrue(checkQuadStr(quad3, nodes));
		
		searcher.close();
		FileUtils.deleteDirectory(tmpFolder);
	}
	

	private boolean checkQuad(String[] expected, Collection<Node[]> actual) {
		for (Node[] node : actual) {
			boolean equals = expected[0].equals(node[0].getNode());
			equals &= expected[1].equals(node[1].getNode());
			equals &= expected[2].equals(node[2].getNode());
			equals &= expected[3].equals(node[3].getNode());
			if (equals) {
				return true;
			}
		}
		return false;
	}
	
	private boolean checkQuadStr(String[] expected, String[][] actual) {
		for (String[] node : actual) {
			boolean equals = expected[0].equals(node[0]);
			equals &= expected[1].equals(node[1]);
			equals &= expected[2].equals(node[2]);
			equals &= expected[3].equals(node[3]);
			if (equals) {
				return true;
			}
		}
		return false;
	}
}
