package com.oppsci.ngraphstore.graph;

import org.json.simple.JSONObject;

public class Literal implements Node {

	private String value;
	private String langTag;
	private String datatype;
	
	protected Literal(String value) {
		this.value=value;
	}
	protected Literal(String value, String tag, boolean isLang) {
		this.value=value;
		if(isLang) {
			this.langTag=tag;
		}
		else {
			this.datatype=tag;
		}
	}

	public String getLangTag() {
		return langTag;
	}

	public String getDatatype() {
		return datatype;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String getNode() {
		StringBuilder node = new StringBuilder();
		if (value.contains("\"")) {
			node.append("\'").append(value).append("\'");
		} else {
			node.append("\"").append(value).append("\"");
		}
		if(langTag!=null) {
			node.append("@").append(langTag);
		}
		else if(datatype!=null) {
			node.append("^^<").append(datatype).append(">");
		}
		return node.toString();
	}

	@Override
	public String asJSONString() {
		return asJSON().toJSONString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject asJSON() {
		JSONObject literal = new JSONObject();
		String type = datatype == null ? "literal" : "typed-literal";
		literal.put("type", type);
		literal.put("value", value);
		if (langTag != null)
			literal.put("langTag", langTag);
		if (datatype != null)
			literal.put("datatype", datatype);

		return literal;
	}

	@Override
	public boolean isLiteral() {
		return true;
	}

	@Override
	public boolean isURI() {
		return false;
	}

	@Override
	public boolean isBlankNode() {
		return false;
	}

}
