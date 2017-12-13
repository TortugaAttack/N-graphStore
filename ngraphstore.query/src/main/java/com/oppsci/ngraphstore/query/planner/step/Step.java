package com.oppsci.ngraphstore.query.planner.step;

import java.util.List;

import com.oppsci.ngraphstore.query.planner.merger.Merger;
import com.oppsci.ngraphstore.storage.cluster.overseer.ClusterOverseer;
import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

/**
 * The interface for Steps in the query plannin process. <br/>
 * F.e. to get quads/triples 
 * 
 * @author f.conrads
 *
 */
public interface Step {

	/**
	 * will execute the given step using the ClusterOverseer and returning a SimpleResultSet as answer.
	 * If the Step has childs, each child should be executed as well and merged using the current merger
	 * @param overseer
	 * @return
	 * @throws Exception
	 */
	public SimpleResultSet execute(ClusterOverseer<SimpleResultSet> overseer) throws Exception;

	/**
	 * Has to return if more results are possible.<br/>
	 * If this is a parent step it has to return true as soon as one of the childs is remembered 
	 * as a step with more results
	 * 
	 * @return
	 */
	public boolean isRemembered();

	/**
	 * reset stats and remembered status. so the results will be the same as initial.
	 */
	public void reset();

	/**
	 * Calculates the cumulated restriction account for this Step which will be used for sorting
	 * @return
	 */
	public int calculateRestrictionAccount();

	/**
	 * Sets the graph (either quoted uri using &lt; and &gt; or var name) 
	 * 
	 * will be used to get results
	 *  
	 * @param graph 
	 * @param isVar
	 */
	public void setGraph(String graph, boolean isVar);
	
	/**
	 * provides the parent Step, may be null if this is the root group. 
	 * <br/><br/>
	 * Parent Step is normally  a Group Step
	 * @return
	 */
	public Step getParent();
	
	/**
	 * Sets the parent step for the current Step. 
	 * 
	 * @param parent
	 */
	public void setParent(Step parent);

	/**
	 * Returns the current Merger for the child steps.<br/>
	 * can be null
	 * 
	 * @return
	 */
	public Merger getMerger();

	/**
	 * Sets the merger to use for the child steps of this particaular step (e.g. if this is a group step
	 * the childs will be joined) 
	 * 
	 * @param downMerger
	 */
	public void setMerger(Merger downMerger);

	/**
	 * Returns the Child Steps of this current Step
	 * @return
	 */
	public List<Step> getChildSteps();

	/**
	 * Sets the child steps for this step
	 * @param childSteps
	 */
	public void setChildSteps(List<Step> childSteps);
}
