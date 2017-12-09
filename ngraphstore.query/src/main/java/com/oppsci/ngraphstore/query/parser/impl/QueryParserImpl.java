package com.oppsci.ngraphstore.query.parser.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.transaction.NotSupportedException;

import com.oppsci.ngraphstore.query.parser.Query;
import com.oppsci.ngraphstore.query.parser.QueryParser;
import com.oppsci.ngraphstore.query.parser.exceptions.MethodUnkownException;
import com.oppsci.ngraphstore.query.sparql.elements.impl.BGPElement;

public class QueryParserImpl implements QueryParser {

	@Override
	public Query parse(String queryString) throws Exception {
		String internalQueryString = queryString;
		Query query = new Query();
		// get prefixes
		Map<String, String> prefixes = new HashMap<String, String>();
		internalQueryString = retrievePrefixMapping(internalQueryString, prefixes);

		StringBuilder method = new StringBuilder();
		internalQueryString = retrieveMethod(queryString, method);
		if (internalQueryString == null) {
			throw new MethodUnkownException("Query Method: <" + method + "> is unknown");
		}
		// according to method go on with different methods
		switch (method.toString()) {
		case "select":
			return parseSelect(internalQueryString, prefixes);
		case "ask":
			return parseAsk(internalQueryString, prefixes);
		case "describe":
			return parseDescribe(internalQueryString, prefixes);
		case "construct":
			return parseConstruct(internalQueryString, prefixes);
		default:
			// nothing to do here
			return null;
		}
	}

	private Query parseSelect(String queryString, Map<String, String> prefixes) {

		return null;
	}

	private Query parseAsk(String queryString, Map<String, String> prefixes) {

		return null;
	}

	private Query parseDescribe(String queryString, Map<String, String> prefixes) {

		return null;
	}

	private Query parseConstruct(String queryString, Map<String, String> prefixes) {

		return null;
	}

	public String retrieveMethod(String queryString, StringBuilder method) {
		String lcQueryString = queryString.trim().toLowerCase();
		if (lcQueryString.startsWith("select")) {
			method.append("select");
			return queryString.substring(6);
		} else if (lcQueryString.startsWith("ask")) {
			method.append("ask");
			return queryString.substring(3);
		} else if (lcQueryString.startsWith("describe")) {
			method.append("describe");
			return queryString.substring(8);
		} else if (lcQueryString.startsWith("construct")) {
			method.append("construct");
			return queryString.substring(9);
		}
		// set false method and return null as error indication
		method.append(queryString.split("\\s+")[0]);
		return null;
	}

	public String retrievePrefixMapping(String queryString, Map<String, String> mapping) {
		Pattern p = Pattern.compile("^(\\s*PREFIX\\s+\\w+:\\s*<[^\\s]+>)+");
		Matcher matcher = p.matcher(queryString);
		String prologue = "";
		if (matcher.find()) {
			prologue = matcher.group();
		}
		String[] prefixes = prologue.trim().split("PREFIX\\s+");

		for (String prefix : prefixes) {
			if (prefix.isEmpty()) {
				continue;
			}
			String[] map = prefix.trim().split(":\\s*<");
			mapping.put(map[0], "<" + map[1]);
		}
		return queryString.substring(prologue.length());

	}

	public List<BGPElement> retrieveBGPElements(String queryString) throws Exception {
		List<BGPElement> elements = new LinkedList<BGPElement>();
		int startIndex = queryString.indexOf("{");
		int endIndex = queryString.lastIndexOf("}");
		String bgps = queryString.substring(startIndex + 1, endIndex).trim();
		// FIXME this assumes very simple bgps.
		int tripleIndex = 0;
		String triple[] = new String[3];
		while (!bgps.isEmpty()) {
			if (tripleIndex == 3) {
				elements.add(new BGPElement(triple));
				// next should be either . ; or ,
				if (bgps.startsWith("."))
					tripleIndex = 0;
				if (bgps.startsWith(";"))
					tripleIndex = 1;
				if (bgps.startsWith(","))
					tripleIndex = 2;
				bgps = bgps.substring(1).trim();
			} else {
				int index = -1;
				if (bgps.startsWith("<")) {

					// must be URI
					index = bgps.indexOf(">");

				} else if (tripleIndex == 2) {
					// literal
					char emiter = '\'';
					if (bgps.startsWith("\"")) {
						emiter = '"';
					}
					index = bgps.indexOf(emiter, 1);
					if (bgps.length() > index + 1) {
						if (bgps.charAt(index + 1) == '^') {
							// has type
							index = bgps.indexOf("^");
							index = bgps.indexOf(">", index);
						} else if (bgps.charAt(index + 1) == '@') {
							// has lang tag
							Pattern p = Pattern.compile("@\\w+");
							Matcher m = p.matcher(bgps);
							m.find();
							int len = m.group().length();
							index = bgps.indexOf("@");
							// while(bgps.charAt(index))
							index = index + len - 1;
							// index = bgps.indexOf(" ", index)-1;
						}
					}
				} else if (bgps.startsWith("?")) {
					// var
					index = bgps.indexOf(" ") - 1;
				} else {
					// ???
					throw new NotSupportedException("Currently not supported");
				}
				triple[tripleIndex] = bgps.substring(0, index + 1);
				bgps = bgps.substring(index + 1).trim();
				tripleIndex++;
			}

		}
		if (tripleIndex == 3) {
			elements.add(new BGPElement(triple));
			// next should be either . ; or ,
			if (bgps.startsWith("."))
				tripleIndex = 0;
			if (bgps.startsWith(";"))
				tripleIndex = 1;
			if (bgps.startsWith(","))
				tripleIndex = 2;
		}
		return elements;
	}

}
