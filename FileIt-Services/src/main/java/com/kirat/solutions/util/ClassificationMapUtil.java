package com.kirat.solutions.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.kirat.solutions.Constants.BinderConstants;
import com.kirat.solutions.Constants.ErrorCodeConstants;

public class ClassificationMapUtil {

	private static ClassificationMapUtil instance = null;

	//private FileItContext context = new FileItContext();

	private Map<String, List<String>> classificatioMap = new HashMap<String,List<String>>();

	private String pathtoClassificationMap = FileInfoPropertyReader.getInstance().getString("lib.path")+
			BinderConstants.FILE_SEPARATOR+ "ClassificationMap.JSON";

	private ClassificationMapUtil() {
	}

	public static ClassificationMapUtil getInstance() {
		if (instance == null) {
			instance = new ClassificationMapUtil();
		}
		return instance;
	}

	// Read the classification Map JSON file and prepare context
	public boolean addMaptoContext() throws FileItException {
		File mapFile = new File(pathtoClassificationMap);
			try {
				FileReader fr = new FileReader(mapFile);
				JSONParser parser = new JSONParser();
				classificatioMap = (Map<String, List<String>>) parser.parse(fr);
				FileItContext.add(BinderConstants.CONTXT_CLASSIFICATION, classificatioMap);
			} catch (IOException | ParseException e) {
				classificatioMap.put("BlankArray", new ArrayList<>());
				FileItContext.add(BinderConstants.CONTXT_CLASSIFICATION, classificatioMap);
				//throw new FileItException(e.getMessage());
			}
		return true;
	}

	// Add the context to classification Map JSON file
	public boolean addContextToMap() throws FileItException {
		try {
			File mapFile = new File(pathtoClassificationMap);
			if (!mapFile.exists()) {
				mapFile.createNewFile();
			}
			classificatioMap = (Map<String, List<String>>) FileItContext.get(BinderConstants.CONTXT_CLASSIFICATION);
			classificatioMap.remove("BlankArray");
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(mapFile, classificatioMap);
		} catch (IOException e) {
			throw new FileItException(e.getMessage());
		}

		return true;
	}

	public void hasBook(String BookName) {
		
	}
	
	public void putToContext(String classification, String bookName) throws FileItException {
		List<String> temp;
		classificatioMap = (Map<String, List<String>>) FileItContext.get(BinderConstants.CONTXT_CLASSIFICATION);
		if(classificatioMap == null) {
			classificatioMap = new HashMap<String, List<String>>();
			temp = new ArrayList<String>();
		}else if(classificatioMap.get(classification) != null) {
				temp = classificatioMap.get(classification);
		}else {
				temp = new ArrayList<String>();
		}
		temp.add(bookName);
		classificatioMap.put(classification, temp);
		FileItContext.add(BinderConstants.CONTXT_CLASSIFICATION, classificatioMap);
		addContextToMap();
	}

}
