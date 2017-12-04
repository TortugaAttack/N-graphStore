package con.oppsci.ngraphstore.query;

import java.util.List;

import com.oppsci.ngraphstore.sparql.elements.BGPElement;
import com.oppsci.ngraphstore.sparql.elements.Filter;

import con.oppsci.ngraphstore.results.SimpleResultSet;
import con.oppsci.ngraphstore.storage.lucene.LuceneSearcher;

public class QueryPlanner {

	private LuceneSearcher searcher;
	
	public SimpleResultSet search(String query){
		Query q = new Query(query);
		//sort BGP according to restrictive first principle
		List<BGPElement> rfpSortedBGPs = sortToRfp(q.getBGPElements());
		//apply BGP Lucene Search
		List<String[]> currentResults = applyBGPs(rfpSortedBGPs);
		//apply in memory Filter search
		currentResults = applyFilter(currentResults, q.getFilter());
		//return results
		return new SimpleResultSet(q.getVars(), currentResults);
	}
	
	public List<BGPElement> sortToRfp(List<BGPElement> bgps){
		return null;
		
	}
	
	public List<String[]> applyBGPs(List<BGPElement> rfpSortedBGPs){
		return null;		
	}
	
	public List<String[]> applyFilter(List<String[]> currentResults, List<Filter> filter){
		return null;
	}
	
}
