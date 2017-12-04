package con.oppsci.ngraphstore.storage;

import java.util.List;

import con.oppsci.ngraphstore.results.SimpleResultSet;

public class ClusterOverseer {

	private List<Cluster> cluster;
	
	private SimpleResultSet mergeSyncedResults(SimpleResultSet[] results) {
		SimpleResultSet merged = new SimpleResultSet();
		if(results.length>0) {
			//as they are synchronized this is okay
			merged.setVars(results[0].getVars());
		}
		for(SimpleResultSet srs : results) {
			merged.addRows(srs.getRows());
		}
		return merged;
	}
	
}
