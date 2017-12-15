package com.oppsci.ngraphstore.storage.cluster.overseer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.store.AlreadyClosedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.oppsci.ngraphstore.graph.Triple;
import com.oppsci.ngraphstore.graph.TripleFactory;
import com.oppsci.ngraphstore.graph.elements.Node;
import com.oppsci.ngraphstore.graph.elements.NodeFactory;
import com.oppsci.ngraphstore.storage.cluster.overseer.impl.ClusterOverseerImpl;
import com.oppsci.ngraphstore.storage.lucene.LuceneConstants;
import com.oppsci.ngraphstore.storage.lucene.LuceneIndexer;
import com.oppsci.ngraphstore.storage.lucene.LuceneSearcher;
import com.oppsci.ngraphstore.storage.lucene.spec.SearchStats;
import com.oppsci.ngraphstore.storage.lucene.spec.impl.LuceneSearchSpec;
import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

public class ClusterOverseerTest {

	private String folder;
	private LuceneIndexer indexer0;
	private LuceneIndexer indexer1;
	private LuceneSearcher searcher0;
	private LuceneSearcher searcher1;
	private ClusterOverseerImpl overseer;
	private Node[][] testNodes;

	@Before
	public void init() throws IOException {
		folder = UUID.randomUUID().toString();
		indexer0 = new LuceneIndexer(folder+File.separator+"0");
		indexer0.index("<abc://abc>", "<abc://abc1>", "<abc://abc3>", "<abc://abc6>");
		indexer0.index("<abc://abc>", "<abc://abc1>", "<abc://abc4>", "<abc://abc5>");
		indexer0.index("<abc://abc>", "<abc://abc8>", "<abc://abc9>", "<abc://abc5>");
		indexer0.close();
		//
		indexer1 = new LuceneIndexer(folder+File.separator+"1");
		indexer1.index("<abc://abc>", "<abc://abc2>", "<abc://abc4>", "<abc://abc5>");
		indexer1.index("<abc://abc>", "<abc://abc2>", "<abc://abc3>", "<abc://abc5>");
		indexer1.index("<abc://abc>", "<abc://abc9>", "<abc://abc8>", "<abc://abc5>");
		indexer1.index("<abc://abc9>", "<abc://abc8>", "<abc://abc8>", "<abc://abc5>");
		indexer1.close();
		
		searcher0 = new LuceneSearcher(folder+File.separator+"0");
		searcher0.close();
		searcher1 = new LuceneSearcher(folder+File.separator+"1");
		searcher1.close();
		overseer = new ClusterOverseerImpl(folder, 2, 180, false);
		createNodes();
	}

	private void createNodes() {
		testNodes = new Node[4][];
		for(int i=0;i<4;i++) {
			Node[] node = new Node[4];
			node[0] = NodeFactory.createURI("<urn://a"+i+">");
			node[1] = NodeFactory.createURI("<urn://b"+i+">");
			testNodes[i]=node;
		}
	}
	
	@After
	public void close() {
		try {
			FileUtils.deleteDirectory(new File(folder));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private SimpleResultSet[] baseRes(List<String> vars) {
		SimpleResultSet[] results = new SimpleResultSet[2];
		SimpleResultSet res1 = new SimpleResultSet();
		SimpleResultSet res2 = new SimpleResultSet();
		results[0] = res1;
		results[1] = res2;
		
		res1.setVars(vars);
		res2.setVars(vars);
		return results;
	}
	
	@Test
	public void emptyMerge() throws IOException {
		LinkedList<String> vars = new LinkedList<String>();
		vars.add("v1");
		vars.add("v2");
		SimpleResultSet[] results = baseRes(vars);
		//empty merge
		SimpleResultSet merged = overseer.mergeSyncedResults(results);
		assertEquals(vars, merged.getVars());
		assertTrue(merged.getRows().isEmpty());
	}
		
	@Test
	public void oneEmptyMerge() {
		LinkedList<String> vars = new LinkedList<String>();
		vars.add("v1");
		vars.add("v2");
		SimpleResultSet[] results = baseRes(vars);
		results[0].addRow(testNodes[0]);
		SimpleResultSet merged = overseer.mergeSyncedResults(results);
		assertEquals(vars, merged.getVars());
		assertTrue(merged.getRows().size()==1);
		Node[] actual = merged.getRows().iterator().next();
		assertEquals(testNodes[0][0], actual[0]);
		assertEquals(testNodes[0][1], actual[1]);
		//switch
		SimpleResultSet tmp = results[0];
		results[0]=results[1];
		results[1]=tmp;
		merged = overseer.mergeSyncedResults(results);
		assertEquals(vars, merged.getVars());
		assertTrue(merged.getRows().size()==1);
		actual = merged.getRows().iterator().next();
		assertEquals(testNodes[0][0], actual[0]);
		assertEquals(testNodes[0][1], actual[1]);
	}
	
	@Test
	public void fullMergeTest() {
		LinkedList<String> vars = new LinkedList<String>();
		vars.add("v1");
		vars.add("v2");
		SimpleResultSet[] results = baseRes(vars);
		results[0].addRow(testNodes[0]);
		results[0].addRow(testNodes[1]);
		SimpleResultSet merged = overseer.mergeSyncedResults(results);
		assertEquals(vars, merged.getVars());
		assertTrue(merged.getRows().size()==2);
		Iterator<Node[]> nodeIterator = merged.getRows().iterator();
		Node[] actual = nodeIterator.next();
		assertEquals(testNodes[0][0], actual[0]);
		assertEquals(testNodes[0][1], actual[1]);
		actual = nodeIterator.next();
		assertEquals(testNodes[1][0], actual[0]);
		assertEquals(testNodes[1][1], actual[1]);
	}
	
	@Test
	public void select() throws Exception {
		boolean[] objectFields= new boolean[] {true, true, true, true};
		String[] uris = new String[] {"<abc://abc4>"};
		String[] fields = new String[] {LuceneConstants.OBJECT};
		LuceneSearchSpec spec = new LuceneSearchSpec(uris, objectFields, fields);
		SimpleResultSet res = overseer.search(spec, new SearchStats());
		assertTrue(res.getRows().size()==2);
		Iterator<Node[]> nodeIterator = res.getRows().iterator();
		Node[] node1 = nodeIterator.next();
		Node[] node2 = nodeIterator.next();
		boolean firstNode ="<abc://abc1>".equals(node1[1].getNode());
		if(firstNode) {
			assertEquals("<abc://abc2>", node2[1].getNode());
		}
		else {
			assertEquals("<abc://abc1>", node2[1].getNode());
			assertEquals("<abc://abc2>", node1[1].getNode());
		}
	}
	
	@Test
	public void selectAll() throws Exception {
		boolean[] objectFields= new boolean[] {true, true, true, true};
		LuceneSearchSpec spec = new LuceneSearchSpec(null, objectFields, null);
		SimpleResultSet res = overseer.searchAll(spec, new SearchStats());
		assertTrue(res.getRows().size()==7);
	}
	
	@Test
	public void explore() throws Exception {
		String[][] results = overseer.explore("<abc://abc9>");
		assertTrue(results.length==3);
	}
	
	@Test
	public void dropAll() throws Exception {
		overseer.dropAll();
		boolean[] objectFields= new boolean[] {true, true, true, true};
		LuceneSearchSpec spec = new LuceneSearchSpec(null, objectFields, null);
		assertTrue(overseer.searchAll(spec, new SearchStats()).getRows().isEmpty());
	}
	
	@Test
	public void drop() throws Exception {
		overseer.drop("<abc://abc5>");
		boolean[] objectFlags= new boolean[] {true, true, true, true};
		LuceneSearchSpec spec = new LuceneSearchSpec(null, objectFlags, null);
		searcher0.reopen();
		assertTrue(searcher0.search("<abc://abc5>", objectFlags, LuceneConstants.GRAPH, new SearchStats()).isEmpty());
		assertTrue(searcher0.search("<abc://abc6>", objectFlags, LuceneConstants.GRAPH, new SearchStats()).size()==1);
		searcher0.close();
		searcher1.reopen();
		assertTrue(searcher1.search("<abc://abc5>", objectFlags, LuceneConstants.GRAPH, new SearchStats()).isEmpty());
		searcher1.close();
	}
	
	@Test
	public void delete() throws Exception {
		Triple<String>[] triples = TripleFactory.parseTriples("<abc://abc> <abc://abc1> <abc://abc4> .", "<abc://abc5>");
		overseer.delete(triples);
		boolean[] objectFlags= new boolean[] {true, true, true, true};
		searcher0.reopen();
		assertTrue(searcher0.search("<abc://abc5>", objectFlags, LuceneConstants.GRAPH, new SearchStats()).size()==1);
		assertTrue(searcher0.search("<abc://abc6>", objectFlags, LuceneConstants.GRAPH, new SearchStats()).size()==1);
		searcher0.close();
		searcher1.reopen();
		assertTrue(searcher1.search("<abc://abc5>", objectFlags, LuceneConstants.GRAPH, new SearchStats()).size()==4);
		searcher1.close();
	}
	
	@Test
	public void load() throws Exception {
		Triple<String>[] triples = TripleFactory.parseTriples("<abc://Z> <abc://X> <abc://Y> . <abc://U> <abc://V> <abc://W> \n ", "<abc://P>");
		overseer.load(triples);
		boolean[] objectFlags= new boolean[] {true, true, true, true};
		searcher0.reopen();
		Collection<Node[]> zxy = searcher0.getAllRecords(objectFlags, new SearchStats());
		searcher0.close();
		searcher1.reopen();
		Collection<Node[]> uvw = searcher1.getAllRecords(objectFlags, new SearchStats());
		searcher1.close();
		assertTrue(zxy.size()==1);
		assertTrue(uvw.size()==1);
		Node[] zxyNode = zxy.iterator().next();
		Node[] uvwNode = uvw.iterator().next();
		assertEquals("<abc://Z>", zxyNode[0].getNode());
		assertEquals("<abc://X>", zxyNode[1].getNode());
		assertEquals("<abc://Y>", zxyNode[2].getNode());
		assertEquals("<abc://P>", zxyNode[3].getNode());
		assertEquals("<abc://U>", uvwNode[0].getNode());
		assertEquals("<abc://V>", uvwNode[1].getNode());
		assertEquals("<abc://W>", uvwNode[2].getNode());
		assertEquals("<abc://P>", uvwNode[3].getNode());
	}
	

	@Test
	public void add() throws Exception {
		Triple<String>[] triples = TripleFactory.parseTriples("<abc://Z> <abc://X> <abc://Y> . <abc://U> <abc://V> <abc://W> \n ", "<abc://P>");
		overseer.add(triples);
		boolean[] objectFlags= new boolean[] {true, true, true, true};
		searcher0.reopen();
		assertTrue(searcher0.getAllRecords(objectFlags, new SearchStats()).size()==4);
		searcher0.close();
		searcher0.reopen();
		assertTrue(searcher0.search("<abc://Z>", objectFlags, LuceneConstants.SUBJECT, new SearchStats()).size()==1);
		searcher0.close();
		searcher1.reopen();
		assertTrue(searcher1.getAllRecords(objectFlags, new SearchStats()).size()==5);
		assertTrue(searcher1.search("<abc://U>", objectFlags, LuceneConstants.SUBJECT, new SearchStats()).size()==1);
		searcher1.close();
	}
	
	@Test
	public void quadUpdate() throws Exception {
		String[] oldTerms = new String[] {"<abc://abc>", "<abc://abc1>", "<abc://abc3>", "<abc://abc6>"};
		String[] newTerms = new String[] {"<abc://abc10>", "<abc://abc10>", "<abc://abc10>", "<abc://abc10>"};
		overseer.quadUpdate(oldTerms, newTerms);
		boolean[] objectFlags= new boolean[] {true, true, true, true};
		String[] fields = new String[] {LuceneConstants.SUBJECT, LuceneConstants.PREDICATE, LuceneConstants.OBJECT, LuceneConstants.GRAPH};
		searcher0.reopen();
		assertTrue(searcher0.searchRelation(oldTerms, objectFlags, fields,new SearchStats()).isEmpty());
		assertTrue(searcher0.searchRelation(newTerms, objectFlags, fields,new SearchStats()).size()==1);
	}
	
}
