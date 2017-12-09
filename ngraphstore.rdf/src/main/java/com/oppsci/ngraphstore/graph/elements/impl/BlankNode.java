package com.oppsci.ngraphstore.graph.elements.impl;

import org.json.simple.JSONObject;

import com.oppsci.ngraphstore.graph.elements.Node;

public class BlankNode implements Node {

	private String value;
	
	public BlankNode(String value) {
		this.value = value;
	}
	
	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String getNode() {
		return value;
	}

	@Override
	public String asJSONString() {
		return asJSON().toJSONString();
	}

	@Override
	public JSONObject asJSON() {
		JSONObject bnode = new JSONObject();
		bnode.put("type", "bnode");
		bnode.put("value", value);
		return bnode;
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public boolean isURI() {
		return false;
	}

	@Override
	public boolean isBlankNode() {
		return true;
	}

}
