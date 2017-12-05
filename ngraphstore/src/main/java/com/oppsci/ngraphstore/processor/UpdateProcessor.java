package com.oppsci.ngraphstore.processor;

public interface UpdateProcessor {

	public boolean insert(String triple);
	
	public boolean delete(String triple);

	public boolean load(String data);
	
}
