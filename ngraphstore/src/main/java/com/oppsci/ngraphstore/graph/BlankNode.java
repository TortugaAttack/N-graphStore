package com.oppsci.ngraphstore.graph;

import org.json.simple.JSONObject;

public class BlankNode implements Node {

	private String value;
	
	protected BlankNode(String value) {
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
