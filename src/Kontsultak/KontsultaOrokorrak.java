package Kontsultak;

import Bistak.Conexioa;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public abstract class KontsultaOrokorrak extends JFrame {

    protected JTable taula;
    protected DefaultTableModel modeloa;

    protected JPanel formulario;
    protected final Map<String, JTextField> eremuak = new LinkedHashMap<>();

    protected JButton btnKargatu, btnTxertatu, btnEguneratu, btnEzabatu;

    private boolean pInsert = true, pUpdate = true, pDelete = true;

    public KontsultaOrokorrak(String izenburua) {
        setTitle(izenburua);
        setSize(900, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
        kargatu();
    }

    public KontsultaOrokorrak setBaimenak(boolean canInsert, boolean canUpdate, boolean canDelete) {
        this.pInsert = canInsert;
        this.pUpdate = canUpdate;
        this.pDelete = canDelete;

        btnTxertatu.setEnabled(canInsert);
        btnEguneratu.setEnabled(canUpdate);
        btnEzabatu.setEnabled(canDelete);

        return this;
    }

    // ALDAKETA: ZutabeakDef ordez String array erabiltzen dugu
    protected abstract String getTaula();
    protected abstract String[] getZutabeIzenak();


    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(new EmptyBorder(10,10,10,10));
        setContentPane(root);

        // ALDAKETA: Zuzenean getZutabeIzenak() deitzen dugu
        modeloa = new DefaultTableModel(getZutabeIzenak(), 0) {
            @Override public boolean isCellEditable(int r,int c){ return false; }
        };
        taula = new JTable(modeloa);
        root.add(new JScrollPane(taula), BorderLayout.CENTER);

        JPanel botoiak = new JPanel();
        btnKargatu   = new JButton("Kargatu");
        btnTxertatu  = new JButton("Txertatu");
        btnEguneratu = new JButton("Eguneratu");
        btnEzabatu   = new JButton("Ezabatu");

        botoiak.add(btnKargatu);
        botoiak.add(btnTxertatu);
        botoiak.add(btnEguneratu);
        botoiak.add(btnEzabatu);

        root.add(botoiak, BorderLayout.NORTH);

        // ALDAKETA: getZutabeIzenak().length erabiltzen dugu
        formulario = new JPanel(new GridLayout(getZutabeIzenak().length, 2, 6, 6));
        formulario.setBorder(new EmptyBorder(10,10,10,10));

        formulario.setPreferredSize(new Dimension(350, 0)); 

        // ALDAKETA: String array-tik formularioa sortzen dugu
        for (String zutabeIzena : getZutabeIzenak()) {

            JLabel lbl = new JLabel(zutabeIzena + ": ");
            lbl.setFont(new Font("Arial", Font.PLAIN, 13));  
            formulario.add(lbl);

            JTextField tf = new JTextField();
            tf.setColumns(100);                              
            tf.setFont(new Font("Arial", Font.PLAIN, 13));   
            tf.setMargin(new Insets(2, 8, 2, 8));

            // ALDAKETA: "id" izena duten eremuak desgaitu (auto-increment)
            if (zutabeIzena.equalsIgnoreCase("id")) {
                tf.setEnabled(false);
            }
            
            // Zutabe izenak "data" edo "fecha" badira, tooltip jarri
            if (zutabeIzena.toLowerCase().contains("data") || 
                zutabeIzena.toLowerCase().contains("fecha")) {
                tf.setToolTipText("YYYY-MM-DD");
            }

            eremuak.put(zutabeIzena, tf);
            formulario.add(tf);
        }

        root.add(formulario, BorderLayout.EAST);

        btnKargatu.addActionListener(e -> kargatu());
        btnTxertatu.addActionListener(e -> { if (pInsert) txertatu(); else mezua("Ez daukazu txertatzeko baimenik."); });
        btnEguneratu.addActionListener(e -> { if (pUpdate) eguneratu(); else mezua("Ez daukazu eguneratzeko baimenik."); });
        btnEzabatu.addActionListener(e -> { if (pDelete) ezabatu(); else mezua("Ez daukazu ezabatzeko baimenik."); });

        taula.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) taulatikFormera();
        });
    }


    // ---------------- SELECT ----------------
    private void kargatu() {
        modeloa.setRowCount(0);
        String sql = "SELECT * FROM " + getTaula();

        try (Connection cn = new Conexioa().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // ALDAKETA: Zuzenean getZutabeIzenak() erabiltzen dugu
            while (rs.next()) {
                String[] zutabeak = getZutabeIzenak();
                Object[] row = new Object[zutabeak.length];
                for (int i=0; i<row.length; i++)
                    row[i] = rs.getObject(zutabeak[i]);
                modeloa.addRow(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            mezua("Errorea datuak kargatzean.");
        }
    }

    // ---------------- INSERT ----------------
    private void txertatu() {
        try (Connection cn = new Conexioa().getConnection()) {

            // ALDAKETA: "id" zutabea ez txertatu (auto-increment delako)
            String[] guztiak = getZutabeIzenak();
            List<String> txertatukoZutabeak = new ArrayList<>();
            
            for (String zutabea : guztiak) {
                if (!zutabea.equalsIgnoreCase("id")) {
                    txertatukoZutabeak.add(zutabea);
                }
            }

            String cols = String.join(",", txertatukoZutabeak);
            String qs = String.join(",", 
                txertatukoZutabeak.stream().map(z -> "?").collect(Collectors.toList()));

            String sql = "INSERT INTO " + getTaula() + " (" + cols + ") VALUES (" + qs + ")";

            PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            for (int i=0; i<txertatukoZutabeak.size(); i++)
                ps.setObject(i+1, eremuak.get(txertatukoZutabeak.get(i)).getText().trim());

            ps.executeUpdate();
            
            // Eskuratu sortutako ID-a
            ResultSet generatedKeys = ps.getGeneratedKeys();
            int newId = -1;
            if (generatedKeys.next()) {
                newId = generatedKeys.getInt(1);
            }
            generatedKeys.close();
            
            kargatu();
            garbitu(txertatukoZutabeak);
            
            // Hook metodoa deituz txertatu ondoren
            if (newId != -1) {
                ondorenTxertatu(newId);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            mezua("Errorea txertatzean.");
        }
    }
    
    protected void ondorenTxertatu(int id) {
        // Subklaseek gainidatzi dezakete behar badute
    }

    // ---------------- UPDATE ----------------
    private void eguneratu() {
        int r = taula.getSelectedRow();
        if (r < 0) { mezua("Aukeratu errenkada."); return; }

        Object pk = taula.getValueAt(r, 0);
        String pkIzena = getZutabeIzenak()[0]; // Lehenengo zutabea da primary key-a

        try (Connection cn = new Conexioa().getConnection()) {

            // ALDAKETA: Primary key-a ez eguneratu (lehenengo zutabea)
            String[] guztiak = getZutabeIzenak();
            List<String> eguneratukoZutabeak = new ArrayList<>();
            
            for (int i=1; i<guztiak.length; i++) { // i=1 hasita (id saltatu)
                eguneratukoZutabeak.add(guztiak[i]);
            }

            String set = String.join(",", 
                eguneratukoZutabeak.stream().map(z -> z + "=?").collect(Collectors.toList()));
            String sql = "UPDATE " + getTaula() + " SET " + set + " WHERE " + pkIzena + "=?";

            PreparedStatement ps = cn.prepareStatement(sql);

            for (int i=0; i<eguneratukoZutabeak.size(); i++)
                ps.setObject(i+1, eremuak.get(eguneratukoZutabeak.get(i)).getText().trim());

            ps.setObject(eguneratukoZutabeak.size()+1, pk);

            ps.executeUpdate();
            kargatu();

        } catch (Exception ex) {
            ex.printStackTrace();
            mezua("Errorea eguneratzean.");
        }
    }

    // ---------------- DELETE ----------------
    private void ezabatu() {
        int r = taula.getSelectedRow();
        if (r < 0) { mezua("Aukeratu errenkada."); return; }

        Object pk = taula.getValueAt(r, 0);
        String pkIzena = getZutabeIzenak()[0]; // Lehenengo zutabea da primary key-a

        try (Connection cn = new Conexioa().getConnection()) {

            PreparedStatement ps = cn.prepareStatement(
                "DELETE FROM " + getTaula() + " WHERE " + pkIzena + "=?"
            );
            ps.setObject(1, pk);
            ps.executeUpdate();

            kargatu();
            garbitu(Arrays.asList(getZutabeIzenak()));

        } catch (Exception ex) {
            ex.printStackTrace();
            mezua("Errorea ezabatzean.");
        }
    }

    private void taulatikFormera() {
        int r = taula.getSelectedRow();
        if (r < 0) return;

        String[] zutabeak = getZutabeIzenak();
        for (int i=0; i<zutabeak.length; i++) {
            String col = zutabeak[i];
            Object val = taula.getValueAt(r, i);
            eremuak.get(col).setText(val == null ? "" : val.toString());
        }
    }

    private void garbitu(List<String> cols) {
        for (String z : cols)
            eremuak.get(z).setText("");
    }

    private void mezua(String m) {
        JOptionPane.showMessageDialog(this, m);
    }
}
