package com.oppsci.ngraphstore.cache;

public class JSONCacheObject {

	private String key;
	private String JSONString;
	private long timestamp;
	private int size;
	private int frequency;
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * @return the jSONString
	 */
	public String getJSONString() {
		return JSONString;
	}
	/**
	 * @param jSONString the jSONString to set
	 */
	public void setJSONString(String jSONString) {
		JSONString = jSONString;
	}
	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}
	/**
	 * @return the frequency
	 */
	public int getFrequency() {
		return frequency;
	}
	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof String) {
			//check if key is same
			return obj.equals(key);
		}
		return super.equals(obj);
		
	}
	
}
