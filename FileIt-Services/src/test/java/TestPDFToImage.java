/*import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.cxf.helpers.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import com.kirat.solutions.Constants.BinderConstants;
import com.kirat.solutions.util.FileInfoPropertyReader;
import com.kirat.solutions.util.FileItException;
*/
public class TestPDFToImage {
/*
	public static void main(String[] args) throws FileItException{
		// TODO Auto-generated method stub
		PDDocument document = null;
		BufferedImage bufferedImage = null;
		int i = 0;
		String bookName = "Gyana";
		String extension = BinderConstants.IMG_EXTENSION;
		
		try {
			document = PDDocument.load("D:\\PrivacyByDesignVer1.pdf");
		List<PDPage> pages = document.getDocumentCatalog().getAllPages();
		//int count = document.getDocumentCatalog().getAllPages().size();
		for(PDPage page :pages) {
			i++;
			String imagePath = createDyanmicImagePath(i,bookName,extension);
			bufferedImage = page.convertToImage();
			File outputFile = new File(imagePath);
			ImageIO.write(bufferedImage, "jpg", outputFile);
		}
		//PDPage page = pages.get(0); //first one
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new FileItException(e.getMessage());
		}
	}
	
	public static String createDyanmicImagePath (int i , String bookName,String extension) {
		boolean isDirectory = false;
		String fullContentDirectory = null;
		String absoluteImgPath = null;

		String counter = String.valueOf(i);
		String staticPath = FileInfoPropertyReader.getInstance().getString("doc.static.path");
		fullContentDirectory = staticPath.concat("\\"+bookName+"\\Images");
		java.io.File file =  new File(fullContentDirectory);
		isDirectory = file.isDirectory();
		if(!isDirectory) {
			file.mkdirs();
		}
		absoluteImgPath = fullContentDirectory.concat("\\"+counter.concat(extension));
		return absoluteImgPath;
	}*/
}

