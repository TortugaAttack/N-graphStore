package com.oppsci.ngraphstore.query.planner.step;

import com.oppsci.ngraphstore.storage.cluster.overseer.ClusterOverseer;
import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

public interface Step {

	
	public SimpleResultSet execute(ClusterOverseer<SimpleResultSet> overseer) throws Exception;

	public boolean isRemembered();

	public void reset();

	public int calculateRestrictionAccount();

	public void setGraph(String graph, boolean isVar);
}
