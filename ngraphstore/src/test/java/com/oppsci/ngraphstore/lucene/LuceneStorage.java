package com.oppsci.ngraphstore.lucene;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

import com.oppsci.ngraphstore.graph.elements.Literal;
import com.oppsci.ngraphstore.graph.elements.Node;
import com.oppsci.ngraphstore.storage.lucene.LuceneConstants;
import com.oppsci.ngraphstore.storage.lucene.LuceneIndexer;
import com.oppsci.ngraphstore.storage.lucene.LuceneSearcher;

public class LuceneStorage {

	
	@Test
	public void storage() throws IOException {
		LuceneIndexer index = new LuceneIndexer("luceneTest");
		index.index("<test>", "<test>", "<test>", "GRAPH");
		index.index("<test2>", "<test2>", "\"test\"@de", "GRAPH");
		index.close();
		LuceneSearcher searcher = new LuceneSearcher("luceneTest");
		Collection<Node[]> results = searcher.search("<test2>", new boolean[] {true, true, true, false}, LuceneConstants.SUBJECT);
		Iterator<Node[]> resultsIterator = results.iterator();
		assertTrue(resultsIterator.hasNext());
		Node[] result = resultsIterator.next();
		assertTrue(result.length==3);
		assertEquals("test2", result[0].getValue());
		assertEquals("test2", result[1].getValue());
		assertEquals("test", result[2].getValue());
		assertEquals("de", ((Literal)result[2]).getLangTag());
		
	}
}
