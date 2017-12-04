package com.oppsci.ngraphstore.results;

import java.util.LinkedList;
import java.util.List;

import org.apache.jena.sparql.core.Var;

public class SimpleResultSet {

	private List<String> vars = new LinkedList<String>();
	private List<String[]> rows = new LinkedList<String[]>();
	
	
	public SimpleResultSet(List<String> list, List<String[]> results) {
		this.vars = list;
		this.rows = results;
	}
	
	public SimpleResultSet() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the header
	 */
	public List<String> getVars() {
		return vars;
	}
	/**
	 * @param header the header to set
	 */
	public void setVars(List<String> vars) {
		this.vars = vars;
	}
	/**
	 * @return the rows
	 */
	public List<String[]> getRows() {
		return rows;
	}
	/**
	 * @param rows the rows to set
	 */
	public void setRows(List<String[]> rows) {
		this.rows = rows;
	}
	
	public void addRows(List<String[]> rows) {
		this.rows.addAll(rows);
	}
}