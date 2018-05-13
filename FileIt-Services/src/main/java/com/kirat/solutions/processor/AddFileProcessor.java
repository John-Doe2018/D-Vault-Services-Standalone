package com.kirat.solutions.processor;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.kirat.solutions.Constants.BinderConstants;
import com.kirat.solutions.domain.AddFileRequest;
import com.kirat.solutions.domain.FileItContext;
import com.kirat.solutions.logger.FILEITLogger;
import com.kirat.solutions.logger.FILEITLoggerFactory;
import com.kirat.solutions.util.FileInfoPropertyReader;
import com.kirat.solutions.util.FileItException;

public class AddFileProcessor {
	private static final FILEITLogger fileItLogger = FILEITLoggerFactory.getLogger(AddFileProcessor.class);
	FileItContext fileItContext;
	List<String> pathNamesList = new ArrayList<String>();

	public void addFilesToBinder(AddFileRequest oAddFileRequest) throws FileItException {
		fileItLogger.info("Entering to Add File Processor");
		try {
			String filePath = FileInfoPropertyReader.getInstance().getString("masterjson.file.path");
			JSONParser parser = new JSONParser();
			JSONObject array = (JSONObject) parser.parse(new FileReader(filePath));
			JSONArray jsonArray = (JSONArray) array.get("BookList");
			String xmlPath = null;
			for (Object obj : jsonArray) {
				JSONObject book = (JSONObject) obj;
				if (book.containsKey(oAddFileRequest.getBinderName())) {
					JSONObject jsonObject = (JSONObject) book.get(oAddFileRequest.getBinderName());
					xmlPath = (String) jsonObject.get("Path");
				}
			}
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(xmlPath);
			Node topicRef = doc.getElementsByTagName("topicref").item(0);
			for (int i = 0; i < oAddFileRequest.getoBookRequests().size(); i++) {
				Element topic = doc.createElement("topic");
				topic.setAttribute(BinderConstants.ID, oAddFileRequest.getoBookRequests().get(i).getId());
				topic.setAttribute(BinderConstants.NAME, oAddFileRequest.getoBookRequests().get(i).getName());
				topic.setAttribute(BinderConstants.PATH, oAddFileRequest.getoBookRequests().get(i).getPath());
				topic.setAttribute(BinderConstants.TYPE, oAddFileRequest.getoBookRequests().get(i).getType());
				topic.setAttribute(BinderConstants.VERSION, oAddFileRequest.getoBookRequests().get(i).getVersion());
				topicRef.appendChild(topic);
				pathNamesList.add(oAddFileRequest.getoBookRequests().get(i).getPath());
			}
			FileItContext.add(BinderConstants.CONTXT_PATH_NAMES, pathNamesList);

			// create the xml file
			// transform the DOM Object to an XML File
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(doc);
			StreamResult streamResult = new StreamResult(new File(xmlPath));
			transformer.transform(domSource, streamResult);
		} catch (Exception e) {
			// TODO: handle exception
			throw new FileItException(e.getMessage());
		}

	}
}
