package com.oppsci.ngraphstore.graph.elements;

public class NodeFactory {

	public static Node parseNode(String node) {
		String workNode = node.trim();
		if (workNode.startsWith("<")) {
			// uri
			return createURI(workNode.substring(1, workNode.length()-1));
		} else if (workNode.startsWith("_:")) {
			// blank node
			return createBNode(workNode);
		} else {
			// literal
			return parseLiteral(node);
		}
	}
	
	private static Literal parseLiteral(String literalString) {
		int startValue = 1;
		int endValue;
		if (literalString.startsWith("\"")) {
			endValue = literalString.lastIndexOf("\"");
		} else if (literalString.startsWith("'''")) {
			startValue = 3;
			endValue = literalString.lastIndexOf("'''");
		} else {
			endValue = literalString.lastIndexOf("'");
		}
		String value = literalString.substring(startValue, endValue);
		String tag = literalString.substring(endValue+startValue);
		String langTag=null;
		String datatype=null;
		if (tag.startsWith("@")) {
			// has lang
			langTag = tag.substring(1);
		} else if (tag.startsWith("^^")) {
			// is typed-literal
			datatype = tag.substring(2);
			if(datatype.startsWith("<")) {
				datatype = datatype.substring(1, datatype.length()-1);
			}
		}
		return createLiteral(value, langTag, datatype);
	}

	public static Literal createLiteral(String value, String langTag, String datatype) {
		String tag = langTag != null ? langTag : datatype;
		Literal literal = new Literal(value, tag, langTag != null);
		return literal;
	}

	public static URINode createURI(String uri) {
		return new URINode(uri);
	}

	public static BlankNode createBNode(String value) {
		return new BlankNode(value);
	}
}
