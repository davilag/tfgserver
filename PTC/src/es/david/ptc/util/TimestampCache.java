package es.david.ptc.util;

import java.util.ArrayList;
import java.util.List;

public class TimestampCache {
	private List<Long> timeStampCache;
	private static TimestampCache singleton;
	
	public TimestampCache(){
		timeStampCache = new ArrayList<Long>();
	}
	
	public static TimestampCache getSingleton(){
		if(singleton==null){
			singleton = new TimestampCache();
		}
		return singleton;
	}
	
	public void addTimeStamp(Long ts){
		this.timeStampCache.add(ts);
	}
	
	public boolean hasTimeStamp(Long ts){
		return this.timeStampCache.contains(ts);
	}
}
