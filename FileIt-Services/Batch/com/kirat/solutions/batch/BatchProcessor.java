package com.kirat.solutions.batch;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.kirat.solutions.processor.UpdateMasterJson;
import com.kirat.solutions.util.ClassificationMapUtil;
import com.kirat.solutions.util.FileInfoPropertyReader;
import com.kirat.solutions.util.FileItException;

public class BatchProcessor {

	private static BatchProcessor instance = null;
	
	private static Timer timerEvent = null;
	
	private static String pathUnproceesedJson = FileInfoPropertyReader.getInstance().getString("masterjson.file.path");
	
	private BatchProcessor() {}
	
	public static void startProcess() throws FileItException {
		if(instance == null) {
			instance= new BatchProcessor();
			timerEvent = new Timer();
		}
		timerEvent.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					instance.processBooks();
				} catch (FileItException e) {
					e.printStackTrace();
				}
			}
		}, 0, 2000);
	}

	//Function gets called every two second to push the up processed books
	public void processBooks() throws FileItException {
		try {
			JSONArray unProcessedBooks = getUnprocessedBooks();
			int book_index = 0;
			for(Object bookObj : unProcessedBooks) {
				Set<String> keys = ((JSONObject)bookObj).keySet();
				for(String bookName : keys) {
					ClassificationMapUtil.getInstance().putToContext((String)((JSONObject)((JSONObject)bookObj).get(bookName)).get("Classification"), bookName);
				}
				UpdateMasterJson.deleteFromJSON(book_index);
				book_index++;
			}
		} catch (FileItException e) {
			throw new FileItException(e.getMessage());
		}
	}
	
	public JSONArray getUnprocessedBooks() throws FileItException{
		JSONArray bookList = null;
		try {
			File unprocessedJSON = new File(pathUnproceesedJson);
			JSONParser parser = new JSONParser();
			JSONObject superObj = (JSONObject) parser.parse(new FileReader(unprocessedJSON));
			bookList = (JSONArray) superObj.get("BookList");
		} catch (IOException | ParseException e) {
			throw new FileItException(e.getMessage());
		}
		return bookList;
	}

}