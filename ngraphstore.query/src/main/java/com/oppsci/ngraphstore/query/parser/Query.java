package com.oppsci.ngraphstore.query.parser;

import java.util.List;
import java.util.Map;

import com.oppsci.ngraphstore.query.sparql.elements.impl.BGPElement;
import com.oppsci.ngraphstore.query.sparql.elements.impl.Filter;


public class Query {

	private List<String> projectionVars;
	private List<BGPElement> elements;
	private Map<String, String> prologue;
	
	
	public void addPrefixMapping(String prefix, String uri) {
		prologue.put(prefix, uri);
	}
	
	public void setPrefixMapping(Map<String, String> prologue) {
		this.prologue = prologue;
	}
	
	public void addProjectionVar(String var) {
		this.projectionVars.add(var);
	}
	public void setProjectionVars(List<String> vars) {
		this.projectionVars = vars;
	}
	
	public List<String> getProjectionVars() {
		return projectionVars;
	}

	public void addBGPElement(BGPElement bgp) {
		this.elements.add(bgp);
	}
	
	/**
	 * @return the elements
	 */
	public List<BGPElement> getElements() {
		return elements;
	}

	/**
	 * @param elements the elements to set
	 */
	public void setElements(List<BGPElement> elements) {
		this.elements = elements;
	}

	public List<Filter> getFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	public String createCacheKey() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
