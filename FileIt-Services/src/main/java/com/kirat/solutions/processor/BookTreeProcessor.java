package com.kirat.solutions.processor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.XML;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.kirat.solutions.logger.FILEITLogger;
import com.kirat.solutions.logger.FILEITLoggerFactory;
import com.kirat.solutions.util.FileInfoPropertyReader;
import com.kirat.solutions.util.FileItException;

public class BookTreeProcessor {
	private static final FILEITLogger fileItLogger = FILEITLoggerFactory.getLogger(BookTreeProcessor.class);

	public JSONObject processBookXmltoDoc(String bookName) throws FileItException {
		fileItLogger.info("Entering to Book Tree Processor");

		String line = "", str = "";
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		JSONObject json;
		try {
			documentBuilder = documentFactory.newDocumentBuilder();
			String requiredXmlPath = "";
			JSONParser parser = new JSONParser();
			/*
			 * String filePath =
			 * FileInfoPropertyReader.getInstance().getString("masterjson.file.path");
			 * String requiredXmlPath = ""; JSONParser parser = new JSONParser(); JSONObject
			 * array = null; array = (JSONObject) parser.parse(new FileReader(filePath));
			 * 
			 * JSONArray jsonArray = (JSONArray) array.get("BookList"); for (Object obj :
			 * jsonArray) { JSONObject book = (JSONObject) obj; if
			 * (book.containsKey(bookName)) { JSONObject jsonObject = (JSONObject)
			 * book.get(bookName); requiredXmlPath = (String) jsonObject.get("Path"); } }
			 */
			requiredXmlPath = FileInfoPropertyReader.getInstance().getString("xml.file.path").concat(bookName)
					.concat(".xml");
			BufferedReader br = null;
			br = new BufferedReader(new FileReader(requiredXmlPath));

			while ((line = br.readLine()) != null) {
				str += line;
			}
			br.close();
			org.json.JSONObject jsondata = XML.toJSONObject(str);

			json = (JSONObject) parser.parse(jsondata.toString());
		} catch (ParserConfigurationException | ParseException | IOException e) {
			// TODO Auto-generated catch block
			throw new FileItException(e.getMessage());
		}
		return json;
	}

}
