package com.oppsci.ngraphstore.cache;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import com.oppsci.ngraphstore.query.parser.Query;
import com.oppsci.ngraphstore.storage.results.SimpleResultSet;

//TODO set semaphore and mutex!
public class JSONResultsCache {
	
	//100 Mbyte
	private static final int DEFAULT_MAX_OBJECT_SIZE=100000000;
	//2 GigaByte
	private static final long DEFAULT_CACHE_SIZE=2000000000;
	// 1 day
	private static final int DEFAULT_MAX_CACHED_TIME = 86400000;
	
	private int maxObjectSize=DEFAULT_MAX_OBJECT_SIZE;
	private int maxCachedTime=DEFAULT_MAX_CACHED_TIME;
	private long cacheSize=DEFAULT_CACHE_SIZE;
	private List<JSONCacheObject> cacheList = new LinkedList<JSONCacheObject>();
	
	public boolean add(Query q, SimpleResultSet results) {
		String serializable = results.asJSON().toJSONString();
		int objectSize = serializable.getBytes().length;
		String key = q.createCacheKey();
		
		if(isCached(q)) {
			//already contains it
			return updateCacheObject(key, serializable, objectSize);
		}
		cleanUp();
		
		if(objectSize>maxObjectSize) {
			return false;
		}
		
		if(getFreeSize()>=objectSize) {
			//free to go
			JSONCacheObject cacheObject = new JSONCacheObject();
			cacheObject.setFrequency(1);
			return true;
		}
		return exchangeIfPossible(serializable, key, objectSize);
	}
	
	private boolean exchangeIfPossible(String serializable, String key, int size) {
		long free = getFreeSize();
		for(JSONCacheObject obj : cacheList) {
			if(obj.getFrequency()==1 && (free+obj.getSize())>=size) {
				cacheList.remove(obj);
				JSONCacheObject newObj = new JSONCacheObject();
				newObj.setFrequency(1);
				newObj.setJSONString(serializable);
				newObj.setKey(key);
				newObj.setSize(size);
				cacheList.add(newObj);
				return true;
			}
		}
		return false;
	}

	private void cleanUp() {
		for(JSONCacheObject obj : cacheList) {
			if(Calendar.getInstance().getTimeInMillis()-obj.getTimestamp()>maxCachedTime) {
				//Record timed out
				cacheList.remove(obj);
			}
		}
	}

	private boolean updateCacheObject(String key, String serializable, int objectSize) {
		//check if updated Object has enough size
		JSONCacheObject oldObj = cacheList.get(cacheList.indexOf(key));
		
		//check space
		long free = getFreeSize();
		if((free+oldObj.getSize())>=objectSize) {
			//good to go
			oldObj.setJSONString(serializable);
			oldObj.setSize(objectSize);
			oldObj.setTimestamp(Calendar.getInstance().getTimeInMillis());
			oldObj.setFrequency(oldObj.getFrequency()+1);
			
		}
		return false;
	}
	
	private long getFreeSize() {
		long currentSize=0;
		for(JSONCacheObject obj : cacheList) {
			currentSize+=obj.getSize();
		}
		return cacheSize-currentSize;
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public String search(Query query) {
		String key = query.createCacheKey();
		return cacheList.get(cacheList.indexOf(key)).getJSONString();
	}
	

	
	@SuppressWarnings("unlikely-arg-type")
	public boolean isCached(Query query) {
		String key = query.createCacheKey();
		return cacheList.contains(key);
	}
	
}
