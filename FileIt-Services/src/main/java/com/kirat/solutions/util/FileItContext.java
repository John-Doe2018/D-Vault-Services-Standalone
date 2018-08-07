package com.kirat.solutions.util;

import java.util.HashMap;
import java.util.Map;

public class FileItContext {

private static Map<String,Object> fileitContainer = new HashMap<String, Object>();

	private FileItContext() {};
	
	public static void add(String key,Object value) {
		fileitContainer.put(key,value);
	}
	
	public static void remove(String key) {
		fileitContainer.remove(key);
	}
	
	public static Object get(String key) {
		return fileitContainer.get(key);
	}
	
}
