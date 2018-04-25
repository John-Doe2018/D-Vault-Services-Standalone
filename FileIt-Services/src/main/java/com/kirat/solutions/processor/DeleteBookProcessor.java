package com.kirat.solutions.processor;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.kirat.solutions.util.FileInfoPropertyReader;

public class DeleteBookProcessor {

	public String deleteBookProcessor (String deleteBookRequest) throws IOException, ParseException {
		JSONObject parentObj = new JSONObject();
		String deleteMsg = null;
		String filePath = FileInfoPropertyReader.getInstance().getString("masterjson.file.path");
		JSONParser parser = new JSONParser();
		JSONObject array = (JSONObject) parser.parse(new FileReader(filePath));
		JSONArray jsonArray = (JSONArray) array.get("BookList");
		for (Object obj : jsonArray)
		{
			JSONObject book = (JSONObject) obj;
			if(book.containsKey(deleteBookRequest)) {
				jsonArray.remove(obj);
			}
	}
		parentObj.put("BookList", jsonArray);
		FileWriter jsonFile = new FileWriter(filePath);
		jsonFile.write(parentObj.toJSONString());
		jsonFile.flush();
		jsonFile.close();
		return deleteMsg;
	}
	
}
	
