import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class TestPDF {
	public static void main(String args[]) throws InvalidPasswordException, IOException {
		File file = new File("C:\\Users\\aditya98617\\Desktop\\cesu.pdf");
		String pdfFilePath = "C:\\Users\\aditya98617\\Desktop\\cesu.pdf";
		PDDocument document = PDDocument.load(new File(pdfFilePath));
		PDFRenderer pdfRenderer = new PDFRenderer(document);
		for (int page = 0; page < document.getNumberOfPages(); ++page) {
			BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
			ImageIO.write(bim, "JPEG", new File("C:\\\\Users\\\\aditya98617\\\\Desktop\\\\cesu.jpg"));
			// suffix in filename will be used as the file format
		}
		document.close();
	}
}
