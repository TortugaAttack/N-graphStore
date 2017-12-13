package com.oppsci.ngraphstore.storage.results;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.oppsci.ngraphstore.graph.elements.Node;
import com.oppsci.ngraphstore.storage.lucene.spec.SearchStats;

public class SimpleResultSet{
	
	private SearchStats stats;
	private List<String> vars = new LinkedList<String>();
	private Collection<Node[]> rows = new LinkedList<Node[]>();

	public SimpleResultSet(List<String> list, Collection<Node[]> results) {
		this.vars = list;
		this.rows = results;
	}

	public SimpleResultSet() {
	}

	/**
	 * @return the header
	 */
	public List<String> getVars() {
		return vars;
	}

	/**
	 * @param header
	 *            the header to set
	 */
	public void setVars(List<String> vars) {
		this.vars = vars;
	}

	/**
	 * @return the rows
	 */
	public Collection<Node[]> getRows() {
		return rows;
	}

	/**
	 * @param rows
	 *            the rows to set
	 */
	public void setRows(Collection<Node[]> rows) {
		this.rows = rows;
	}

	public void addRows(Collection<Node[]> rows) {
		this.rows.addAll(rows);
	}
	
	public void addRow(Node[] node) {
		this.rows.add(node);
	}

	//TODO optional 
	@SuppressWarnings("unchecked")
	public JSONObject asJSON() {
		JSONObject json = new JSONObject();
		JSONArray jsonVars = new JSONArray();
		jsonVars.addAll(this.vars);
		json.put("head", jsonVars);
		JSONArray bindings = new JSONArray();
		for (Node[] row : rows) {
			for (int i = 0; i < row.length; i++) {
				String var = this.vars.get(i);
				JSONObject result = row[i].asJSON();

				JSONObject binding = new JSONObject();
				binding.put(var, result);
				bindings.add(binding);
			}
		}
		json.put("results", bindings);
		return json;
	}

	/**
	 * @return the stats
	 */
	public SearchStats getStats() {
		return stats;
	}

	/**
	 * @param stats the stats to set
	 */
	public void setStats(SearchStats stats) {
		this.stats = stats;
	}


}
