package com.oppsci.ngraphstore.graph.elements.impl;

import org.json.simple.JSONObject;

import com.oppsci.ngraphstore.graph.elements.Node;

public class URINode implements Node {

	private String uri;

	public URINode(String uri) {
		this.uri=uri;
	}
	
	@Override
	public String getValue() {
		return uri;
	}

	@Override
	public String getNode() {
		return "<" + uri + ">";
	}

	@Override
	public String asJSONString() {
		return asJSON().toJSONString();
	}

	@Override
	public JSONObject asJSON() {
		JSONObject uriJSON = new JSONObject();
		uriJSON.put("type", "uri");
		uriJSON.put("value", "<" +uri+">");
		return uriJSON;
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public boolean isURI() {
		return true;
	}

	@Override
	public boolean isBlankNode() {
		return false;
	}

}
