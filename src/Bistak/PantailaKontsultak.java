package Bistak;

import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.*;
 
public class PantailaKontsultak extends JFrame {
 
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable table;
 
    private JButton btnKontsulta; // SELECT
    private JButton btnEzabatu;   // DELETE
 
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
            	PantailaKontsultak frame = new PantailaKontsultak();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
 
    public PantailaKontsultak() {
        setTitle("SOPORTEA");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setBounds(100, 100, 760, 480);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(8, 8, 8, 8));
        setContentPane(contentPane);
        contentPane.setLayout(null);
 
        btnKontsulta = new JButton("Kontsulta");
        btnKontsulta.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btnKontsulta.setBounds(20, 20, 120, 40);
        contentPane.add(btnKontsulta);
 

 
        btnEzabatu = new JButton("Ezabatu");
        btnEzabatu.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btnEzabatu.setBounds(20, 120, 120, 40);
        contentPane.add(btnEzabatu);
 
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(160, 20, 560, 400);
        contentPane.add(scrollPane);
 
        table = new JTable();
        table.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {"Id", "Arazoa", "Bezeroaren izena", "Langilearen izena"}
        ) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int col) { return false; }
        });
        scrollPane.setViewportView(table);
 
        // ---- Ekintzak ----
        btnKontsulta.addActionListener(this::kargatuSelekta); // SELECT     
        btnEzabatu.addActionListener(this::ezabatuHautatua);      // DELETE
    }
 
    // ============= SELECT =============
    private void kargatuSelekta(ActionEvent e) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // taula garbitu
 
        String sql = "SELECT a.id, a.arazoa, b.izena, l.izena FROM arazoak a INNER JOIN bezeroak b ON a.bezeroa_id = b.id INNER JOIN langileak l ON a.langilea_id = l.id ORDER BY a.id ASC";
        Conexioa kon=new Conexioa();//Konexioa berri bat sortu.
        try (Connection conexion= kon.getConnection();
             Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
 
            while (rs.next()) {
                Object[] row = new Object[] {
                    rs.getInt("id"),
                    rs.getString("arazoa"),
                    rs.getString("b.izena"),
                    rs.getString("l.izena") // edo rs.getDouble("nota")
                };
                model.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Errorea datuak kargatzean:\n" + ex.getMessage(),
                "Errorea", JOptionPane.ERROR_MESSAGE);
        }
    }
 
 
    // ============= DELETE =============
    private void ezabatuHautatua(ActionEvent e) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Aukeratu errenkada bat ezabatzeko.");
            return;
        }
 
        int id = (int) table.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Ziur zaude ezabatu nahi duzula ID = " + id + "?",
            "Berretsi ezabaketa",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;
 
        String sql = "DELETE FROM arazoak WHERE id = ?";
 
        Conexioa kon=new Conexioa();
        try (Connection conexion= kon.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
 
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Ezabatuta (" + rows + ").");
 
            // Taula berriro kargatu aldaketa ikusteko
            kargatuSelekta(null);
 
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Errorea ezabatzean:\n" + ex.getMessage(),
                "Errorea", JOptionPane.ERROR_MESSAGE);
        }
    }
}