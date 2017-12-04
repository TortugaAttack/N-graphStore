package com.oppsci.ngraphstore.query;

import java.util.List;
import java.util.Map;

import com.oppsci.ngraphstore.sparql.elements.BGPElement;


public class Query {

	private List<String> projectionVars;
	private List<BGPElement> elements;
	private Map<String, String> prologue;
	
	protected Query() {
		
	}
	
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
	
	
}
