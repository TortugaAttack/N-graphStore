package com.oppsci.ngraphstore.processor;

public interface UpdateProcessor {

	public boolean insert(String triple, String graph) throws Exception;
	
	public boolean delete(String triple, String graph)  throws Exception;

	public boolean load(String data, String graph)  throws Exception;

	public void quadUpdate(String[] currentTerms, String[] newTerms)  throws Exception;
	
}
