package com.oppsci.ngraphstore.graph;

import org.json.simple.JSONObject;

public interface Node {

	
	public String getValue();
	
	public String getNode();
	
	public String asJSONString();
	
	public JSONObject asJSON();
	
	public boolean isLiteral();
	
	public boolean isURI();
	
	public boolean isBlankNode();
}
