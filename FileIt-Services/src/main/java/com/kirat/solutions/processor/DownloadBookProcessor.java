package com.kirat.solutions.processor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.kirat.solutions.util.FileInfoPropertyReader;
import com.kirat.solutions.util.FileItException;

public class DownloadBookProcessor {
	private static String xmlPath = FileInfoPropertyReader.getInstance().getString("xml.file.path");
	private static String tempPath = FileInfoPropertyReader.getInstance().getString("doc.static.path").concat("\\").concat("download"); 
	public DownloadBookProcessor(){
		File tempFolder = new File(tempPath);
		if(tempFolder.exists()){
			tempFolder.delete();
		}
		tempFolder.mkdir();
	}
	
	public String process(String bookname) throws FileItException{
		try{
			String bookAbsPath = xmlPath+bookname+".xml";
			String filePath;
			String fileName;
			FileOutputStream fos;
			ZipOutputStream zos;
			FileInputStream fis;
			fos = new FileOutputStream(new File(tempPath.concat("\\").concat(bookname).concat(".zip")));
			zos = new ZipOutputStream(new BufferedOutputStream(fos));
			DocumentBuilderFactory docBuilderFac = DocumentBuilderFactory.newInstance();
			docBuilderFac.setNamespaceAware(true);
			DocumentBuilder docbuilder = docBuilderFac.newDocumentBuilder();
			Document document = docbuilder.parse(new File(bookAbsPath));
			NodeList fileList = document.getElementsByTagName("topic");
			for(int i = 0; i < fileList.getLength(); i++){
				Node file = fileList.item(i);
				if (file.getNodeType() == Node.ELEMENT_NODE) {
					filePath = ((Element)file).getAttribute("path");
					fileName = ((Element)file).getAttribute("name");
					File fileToZip = new File(filePath);
					fis = new FileInputStream(fileToZip);
					ZipEntry ze = new ZipEntry(fileName);
					zos.putNextEntry(ze);
					byte[] tmp = new byte[4*1024];
					int size = 0;
					while((size = fis.read(tmp)) != -1){
						zos.write(tmp, 0, size);
					}
					zos.flush();
					fis.close();
				}
			}
			zos.close();
			fos.close();
			return "\\".concat("download").concat(bookname).concat(".zip");
		}catch(ParserConfigurationException | SAXException | IOException e){
			throw new FileItException(e.getMessage());
		}
	}
}
