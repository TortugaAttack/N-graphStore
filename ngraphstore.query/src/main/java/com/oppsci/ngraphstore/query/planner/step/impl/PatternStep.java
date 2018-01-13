package com.oppsci.ngraphstore.query.planner.step.impl;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.sparql.core.TriplePath;

import com.oppsci.ngraphstore.graph.TripleFactory;
import com.oppsci.ngraphstore.query.planner.step.AbstractStep;
import com.oppsci.ngraphstore.storage.cluster.overseer.ClusterOverseer;
import com.oppsci.ngraphstore.storage.lucene.LuceneConstants;
import com.oppsci.ngraphstore.storage.lucene.spec.SearchStats;
import com.oppsci.ngraphstore.storage.lucene.spec.impl.LuceneSearchSpec;
import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

/**
 * The PatternStep which executes one TriplePattern
 * 
 * @author f.conrads
 *
 */
public class PatternStep extends AbstractStep {

	private TriplePath pattern;
	private SearchStats[] stats;

	private void init(int size) {
		stats = new SearchStats[size];
		for(int i=0;i<size;i++) {
			stats[i] = new SearchStats();
		}
	}
	
	@Override
	public SimpleResultSet execute(ClusterOverseer<SimpleResultSet> overseer) throws Exception {
		if(stats==null) {
			init(overseer.getClusterSize());
		}
		List<String> uris = new LinkedList<String>();
		List<String> searchFields = new LinkedList<String>();
		boolean[] objectsFlag = new boolean[] { false, false, false, false };
		List<String> vars = new LinkedList<String>();
		
		if (pattern.getSubject().isVariable()) {
			objectsFlag[0] = true;
			vars.add(pattern.getSubject().toString());
		} else {
			searchFields.add(LuceneConstants.SUBJECT);
			uris.add(TripleFactory.parseSubject(pattern.getSubject()));
		}
		if (pattern.getPredicate().isVariable()) {
			objectsFlag[1] = true;
			vars.add(pattern.getPredicate().toString());
		} else {
			searchFields.add(LuceneConstants.PREDICATE);
			uris.add(TripleFactory.parsePredicate(pattern.getPredicate()));
		}
		if (pattern.getObject().isVariable()) {
			objectsFlag[2] = true;
			vars.add(pattern.getObject().toString());
		} else {
			searchFields.add(LuceneConstants.OBJECT);
			uris.add(TripleFactory.parseObject(pattern.getObject()));
		}
		if(graphIsVar) {
			objectsFlag[3]=true;
			vars.add(graph);
		}
		else if(graph!=null) {
			searchFields.add(LuceneConstants.GRAPH);
			uris.add(graph);
		}

		LuceneSearchSpec spec = new LuceneSearchSpec(uris.toArray(new String[] {}), objectsFlag,
				searchFields.toArray(new String[] {}));
		SimpleResultSet ret;
		
		long start = Calendar.getInstance().getTimeInMillis();

		if(uris.isEmpty()) {
			ret = overseer.searchAll(spec, stats);
		}
		else {
			ret = overseer.search(spec, stats);
		}
		long end = Calendar.getInstance().getTimeInMillis();
		System.out.println("One Search took "+(end-start)+"ms");
		ret.setVars(vars);
		return ret;
	}

	/**
	 * Sets the TriplePath for this Step (could be a triple out of variables, iris, literals, bnodes)
	 * 
	 * @param pattern
	 */
	public void setPattern(TriplePath pattern) {
		this.pattern = pattern;
	}

	@Override
	public boolean isRemembered() {
		for(SearchStats stats : this.stats) {
			if( stats.getTotalHits()>stats.getLastHit()+1) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void reset() {
		init(stats.length);
	}

	@Override
	public String toString() {
		return pattern.toString();
	}

	@Override
	public int calculateRestrictionAccount() {
		int i=0;
		if(pattern.getSubject().isURI()) {
			i+=3;
		}
		else if(pattern.getSubject().isBlank()) {
			i+=1;
		}
		if(pattern.getPredicate()!=null && pattern.getPredicate().isURI()) {
				i+=2;
			
		}
		else {
			//is path
			i+=1;
		}
		
		if(pattern.getObject().isURI()) {
			i+=3;
		}
		else if(pattern.getObject().isBlank()) {
			i+=1;
		}
		return i;
	}



}
