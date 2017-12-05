package com.oppsci.ngraphstore.processor;

public interface UpdateProcessor {

	public boolean insert(String triple, String graph);
	
	public boolean delete(String triple, String graph);

	public boolean load(String data, String graph);
	
}
