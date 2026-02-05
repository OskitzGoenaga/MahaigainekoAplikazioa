package Faktura;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class SortuFaktura {
	public static void sortu() {
		try (PDDocument document = new PDDocument()) {
			PDPage page = new PDPage(PDRectangle.A4);
			document.addPage(page);
 
			try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
				contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
				contentStream.beginText();
				contentStream.newLineAtOffset(100, 700);
				contentStream.showText("Hello World! This is a test PDF using PDFBox.");
				contentStream.endText();
			}
 
			document.save("HelloWorld_PDFBox.pdf");
			System.out.println("PDF created successfully.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
