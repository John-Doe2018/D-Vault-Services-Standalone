package com.kirat.solutions.processor;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.kirat.solutions.Constants.BinderConstants;
import com.kirat.solutions.Constants.ErrorCodeConstants;
import com.kirat.solutions.domain.BinderList;
import com.kirat.solutions.logger.FILEITLogger;
import com.kirat.solutions.logger.FILEITLoggerFactory;
import com.kirat.solutions.util.ErrorMessageReader;
import com.kirat.solutions.util.FileInfoPropertyReader;
import com.kirat.solutions.util.FileItException;
import com.kirat.solutions.util.FileUtil;
import com.kirat.solutions.util.ReadJsonUtil;

public class UpdateMasterJson {
	private static final FILEITLogger fileItLogger = FILEITLoggerFactory.getLogger(UpdateMasterJson.class);
	private static final String filePath = FileInfoPropertyReader.getInstance().getString("masterjson.file.path");
	@SuppressWarnings("unchecked")
	public String prepareMasterJson(BinderList bookObject) throws FileItException {
		fileItLogger.info("Entering in to Update Master Json Processor");
		JSONParser parser = new JSONParser();
		JSONObject obj = new JSONObject();
		JSONObject superObj = new JSONObject();
		JSONObject parentObj = new JSONObject();

		boolean isSameName = false;

		// getting the master Json File path
		
		// Check any book with same name already present or not

		File tmpDir = new File(filePath);
		boolean isFile = tmpDir.exists();
		String xmlFilePath = FileUtil.createDynamicFilePath(bookObject.getName());
		if (isFile) {
			isSameName = ReadJsonUtil.CheckBinderWithSameName(filePath, bookObject.getName());
			if (isSameName) {
				throw new FileItException(ErrorCodeConstants.ERR_CODE_0002,
						ErrorMessageReader.getInstance().getString(ErrorCodeConstants.ERR_CODE_0002));
			} else {
				try {
					JSONObject array = (JSONObject) parser.parse(new FileReader(filePath));
					JSONArray jsonArray = (JSONArray) array.get("BookList");
					// Add the new object to existing
					obj.put("Name", bookObject.getName());
					obj.put("Classification", bookObject.getClassification());
					obj.put("Path", xmlFilePath);
					superObj.put(bookObject.getName(), obj);
					jsonArray.add(superObj);
					parentObj.put("BookList", jsonArray);
					FileWriter jsonFile;
					jsonFile = new FileWriter(filePath);
					jsonFile.write(parentObj.toJSONString());
					jsonFile.flush();
					jsonFile.close();
				} catch (IOException e) {
					throw new FileItException(e.getMessage());
				} catch (ParseException e) {
					throw new FileItException(e.getMessage());
				}
			}
		} else if (!isSameName) {
			obj.put("Name", bookObject.getName());
			obj.put("Classification", bookObject.getClassification());
			obj.put("Path", xmlFilePath);
			superObj.put(bookObject.getName(), obj);
			JSONArray bookList = new JSONArray();
			bookList.add(superObj);
			parentObj.put("BookList", bookList);
			try {
				FileWriter jsonFile = new FileWriter(filePath);
				jsonFile.write(parentObj.toJSONString());
				jsonFile.flush();
				jsonFile.close();
			} catch (IOException e) {
				throw new FileItException(e.getMessage());
			}
		} else {
			throw new FileItException(ErrorCodeConstants.ERR_CODE_0002,
					ErrorMessageReader.getInstance().getString(ErrorCodeConstants.ERR_CODE_0002));
		}
		fileItLogger.info("Exiting in to Update Master Json Processor");
		return bookObject.getName();
	}

	public static boolean deleteFromJSON(int key) throws FileItException{
		try {
			File tmpDir = new File(filePath);
			JSONParser parser = new JSONParser();
			JSONObject array = (JSONObject) parser.parse(new FileReader(filePath));
			JSONArray jsonArray = (JSONArray) array.get("BookList");
			JSONObject deletingObject = (JSONObject) jsonArray.get(key);
			jsonArray.remove(deletingObject);
			FileWriter jsonFile = new FileWriter(filePath);
			jsonFile.write(array.toJSONString());
			jsonFile.flush();
			jsonFile.close();
		}catch(IOException | ParseException e) {
			throw new FileItException(e.getMessage());
		}
		
		return true;
	}
}
