package com.oppsci.ngraphstore.graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

public class TripleFactory {

	
	public static Triple<String>[] parseTriples(String triples) throws IOException{
		BufferedReader reader = new BufferedReader(new StringReader(triples));
		String line="";
		List<Triple<String>> tripleList = new LinkedList<Triple<String>>();
		while((line=reader.readLine())!=null) {
			Triple<String> triple =new Triple<String>();
			line = line.substring(0, line.lastIndexOf(".")).trim();
			int index = line.indexOf(" ");
			triple.setSubject(line.substring(0, index));
			line = line.substring(index).trim();
			index = line.indexOf(" ");
			triple.setPredicate(line.substring(0, index));
			line = line.substring(index).trim();
			triple.setObject(line);
			tripleList.add(triple);
		}
		return tripleList.toArray(new Triple[] {});
	}
}
