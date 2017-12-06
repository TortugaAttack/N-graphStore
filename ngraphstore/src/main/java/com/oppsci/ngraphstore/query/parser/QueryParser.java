package com.oppsci.ngraphstore.query.parser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.oppsci.ngraphstore.query.sparql.elements.BGPElement;

public class QueryParser {

	public Query parse(String queryString) throws Exception {
		Query query = new Query();
		query.setPrefixMapping(retrievePrefixMapping(queryString));
		query.setElements(retrieveBGPElements(queryString));
		return query;
	}

	public Map<String, String> retrievePrefixMapping(String queryString) {
		Map<String, String> mapping = new HashMap<String, String>();
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
		return mapping;

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
//							while(bgps.charAt(index))
							index = index+len-1;
//							index = bgps.indexOf(" ", index)-1;
						}
					}
				} else if (bgps.startsWith("?")) {
					// var
					index = bgps.indexOf(" ")-1;
				} else {
					// ???
					throw new Exception("Currently not supported");
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
