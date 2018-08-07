package com.kirat.solutions.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.kirat.solutions.Constants.BinderConstants;
import com.kirat.solutions.domain.AddFileRequest;
import com.kirat.solutions.domain.BinderList;
import com.kirat.solutions.domain.CreateBinderRequest;
import com.kirat.solutions.domain.CreateBinderResponse;
import com.kirat.solutions.domain.DeleteBookRequest;
import com.kirat.solutions.domain.DeleteFileRequest;
import com.kirat.solutions.domain.DownloadFileRequest;
import com.kirat.solutions.domain.SearchBookRequest;
import com.kirat.solutions.domain.SearchBookResponse;
import com.kirat.solutions.domain.UpdateBookRequest;
import com.kirat.solutions.processor.AddFileProcessor;
import com.kirat.solutions.processor.BookTreeProcessor;
import com.kirat.solutions.processor.ContentProcessor;
import com.kirat.solutions.processor.DeleteBookProcessor;
import com.kirat.solutions.processor.DownloadBookProcessor;
import com.kirat.solutions.processor.LookupBookProcessor;
import com.kirat.solutions.processor.TransformationProcessor;
import com.kirat.solutions.processor.UpdateMasterJson;
import com.kirat.solutions.util.FileInfoPropertyReader;
import com.kirat.solutions.util.FileItContext;
import com.kirat.solutions.util.FileItException;
import com.kirat.solutions.util.FileUtil;

public class BinderService {

	@POST
	@Path("create")
	public CreateBinderResponse createBinder(CreateBinderRequest createBinderRequest) throws FileItException {
		CreateBinderResponse createBinderResponse = new CreateBinderResponse();
		String htmlContent = createBinderRequest.getHtmlContent();
		String bookName = null;
		TransformationProcessor transformationProcessor = new TransformationProcessor();
		BinderList listOfBinderObj = transformationProcessor.createBinderList(htmlContent);
		transformationProcessor.processHtmlToBinderXml(listOfBinderObj);
		// append in MasterJson
		UpdateMasterJson updateMasterJson = new UpdateMasterJson();
		bookName = updateMasterJson.prepareMasterJson(listOfBinderObj);
		// Prepare the Content Structure of the book with image
		/*ContentProcessor contentProcessor = ContentProcessor.getInstance();
		contentProcessor.processContentImage(bookName);*/
		createBinderResponse.setSuccessMsg("Binder Successfully Created.");
		return createBinderResponse;
	}

	@POST
	@Path("update")
	public String updateBinder(UpdateBookRequest updateBookRequest) throws FileItException {
		// append in MasterJson
		TransformationProcessor transformationProcessor = new TransformationProcessor();
		DeleteBookProcessor deleteBookProcessor = new DeleteBookProcessor();
		UpdateMasterJson updateMasterJson = new UpdateMasterJson();
		BinderList BinderObj = transformationProcessor.createBinderList(updateBookRequest.getHtmlContent());
		transformationProcessor.processHtmlToBinderXml(BinderObj);
		deleteBookProcessor.deleteBookProcessor(updateBookRequest.getId());
		updateMasterJson.prepareMasterJson(BinderObj);
		return "Success";
	}

	@POST
	@Path("delete")
	@Produces("application/json")
	public JSONObject deleteBinder(DeleteBookRequest deleteBookRequest) throws FileItException {
		String bookName = deleteBookRequest.getBookName();
		DeleteBookProcessor deleteBookProcessor = new DeleteBookProcessor();
		JSONObject succssMsg = deleteBookProcessor.deleteBookProcessor(bookName);
		return succssMsg;
	}
	
	@POST
	@Path("deleteFile")
	public JSONObject deleteFile(DeleteFileRequest oDeleteFileRequest) throws FileItException {
		JSONObject oJsonObject = new JSONObject();
		try{
			Element topicElement = null;
			ContentProcessor contentProcessor = ContentProcessor.getInstance();
			//contentProcessor.deleteFileImage(oDeleteFileRequest.getBookName(), oDeleteFileRequest.getFileName());
			String xmlfilePath = FileInfoPropertyReader.getInstance().getString("xml.file.path") + oDeleteFileRequest.getBookName()  + ".xml";
			DocumentBuilderFactory docBuilderFac = DocumentBuilderFactory.newInstance();
			docBuilderFac.setNamespaceAware(true);
			DocumentBuilder docbuilder = docBuilderFac.newDocumentBuilder();
			Document document = docbuilder.parse(new File(xmlfilePath));
			NodeList fileList = document.getElementsByTagName("topic");
			for (int i = 0; i < fileList.getLength(); i++) {
				Node element = fileList.item(i);
				if (element.getNodeType() == Node.ELEMENT_NODE) {
					topicElement = (Element) element;
					if (oDeleteFileRequest.getFileName().equals(topicElement.getAttribute("name"))) {
						element.getParentNode().removeChild(topicElement);
						break;
					}
				}
			}
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(document);
			Result res = new StreamResult(new File(xmlfilePath));
			transformer.transform(domSource, res);
			oJsonObject.put("Success", "Deleted Successfully");
			return oJsonObject;
		}catch(DOMException | TransformerException | SAXException | IOException | ParserConfigurationException e){
			throw new FileItException(e.getMessage());
		}
	}
	
	@POST
	@Path("getBookTreeDetail")
	@Produces("application/json")
	public JSONObject BookTreeDetail(String bookName) throws FileItException {
		BookTreeProcessor bookTreeProcessor = new BookTreeProcessor();
		JSONObject document = bookTreeProcessor.processBookXmltoDoc(bookName);
		return document;
	}

	@POST
	@Path("getPDF")
	@Produces("application/pdf")
	public Response getPDF(String pathName) throws FileNotFoundException, IOException, ParseException {
		pathName = FileUtil.correctFilePath(pathName);
		File file = new File(pathName.substring(1, pathName.length() - 1));
		ResponseBuilder response = Response.ok((Object) file);
		response.header("Content-Disposition", "attachment; filename=PrivacyByDesignVer1.0.pdf");
		return response.build();
	}

	@POST
	@Path("searchBook")
	public SearchBookResponse searchBook(SearchBookRequest searchBookRequest) throws FileItException {
		SearchBookResponse bookResponse = new SearchBookResponse();
		String bookName = searchBookRequest.getBookName();
		JSONObject jsonObject = null;
		LookupBookProcessor lookupBookProcessor = new LookupBookProcessor();
		jsonObject = lookupBookProcessor.lookupBookbyName(bookName);
		bookResponse.setJsonObject(jsonObject);
		return bookResponse;
	}

	@POST
	@Path("addFile")
	@Produces("application/json")
	public JSONObject addFile(AddFileRequest oAddFileRequest) throws FileItException {
		AddFileProcessor oAddFileProcessor = new AddFileProcessor();
		oAddFileProcessor.addFilesToBinder(oAddFileRequest);
		ContentProcessor contentProcessor = ContentProcessor.getInstance();
		contentProcessor.processContentImage(oAddFileRequest.getBinderName());
		JSONObject object = new JSONObject();
		object.put("Success", "File Added Successfully");
		return object;
	}
	
	//Advance Search Added
	
	@POST
	@Path("advancedSearch")
	public JSONArray advancedSearch() throws Exception {
		/*InputStream oInputStream = oCloudStorageConfig
				.getFile(CloudPropertiesReader.getInstance().getString("bucket.name"), "test.JSON");
		InputStream oInputStream = Reader.getInstance().getString("masterjson.file.path")
		JSONParser parser = new JSONParser();
		JSONObject array = null;
		array = (JSONObject) parser.parse(new InputStreamReader(oInputStream));*/
		String filePath = FileInfoPropertyReader.getInstance().getString("masterjson.file.path");
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(filePath));
		JSONArray jsonArray = (JSONArray) jsonObject.get("BookList");
		JSONArray oArray = new JSONArray();
		for (Object obj : jsonArray) {
			JSONObject book = (JSONObject) obj;
			Set<String> keys = book.keySet();
			for (String s : keys) {
				oArray.add(s);
			}
		}
		return oArray;
	}
	
	@POST
	@Path("fetchImageDetails")
	@Produces("application/json")
	public List<String> fetchImageDetails(String bookName) throws Exception {
		ContentProcessor contentProcessor = ContentProcessor.getInstance();
		return contentProcessor.processContentImage(bookName);
	}
	
	
	//Download Service for Standalone added 14/07/2018
	
	@POST
	@Path("download")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject downloadFile(DownloadFileRequest oDownloadFileRequest) throws Exception {
		JSONObject obj = new JSONObject();
		obj.put("URL", new DownloadBookProcessor().process(oDownloadFileRequest.getBookName()));
		return obj;
	}
	
	@POST
	@Path("classifiedData")
	public JSONObject getBookClassification() throws Exception {
		/*if (FileItContext.get(BinderConstants.CLASSIFIED_BOOK_NAMES) == null) {
			PrepareClassificationMap
					.createClassifiedMap(FileInfoPropertyReader.getInstance().getString("masterjson.file.path"));
		}*/
		return (JSONObject) FileItContext.get(BinderConstants.CONTXT_CLASSIFICATION);

	}

}
