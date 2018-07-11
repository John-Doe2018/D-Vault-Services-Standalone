package com.kirat.solutions.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.kirat.solutions.domain.AddFileRequest;
import com.kirat.solutions.domain.BinderList;
import com.kirat.solutions.domain.CreateBinderRequest;
import com.kirat.solutions.domain.CreateBinderResponse;
import com.kirat.solutions.domain.DeleteBookRequest;
import com.kirat.solutions.domain.SearchBookRequest;
import com.kirat.solutions.domain.SearchBookResponse;
import com.kirat.solutions.domain.UpdateBookRequest;
import com.kirat.solutions.processor.AddFileProcessor;
import com.kirat.solutions.processor.BookTreeProcessor;
import com.kirat.solutions.processor.ContentProcessor;
import com.kirat.solutions.processor.DeleteBookProcessor;
import com.kirat.solutions.processor.LookupBookProcessor;
import com.kirat.solutions.processor.TransformationProcessor;
import com.kirat.solutions.processor.UpdateMasterJson;
import com.kirat.solutions.util.FileInfoPropertyReader;
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
		ContentProcessor contentProcessor = ContentProcessor.getInstance();
		contentProcessor.processContentImage(bookName);
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
	public JSONObject fetchImageDetails(String bookName) throws Exception {
		String filePath = FileInfoPropertyReader.getInstance().getString("xml.file.path") + bookName + ".json";
		JSONParser parser = new JSONParser();
		JSONObject superObj = (JSONObject) parser.parse(new FileReader(filePath));
		return superObj;
	}

}
