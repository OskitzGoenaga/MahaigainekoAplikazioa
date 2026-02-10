package Faktura;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import Bistak.Conexioa;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

public class SortuFaktura {

    private static final String FTP_HOST = "192.168.115.163";
    private static final int FTP_PUERTO = 21;
    private static final String FTP_USUARIO = "Erronka";
    private static final String FTP_PASSWORD = "1234";
    private static final String FTP_KARPETA = "/WEB/fakturak/";

    public static String sortuFaktura(int salmentaId) {

        String pdfIzena = "faktura_" + salmentaId + ".pdf";

        try (Connection cn = new Conexioa().getConnection()) {

            String query = "SELECT s.id, s.kantitatea, s.data, " +
                    "b.izena AS bezero_izena, b.abizena AS bezero_abizena, b.email, " +
                    "p.izena AS produktu_izena, p.prezioa " +
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

            if (rs.next()) {

                String bezeroIzena = rs.getString("bezero_izena") + " " + rs.getString("bezero_abizena");
                String email = rs.getString("email");

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
                cs.showText(email);
                cs.endText();
                y -= 35;

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

                cs.beginText(); cs.newLineAtOffset(margin, y); cs.showText("Produktua"); cs.endText();
                cs.beginText(); cs.newLineAtOffset(300, y); cs.showText("Kant."); cs.endText();
                cs.beginText(); cs.newLineAtOffset(370, y); cs.showText("Prezioa"); cs.endText();
                cs.beginText(); cs.newLineAtOffset(470, y); cs.showText("Totala"); cs.endText();

                y -= 5;
                cs.moveTo(margin, y);
                cs.lineTo(550, y);
                cs.stroke();
                y -= 15;

                double guztira = 0;
                cs.setFont(PDType1Font.HELVETICA, 10);

                do {
                    String produktu = rs.getString("produktu_izena");
                    int kant = rs.getInt("kantitatea");
                    double prezio = rs.getDouble("prezioa");
                    double total = kant * prezio;
                    guztira += total;

                    cs.beginText(); cs.newLineAtOffset(margin, y); cs.showText(produktu); cs.endText();
                    cs.beginText(); cs.newLineAtOffset(300, y); cs.showText(String.valueOf(kant)); cs.endText();
                    cs.beginText(); cs.newLineAtOffset(370, y); cs.showText(String.format("%.2f EUR", prezio)); cs.endText();
                    cs.beginText(); cs.newLineAtOffset(470, y); cs.showText(String.format("%.2f EUR", total)); cs.endText();

                    y -= 20;

                } while (rs.next());

                cs.moveTo(margin, y);
                cs.lineTo(550, y);
                cs.stroke();
                y -= 25;

                cs.setFont(PDType1Font.HELVETICA_BOLD, 14);
                cs.beginText(); cs.newLineAtOffset(370, y); cs.showText("GUZTIRA:"); cs.endText();
                cs.beginText(); cs.newLineAtOffset(470, y); cs.showText(String.format("%.2f EUR", guztira)); cs.endText();
            }

            cs.close();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            document.close();

            byte[] pdfBytes = baos.toByteArray();

            uploadPDFviaFTP(pdfBytes, pdfIzena);

            return pdfIzena;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    
    private static void uploadPDFviaFTP(byte[] pdfBytes, String remoteName) {

        FTPClient ftp = new FTPClient();

        try {
            ftp.connect(FTP_HOST, FTP_PUERTO);
            ftp.login(FTP_USUARIO, FTP_PASSWORD);
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            InputStream is = new ByteArrayInputStream(pdfBytes);

            boolean ok = ftp.storeFile(FTP_KARPETA + remoteName, is);
            is.close();

            if (ok) {
                System.out.println("✔ Faktura igota");
            } else {
                System.out.println("❌ Errorea FTP igoeran");
            }

            ftp.logout();
            ftp.disconnect();

        } catch (Exception e) {
            System.err.println("FTP errorea: " + e.getMessage());
        }
    }
}