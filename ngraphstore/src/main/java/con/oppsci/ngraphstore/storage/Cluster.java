package con.oppsci.ngraphstore.storage;

import java.util.List;

import con.oppsci.ngraphstore.query.QueryPlanner;
import con.oppsci.ngraphstore.results.SimpleResultSet;

/**
 * One of the Nth Cluster 
 * 
 * <br/>
 * Will 
 * 
 * @author f.conrads
 *
 */
public class Cluster {

	private QueryPlanner queryPlanner;
	
	public SimpleResultSet select(String query){
		return queryPlanner.search(query);
	}

}
