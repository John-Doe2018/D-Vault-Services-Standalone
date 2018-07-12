package com.kirat.solutions.processor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.kirat.solutions.Constants.BinderConstants;
import com.kirat.solutions.domain.FileItContext;
import com.kirat.solutions.util.FileInfoPropertyReader;
import com.kirat.solutions.util.FileItException;

public class ContentProcessor {
	private final static String JSONExtention = ".json";
	private final static String staticPath = FileInfoPropertyReader.getInstance().getString("doc.static.path");
	FileItContext fileItContext;
	Map<String, String> paths = new HashMap<String, String>();
	private static ContentProcessor INSTANCE;
	static int pagecounter = 0;

	public static synchronized ContentProcessor getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new ContentProcessor();
		}
		return INSTANCE;
	}

	@SuppressWarnings({ "static-access", "unchecked" })
	public void processContentImage(String bookName) throws FileItException {
		try {
			String filename = FileInfoPropertyReader.getInstance().getString("xml.file.path") + bookName + ContentProcessor.JSONExtention;
			FileReader BookJson;
			JSONObject superObj = new JSONObject();
			JSONObject bookObj;
			JSONParser parser = new JSONParser();
			if (new File(filename).exists()) {
				BookJson = new FileReader(filename);
				superObj = (JSONObject) parser.parse(BookJson);
			} else {
				superObj.put("ImagePath", "/" + bookName + "/Images");
			}
			PDDocument document = null;
			BufferedImage bufferedImage = null;
			String extension = BinderConstants.IMG_EXTENSION;
			paths = (Map<String,String>) fileItContext.get(BinderConstants.CONTXT_DOC);
			for (String key : paths.keySet()) {
				bookObj = new JSONObject();
				// i = counter;
				document = PDDocument.load(paths.get(key));
				List<PDPage> pages = document.getDocumentCatalog().getAllPages();
				// int count =
				// document.getDocumentCatalog().getAllPages().size();
				bookObj.put("Start", pagecounter);
				for (PDPage page : pages) {
					pagecounter++;
					String imagePath = createDyanmicImagePath(pagecounter, bookName, extension);
					bufferedImage = page.convertToImage();
					File outputFile = new File(imagePath);
					ImageIO.write(bufferedImage, "jpg", outputFile);
				}
				bookObj.put("End", pagecounter);
				superObj.put(key, bookObj);
			}
			FileWriter bookJsonFile = new FileWriter(filename);
			bookJsonFile.write(superObj.toJSONString());
			bookJsonFile.flush();
			bookJsonFile.close();
		} catch (IOException e) {
			throw new FileItException(e.getMessage());
		}catch(ParseException pe){
			throw new FileItException(pe.getMessage());
		}

	}
	
	public static boolean deleteFileImage(String bookName, String fileName) throws FileItException{
		try{
			String filename = FileInfoPropertyReader.getInstance().getString("xml.file.path") + bookName + ContentProcessor.JSONExtention;
			JSONObject superObj = (JSONObject) new JSONParser().parse(new FileReader(filename));
			String extension = BinderConstants.IMG_EXTENSION;
			JSONObject documentObject = (JSONObject) superObj.get(fileName);
			for(int i=Integer.parseInt(documentObject.get("Start").toString()) + 1; i<=Integer.parseInt(documentObject.get("End").toString()); i++){
				String imagePath = createDyanmicImagePath(i, bookName, extension);
				new File(imagePath).delete();
			}
			superObj.remove(fileName);
			FileWriter bookJsonFile = new FileWriter(filename);
			bookJsonFile.write(superObj.toJSONString());
			bookJsonFile.flush();
			bookJsonFile.close();
			return true;
		}catch(ParseException pe){
			throw new FileItException(pe.getMessage());
		}catch (IOException ioe) {
			throw new FileItException(ioe.getMessage());
		}
	}

	public static String createDyanmicImagePath(int i, String bookName, String extension) {
		boolean isDirectory = false;
		String fullContentDirectory = null;
		String absoluteImgPath = null;

		String counter = String.valueOf(i);
		fullContentDirectory = staticPath.concat("\\" + bookName + "\\Images");
		java.io.File file = new File(fullContentDirectory);
		isDirectory = file.isDirectory();
		if (!isDirectory) {
			file.mkdirs();
		}
		absoluteImgPath = fullContentDirectory.concat("\\" + counter.concat(extension));
		return absoluteImgPath;
	}

}
