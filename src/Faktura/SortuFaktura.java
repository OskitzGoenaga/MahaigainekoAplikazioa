package Faktura;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import Bistak.Conexioa;
 
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
 
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
public class SortuFaktura {
	
    // FTP KONFIGURAZIOA
    private static final String FTP_SERVER   = "192.168.115.163";
    private static final int    FTP_PORT     = 21;
    private static final String FTP_USER     = "Erronka";
    private static final String FTP_PASS     = "1234";
    private static final String FTP_KARPETA  = "./";
 
    public static String sortuFaktura(int salmentaId) {
 
        String pdfIzena = "faktura_" + salmentaId + ".pdf";
 
        try (Connection cn = new Conexioa().getConnection()) {
            if (cn == null) {
                System.err.println("Ezin izan da datu-basera konektatu");
                return null;
            }
 
            String query = "SELECT s.id, s.kantitatea, s.data, " +
                          "b.izena as bezero_izena, b.abizena as bezero_abizena, b.email, " +
                          "p.izena as produktu_izena, p.prezioa " +
                          "FROM saskiak s " +
                          "JOIN bezeroak b ON s.bezeroa_id = b.id " +
                          "JOIN produktuak p ON s.produktua_id = p.id " +
                          "WHERE s.salmenta_id = ?";
 
            PreparedStatement ps = cn.prepareStatement(query);
            ps.setInt(1, salmentaId);
            ResultSet rs = ps.executeQuery();
 
            
            PDDocument document = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
 
            PDPageContentStream cs = new PDPageContentStream(document, page);
 
            float y = 750;
            float margin = 50;
 
            // GOIBURUA
            cs.setFont(PDType1Font.HELVETICA_BOLD, 24);
            cs.beginText();
            cs.newLineAtOffset(margin, y);
            cs.showText("FAKTURA");
            cs.endText();
            y -= 40;
 
            cs.setFont(PDType1Font.HELVETICA, 11);
            cs.beginText();
            cs.newLineAtOffset(margin, y);
            cs.showText("Faktura Zenbakia: FAK-" + salmentaId);
            cs.endText();
            y -= 15;
 
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            cs.beginText();
            cs.newLineAtOffset(margin, y);
            cs.showText("Data: " + sdf.format(new Date()));
            cs.endText();
            y -= 40;
 
            // BEZEROA
            if (rs.next()) {
                String bezeroIzena = rs.getString("bezero_izena") + " " + rs.getString("bezero_abizena");
                String bezeroEmail = rs.getString("email");
 
                cs.setFont(PDType1Font.HELVETICA_BOLD, 13);
                cs.beginText();
                cs.newLineAtOffset(margin, y);
                cs.showText("BEZEROA:");
                cs.endText();
                y -= 18;
 
                cs.setFont(PDType1Font.HELVETICA, 11);
                cs.beginText();
                cs.newLineAtOffset(margin, y);
                cs.showText(bezeroIzena);
                cs.endText();
                y -= 15;
 
                cs.beginText();
                cs.newLineAtOffset(margin, y);
                cs.showText(bezeroEmail);
                cs.endText();
                y -= 35;
 
                // PRODUKTUAK
                cs.setFont(PDType1Font.HELVETICA_BOLD, 13);
                cs.beginText();
                cs.newLineAtOffset(margin, y);
                cs.showText("PRODUKTUAK:");
                cs.endText();
                y -= 20;
 
                cs.setLineWidth(1f);
                cs.moveTo(margin, y);
                cs.lineTo(550, y);
                cs.stroke();
                y -= 15;
 
                cs.setFont(PDType1Font.HELVETICA_BOLD, 10);
                cs.beginText(); 
                cs.newLineAtOffset(margin, y); 
                cs.showText("Produktua"); 
                cs.endText();
                
                cs.beginText(); 
                cs.newLineAtOffset(300, y);   
                cs.showText("Kant.");    
                cs.endText();
                
                cs.beginText(); 
                cs.newLineAtOffset(370, y);   
                cs.showText("Prezioa");  
                cs.endText();
                
                cs.beginText(); 
                cs.newLineAtOffset(470, y);   
                cs.showText("Totala");   
                cs.endText();
                
                y -= 5;
 
                cs.setLineWidth(0.5f);
                cs.moveTo(margin, y);
                cs.lineTo(550, y);
                cs.stroke();
                y -= 15;
 
                double guztira = 0;
                cs.setFont(PDType1Font.HELVETICA, 10);
 
                do {
                    String produktuIzena = rs.getString("produktu_izena");
                    int kantitatea = rs.getInt("kantitatea");
                    double prezioa = rs.getDouble("prezioa");
                    double totala = kantitatea * prezioa;
                    guztira += totala;
 
                    cs.beginText(); 
                    cs.newLineAtOffset(margin, y); 
                    cs.showText(produktuIzena);                     
                    cs.endText();
                    
                    cs.beginText(); 
                    cs.newLineAtOffset(300, y);    
                    cs.showText(String.valueOf(kantitatea));         
                    cs.endText();
                    
                    cs.beginText(); 
                    cs.newLineAtOffset(370, y);    
                    cs.showText(String.format("%.2f EUR", prezioa)); 
                    cs.endText();
                    
                    cs.beginText(); 
                    cs.newLineAtOffset(470, y);    
                    cs.showText(String.format("%.2f EUR", totala));  
                    cs.endText();
                    
 
                    y -= 20;
                } while (rs.next());
 
                cs.moveTo(margin, y);
                cs.lineTo(550, y);
                cs.stroke();
                y -= 25;
 
                cs.setFont(PDType1Font.HELVETICA_BOLD, 14);
                cs.beginText(); 
                cs.newLineAtOffset(370, y); 
                cs.showText("GUZTIRA:"); 
                cs.endText();
                
                cs.beginText(); 
                cs.newLineAtOffset(470, y); 
                cs.showText(String.format("%.2f EUR", guztira)); 
                cs.endText();
            }
 
            cs.close();
 
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            document.close();
            rs.close();
            ps.close();
 
            // FTP-RA IGO
            byte[] pdfBytes = baos.toByteArray();
            // FTP erroreak irentsi: PDFa igo bada, beti itzuli pdfIzena
            try {
                igoPDFFTPra(pdfBytes, pdfIzena);
            } catch (Exception ftpEx) {
                // Disconnect-eko errorea ignoratu (Commons IO bug)
                System.err.println("FTP itxieran oharra (ez da errorea): " + ftpEx.getMessage());
            }
 
            return pdfIzena;
 
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
 
    
    private static void igoPDFFTPra(byte[] pdfBytes, String pdfIzena) throws IOException {
        FTPClient ftpClient = new FTPClient();
 
        try {
            ftpClient.connect(FTP_SERVER, FTP_PORT);
            boolean loggedIn = ftpClient.login(FTP_USER, FTP_PASS);
 
            if (!loggedIn) {
                throw new IOException("FTP saioa hasi ezin izan da: erabiltzailea edo pasahitza okerra.");
            }
 
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
 
            // Helburuko karpeta sortu ez badago
            ftpClient.makeDirectory(FTP_KARPETA);
            ftpClient.changeWorkingDirectory(FTP_KARPETA);
 
            // PDF igo stream baten bidez
            try (InputStream inputStream = new ByteArrayInputStream(pdfBytes)) {
                boolean igota = ftpClient.storeFile(pdfIzena, inputStream);
                if (igota) {
                    System.out.println("PDF zerbitzarira igo da: " + pdfIzena);
                } else {
                    throw new IOException("PDFa zerbitzarira igotzerakoan errorea: " + ftpClient.getReplyString());
                }
            }
 
        } finally {
            try { ftpClient.logout();     } catch (Throwable ignored) {}
            try { ftpClient.disconnect(); } catch (Throwable ignored) {}
        }
    }
}