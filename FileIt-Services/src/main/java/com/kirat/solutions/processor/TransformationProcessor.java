package com.kirat.solutions.processor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.ObjectMapper;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.kirat.solutions.Constants.BinderConstants;
import com.kirat.solutions.domain.BinderList;
import com.kirat.solutions.domain.Children;
import com.kirat.solutions.logger.FILEITLogger;
import com.kirat.solutions.logger.FILEITLoggerFactory;
import com.kirat.solutions.util.FileItContext;
import com.kirat.solutions.util.FileItException;
import com.kirat.solutions.util.FileUtil;

public class TransformationProcessor {
	private static final FILEITLogger fileItLogger = FILEITLoggerFactory.getLogger(TransformationProcessor.class);
	FileItContext fileItContext;
	Map<String, String> pathNamesList = new HashMap<String, String>();
	public boolean processHtmlToBinderXml(BinderList binderObject)
			throws FileItException  {
		fileItLogger.info("Entering in to processHtmlToBinderXml");
		/*BinderList binderObject = null;*/
		/*ObjectMapper objectMapper = new ObjectMapper();
		JsonFactory f = new JsonFactory();
		JsonParser jp = null;
		try {
			jp = f.createJsonParser(htmlContent);
			jp.nextToken();
			while (jp.nextToken() == JsonToken.FIELD_NAME) {
				binderObject = objectMapper.readValue(jp, BinderList.class);
			}
		} catch (IOException e) {
			throw new FileItException(e.getMessage()) ;
		}*/
		prepareBinderXML(binderObject);
		fileItLogger.info("Exiting in to processHtmlToBinderXml");
		return true;
	}

	public void prepareBinderXML(BinderList binderlist) throws FileItException {
		String xmlFilePath = FileUtil.createDynamicFilePath(binderlist.getName());
		try {
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			org.w3c.dom.Document document = documentBuilder.newDocument();
			// root element
			Element root = document.createElement("map");
			document.appendChild(root);
			String uniqueID = UUID.randomUUID().toString();
			root.setAttribute("id", uniqueID);

			Element title = document.createElement("title");
			title.setNodeValue(binderlist.getName());
			root.appendChild(title);

			Element body = document.createElement("body");
			root.appendChild(body);
			Element topicref = document.createElement("topicref");
			body.appendChild(topicref);

			// set an attribute to topicref element
			Attr attr = document.createAttribute("navtitle");
			attr.setValue(binderlist.getName());
			Attr type = document.createAttribute("type");
			type.setValue(BinderConstants.BINDER);
			Attr id = document.createAttribute("id");
			id.setValue("topicref");
			Attr classification = document.createAttribute("classification");
			classification.setValue(binderlist.getClassification());
			topicref.setAttributeNode(attr);
			topicref.setAttributeNode(type);
			topicref.setAttributeNode(classification);
			topicref.setAttributeNode(id);
			for (Children child : binderlist.getChildren()) {
				Element topic = document.createElement("topic");
				topic.setAttribute(BinderConstants.NAME, child.getName());
				topic.setAttribute(BinderConstants.PATH, child.getPath());
				topic.setAttribute(BinderConstants.TYPE, child.getType());
				topic.setAttribute(BinderConstants.VERSION, child.getVersion());
				topic.setAttribute(BinderConstants.ID, (String.valueOf(child.getId())));
				topicref.appendChild(topic);
				pathNamesList.put(child.getName(), child.getPath());
			}
			FileItContext.add(BinderConstants.CONTXT_DOC, pathNamesList);
			// create the xml file
			// transform the DOM Object to an XML File
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(new File(xmlFilePath));
			transformer.transform(domSource, streamResult);
		} catch (Exception e) {
			throw new FileItException(e.getMessage()) ;
		}

	}
	
	public BinderList createBinderList(String htmlContent) throws FileItException{
		BinderList binderObject = null;
		ObjectMapper objectMapper = new ObjectMapper();
		JsonFactory f = new JsonFactory();
		JsonParser jp = null;
		try {
			jp = f.createJsonParser(htmlContent);
			jp.nextToken();
			while (jp.nextToken() == JsonToken.FIELD_NAME) {
				binderObject = objectMapper.readValue(jp, BinderList.class);
			}
		} catch (IOException e) {
			throw new FileItException(e.getMessage());
		}
		return binderObject;
	}

}
