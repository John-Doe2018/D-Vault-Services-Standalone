package com.kirat.solutions.processor;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.kirat.solutions.util.FileInfoPropertyReader;
import com.kirat.solutions.util.FileItContext;
import com.kirat.solutions.util.FileItException;

public class ContentProcessor {
	private final static String JSONExtention = ".json";
	private final static String staticPath = FileInfoPropertyReader.getInstance().getString("doc.static.path");
	FileItContext fileItContext;
	private static List<String> images; 
	Map<String, String> paths = new HashMap<String, String>();
	private static ContentProcessor INSTANCE;
	static int pagecounter = 0;
	
	private ContentProcessor() {}

	public static synchronized ContentProcessor getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new ContentProcessor();
		}
		return INSTANCE;
	}

	@SuppressWarnings({ "unchecked" })
	public List<String> processContentImage(String bookName) throws FileItException {
		try {
			images = new ArrayList<>();
			PDDocument document = null;
			BufferedImage bufferedImage = null;
			paths = readBookDetailsfromXml(bookName);
			for (String key : paths.keySet()) {
				document = PDDocument.load(paths.get(key));
				List<PDPage> pages = document.getDocumentCatalog().getAllPages();
				for (PDPage page : pages) {
					bufferedImage = page.convertToImage();
					ByteArrayOutputStream imageByte = new ByteArrayOutputStream();
					ImageIO.write(bufferedImage, "jpg", imageByte);
					imageByte.flush();
					String base64ImageString = Base64.getEncoder().encodeToString(imageByte.toByteArray());
					images.add(("data:image/jpg;base64,").concat(base64ImageString));
				}
			}
			return images;
		} catch (IOException e) {
			throw new FileItException(e.getMessage());
		}
	}
	
	public Map<String, String> readBookDetailsfromXml(String bookName) throws FileItException{
		Map<String, String> pathmap = new HashMap<>();
		try {
			String xmlfilePath = FileInfoPropertyReader.getInstance().getString("xml.file.path") + bookName  + ".xml";
			DocumentBuilderFactory docBuilderFac = DocumentBuilderFactory.newInstance();
			docBuilderFac.setNamespaceAware(true);
			DocumentBuilder docbuilder = docBuilderFac.newDocumentBuilder();
			Document document = docbuilder.parse(new File(xmlfilePath));
			NodeList fileList = document.getElementsByTagName("topic");
			for(int fileIndex=0; fileIndex<fileList.getLength(); fileIndex++) {
				Node file = fileList.item(fileIndex);
				if(file.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) file;
					pathmap.put(element.getAttribute("name") , element.getAttribute("path"));
				}
			}
		}catch(IOException | ParserConfigurationException | SAXException e) {
			throw new FileItException(e.getMessage());
		}
		return pathmap;
	}
	
	/*public static boolean deleteFileImage(String bookName, String fileName) throws FileItException{
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
	}*/

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
