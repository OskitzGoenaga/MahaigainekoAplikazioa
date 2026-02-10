package Faktura;
 
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
 
import Bistak.Conexioa;
 
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
 
public class SortuFaktura {
 
    private static final String FAKTURA_KARPETA_LOKALA = "fakturak/";
    private static final String ZERBITZARI_KARPETA = "C:/xampp/htdocs/WEB/fakturak/";
    
    // Faktura PDF bat sortzen du
    
    public static String sortuFaktura(int salmentaId) {
        
        // Karpeta lokala sortu ez badago
        File karpelaLokala = new File(FAKTURA_KARPETA_LOKALA);
        if (!karpelaLokala.exists()) {
            karpelaLokala.mkdirs();
        }
        
        // PDF fitxategiaren izena
        String pdfIzena = "faktura_" + salmentaId + ".pdf";
        String pdfPathLokala = FAKTURA_KARPETA_LOKALA + pdfIzena;
        
        try (Connection cn = new Conexioa().getConnection()) {
            if (cn == null) {
                System.err.println("Ezin izan da datu-basera konektatu");
                return null;
            }
            
            // Kontsulta - ALDATU TAULA IZENA "saskia" → "saskiak" zure datu-basearen arabera
            String query = "SELECT s.id, s.kantitatea, s.data, " +
                          "b.izena as bezero_izena, b.abizena as bezero_abizena, b.email, " +
                          "p.izena as produktu_izena, p.prezioa " +
                          "FROM saskiak s " +  // ← Egiaztatu taula izena: "saskia" edo "saskiak"
                          "JOIN bezeroak b ON s.bezeroa_id = b.id " +
                          "JOIN produktuak p ON s.produktua_id = p.id " +
                          "WHERE s.salmenta_id = ?";
            
            PreparedStatement ps = cn.prepareStatement(query);
            ps.setInt(1, salmentaId);
            ResultSet rs = ps.executeQuery();
            
            // PDF dokumentua sortu
            PDDocument document = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            
            PDPageContentStream cs = new PDPageContentStream(document, page);
            
            float y = 750; // Hasierako Y posizioa
            float margin = 50;
            
            // === GOIBURUA ===
            cs.setFont(PDType1Font.HELVETICA_BOLD, 24);
            cs.beginText();
            cs.newLineAtOffset(margin, y);
            cs.showText("FAKTURA");
            cs.endText();
            y -= 40;
            
            // Faktura Info
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
            
            // === BEZEROA ===
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
                
                // === PRODUKTUAK ===
                cs.setFont(PDType1Font.HELVETICA_BOLD, 13);
                cs.beginText();
                cs.newLineAtOffset(margin, y);
                cs.showText("PRODUKTUAK:");
                cs.endText();
                y -= 20;
                
                // Taula goiburua
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
                
                // Produktuak
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
                
                // Lerroa
                cs.moveTo(margin, y);
                cs.lineTo(550, y);
                cs.stroke();
                y -= 25;
                
                // === TOTALA ===
                cs.setFont(PDType1Font.HELVETICA_BOLD, 14);
                cs.beginText();
                cs.newLineAtOffset(370, y);
                cs.showText("GUZTIRA:");
                cs.endText();
                
                cs.beginText();
                cs.newLineAtOffset(470, y);
                cs.showText(String.format("%.2f EUR", guztira));
                cs.endText();
                y -= 50;
                
            }
            
            cs.close();
            
            // Gorde lokala
            document.save(pdfPathLokala);
            document.close();
            
            rs.close();
            ps.close();
                        
            // ZERBITZARIRA KOPIATU
            kopiatuZerbitzarira(pdfPathLokala, pdfIzena);
            
            return pdfIzena; // Fitxategi izena itzuli (faktura_X.pdf)
            
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * PDF-a zerbitzarira kopiatu
     */
    private static void kopiatuZerbitzarira(String pdfPathLokala, String pdfIzena) {
        try {
            // Zerbitzariko karpeta sortu ez badago
            File zerbitzariKarpeta = new File(ZERBITZARI_KARPETA);
            if (!zerbitzariKarpeta.exists()) {
                zerbitzariKarpeta.mkdirs();
            }
            
            String helmugaPath = ZERBITZARI_KARPETA + pdfIzena;
            
            // Kopiatu fitxategia
            java.nio.file.Files.copy(
                new File(pdfPathLokala).toPath(),
                new File(helmugaPath).toPath(),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
                        
        } catch (Exception e) {
            System.err.println("Errorea zerbitzarira kopiatzean: " + e.getMessage());
            e.printStackTrace();
        }
    }
}