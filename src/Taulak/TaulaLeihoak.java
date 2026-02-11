package Taulak;

import Kontsultak.*;
import javax.swing.*;
import java.awt.*;

public class TaulaLeihoak {

    // ------------------ BEZEROAK ------------------
    public static class Bezeroak extends KontsultaOrokorrak {
        public Bezeroak() { super("Bezeroak"); }
        
        @Override 
        protected String getTaula() { 
            return "bezeroak"; 
        }
        
        @Override 
        protected String[] getZutabeIzenak() {
            return new String[]{"id", "izena", "abizena", "email", "telefonoa", "helbidea"};
        }
    }

    // ------------------ EROSKETAK ------------------
    public static class Erosketak extends KontsultaOrokorrak {
        public Erosketak() { super("Erosketak"); }
        
        @Override 
        protected String getTaula() { 
            return "erosketak"; 
        }
        
        @Override 
        protected String[] getZutabeIzenak() {
            return new String[]{"id", "data", "kantitatea", "hornitzailea_id", "produktua_id"};
        }
    }

    // ------------------ HORNTZAILEAK ------------------
    public static class Hornitzaileak extends KontsultaOrokorrak {
        public Hornitzaileak() { super("Hornitzaileak"); }
        
        @Override 
        protected String getTaula() { 
            return "hornitzaileak"; 
        }
        
        @Override 
        protected String[] getZutabeIzenak() {
            return new String[]{"id", "enpresa", "telefonoa", "email"};
        }
    }

    // ------------------ LANGILEAK ------------------
    public static class Langileak extends KontsultaOrokorrak {
        public Langileak() { super("Langileak"); }
        
        @Override 
        protected String getTaula() { 
            return "langileak"; 
        }
        
        @Override 
        protected String[] getZutabeIzenak() {
            return new String[]{"id", "izena", "abizena", "rol", "nan", "email", "pasahitza"};
        }
    }

    // ------------------ PRODUKTUAK ------------------
    public static class Produktuak extends KontsultaOrokorrak {
        public Produktuak() { super("Produktuak"); }
        
        @Override 
        protected String getTaula() { 
            return "produktuak"; 
        }
        
        @Override 
        protected String[] getZutabeIzenak() {
            return new String[]{"id", "mota", "izena", "prezioa", "stock", "argazkia", "hornitzaile_id"};
        }
    }

    // ------------------ SALMENTAK ------------------
    public static class Salmentak extends KontsultaOrokorrak {
        
        private JButton btnSortuPDF;
        
        public Salmentak() { 
            super("Salmentak"); 
            gehituPDFBotoia();
        }
        
        @Override 
        protected String getTaula() { 
            return "salmentak"; 
        }
        
        @Override 
        protected String[] getZutabeIzenak() {
            return new String[]{"id", "faktura_path"};
        }
        
        // Botoi berria gehitzen du PDF-a sortzeko
        private void gehituPDFBotoia() {
            // Eskuratu botoien panela
            Container contentPane = getContentPane();
            Component[] components = contentPane.getComponents();
            
            JPanel botoiPanel = null;
            for (Component comp : components) {
                if (comp instanceof JPanel) {
                    JPanel panel = (JPanel) comp;
                    if (panel.getComponents().length > 0 && 
                        panel.getComponent(0) instanceof JButton) {
                        botoiPanel = panel;
                        break;
                    }
                }
            }
            
            if (botoiPanel != null) {
                // Sortu PDF botoia
                btnSortuPDF = new JButton("Sortu PDF");
                btnSortuPDF.setFont(new Font("Arial", Font.BOLD, 12));
                btnSortuPDF.setFocusPainted(false);
                
                btnSortuPDF.addActionListener(e -> sortuPDFBotoia());
                
                botoiPanel.add(btnSortuPDF);
                botoiPanel.revalidate();
                botoiPanel.repaint();
            }
        }
        
        // PDF Sortu botoiari klik egitean
        private void sortuPDFBotoia() {
            int row = taula.getSelectedRow();
            
            if (row < 0) {
                JOptionPane.showMessageDialog(this, 
                    "Mesedez, aukeratu salmenta bat taulan.", 
                    "Oharra", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Salmenta ID-a eskuratu
            Object idObj = taula.getValueAt(row, 0);
            if (idObj == null) {
                JOptionPane.showMessageDialog(this, 
                    "Errorea: Salmenta ID-a ez da aurkitu.", 
                    "Errorea", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int salmentaId = Integer.parseInt(idObj.toString());
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Salmenta #" + salmentaId + "rako PDF faktura sortu nahi duzu?",
                "Berrespena",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
            
            sortuPDF(salmentaId);
        }
        
        // PDF faktura sortzen du
        private void sortuPDF(int salmentaId) {
            try {
                String pdfPath = Faktura.SortuFaktura.sortuFaktura(salmentaId);
                
                if (pdfPath != null) {
                    // Eguneratu salmentak taula PDF path-arekin
                    try (java.sql.Connection cn = new Bistak.Conexioa().getConnection()) {
                        java.sql.PreparedStatement ps = cn.prepareStatement(
                            "UPDATE salmentak SET faktura_path = ? WHERE id = ?"
                        );
                        ps.setString(1, pdfPath);
                        ps.setInt(2, salmentaId);
                        ps.executeUpdate();
                        ps.close();
                        
                        int selectedRow = taula.getSelectedRow();
                        Component root = SwingUtilities.getRoot(this);
                        if (root instanceof JFrame) {
                            JFrame frame = (JFrame) root;
                            if (btnKargatu != null) {
                                btnKargatu.doClick();
                            }
                        }
                        
                        if (selectedRow >= 0 && selectedRow < taula.getRowCount()) {
                            taula.setRowSelectionInterval(selectedRow, selectedRow);
                        }
                        
                        JOptionPane.showMessageDialog(this, 
                            "✓ Faktura PDF sortuta!\n\n" +
                            "Fitxategia: " + pdfPath + "\n" +
                            "Salmenta ID: " + salmentaId, 
                            "Arrakasta", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, 
                            "Errorea PDF path-a gordetzean:\n" + e.getMessage(), 
                            "Errorea", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Errorea faktura PDF-a sortzerakoan.\n" +
                        "Egiaztatu kontsola errore mezuak ikusteko.", 
                        "Errorea", 
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Errorea PDF sortzerakoan:\n" + e.getMessage(), 
                    "Errorea", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ------------------ SASKIA ------------------
    public static class Saskia extends KontsultaOrokorrak {
        public Saskia() { super("Saskia"); }
        
        @Override 
        protected String getTaula() { 
            return "saskia"; 
        }
        
        @Override 
        protected String[] getZutabeIzenak() {
            return new String[]{"id", "kantitatea", "data", "bezeroa_id", "produktua_id", "salmenta_id"};
        }
    }

    // ------------------ ARAZOAK / SOPORTEA ------------------
    public static class Arazoak extends KontsultaOrokorrak {
        public Arazoak() { super("Arazoak (Soportea)"); }
        
        @Override 
        protected String getTaula() { 
            return "arazoak"; 
        }
        
        @Override 
        protected String[] getZutabeIzenak() {
            return new String[]{"id", "arazoa", "bezeroa_id", "langilea_id"};
        }
    }
}
