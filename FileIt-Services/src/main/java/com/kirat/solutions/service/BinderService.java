package com.kirat.solutions.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.kirat.solutions.domain.BinderList;
import com.kirat.solutions.domain.CreateBinderRequest;
import com.kirat.solutions.domain.CreateBinderResponse;
import com.kirat.solutions.domain.SearchBookResponse;
import com.kirat.solutions.domain.UpdateBookRequest;
import com.kirat.solutions.processor.BookTreeProcessor;
import com.kirat.solutions.processor.ContentProcessor;
import com.kirat.solutions.processor.DeleteBookProcessor;
import com.kirat.solutions.processor.LookupBookProcessor;
import com.kirat.solutions.processor.TransformationProcessor;
import com.kirat.solutions.processor.UpdateMasterJson;
import com.kirat.solutions.util.FileItException;
import com.kirat.solutions.util.FileUtil;

public class BinderService {

	@POST
	@Path("create")
	public CreateBinderResponse createBinder(CreateBinderRequest createBinderRequest) throws FileItException {
		CreateBinderResponse createBinderResponse = new CreateBinderResponse();
		String htmlContent = createBinderRequest.getHtmlContent();
		String bookName = null;
		BinderList listOfBinderObj;
		TransformationProcessor transformationProcessor = new TransformationProcessor();
		listOfBinderObj = transformationProcessor.processHtmlToBinderXml(htmlContent);
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
		String s = "Serice was run Successfully";
		return s;
	}

	@POST
	@Path("delete")
	public String deleteBinder(String bookName)
			throws FileItException, IOException, ParseException {
		String succssMsg;
		DeleteBookProcessor deleteBookProcessor = new DeleteBookProcessor();
		succssMsg = deleteBookProcessor.deleteBookProcessor(bookName);
		// append in MasterJson
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
		File file = new File(pathName);
		ResponseBuilder response = Response.ok((Object) file);
		response.header("Content-Disposition", "attachment; filename=PrivacyByDesignVer1.0.pdf");
		return response.build();
	}

	@POST
	@Path("searchBook")
	public SearchBookResponse searchBook(String bookName) throws FileItException {
		SearchBookResponse bookResponse = new SearchBookResponse();
		JSONObject jsonObject = null;
		LookupBookProcessor lookupBookProcessor = new LookupBookProcessor();
		jsonObject = lookupBookProcessor.lookupBookbyName(bookName);
		bookResponse.setJsonObject(jsonObject);
		return bookResponse;
	}

}
