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
 
    protected abstract String getTaula();
    protected abstract List<ZutabeakDef> getZutabeak();
 
 
    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(new EmptyBorder(10,10,10,10));
        setContentPane(root);
 
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
 
        formulario = new JPanel(new GridLayout(getZutabeak().size(), 2, 6, 6));
        formulario.setBorder(new EmptyBorder(10,10,10,10));
 
        formulario.setPreferredSize(new Dimension(350, 0));
 
        for (ZutabeakDef z : getZutabeak()) {
 
            JLabel lbl = new JLabel(z.izena + ": ");
            lbl.setFont(new Font("Arial", Font.PLAIN, 13));  
            formulario.add(lbl);
 
            JTextField tf = new JTextField();
            tf.setColumns(100);                              
            tf.setFont(new Font("Arial", Font.PLAIN, 13));   
            tf.setMargin(new Insets(2, 8, 2, 8));
			
 
            if (z.mota == ZutabeakDef.DatuMota.DATA)
                tf.setToolTipText("YYYY-MM-DD");
 
            if (z.gakoNagusia && z.autoGehiketa)
                tf.setEnabled(false);
 
            eremuak.put(z.izena, tf);
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
 
 
    private String[] getZutabeIzenak() {
        List<ZutabeakDef> z = getZutabeak();
        String[] arr = new String[z.size()];
        for (int i=0;i<z.size();i++) arr[i] = z.get(i).izena;
        return arr;
    }
 
 
    // ---------------- SELECT ----------------
    private void kargatu() {
        modeloa.setRowCount(0);
        String sql = "SELECT * FROM " + getTaula();
 
        try (Connection cn = new Conexioa().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
 
            while (rs.next()) {
                Object[] row = new Object[getZutabeak().size()];
                for (int i=0;i<row.length;i++)
                    row[i] = rs.getObject(getZutabeak().get(i).izena);
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
 
            List<ZutabeakDef> ins = new ArrayList<>();
            for (ZutabeakDef z : getZutabeak())
                if (!z.autoGehiketa) ins.add(z);
 
            String cols = String.join(",", ins.stream().map(z -> z.izena).collect(Collectors.toList()));
            String qs = String.join(",", ins.stream().map(z -> "?").collect(Collectors.toList()));
 
            String sql = "INSERT INTO " + getTaula() + " (" + cols + ") VALUES (" + qs + ")";
 
            PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
 
            for (int i=0;i<ins.size();i++)
                ps.setObject(i+1, eremuak.get(ins.get(i).izena).getText().trim());
 
            ps.executeUpdate();
            
            // Eskuratu sortutako ID-a
            ResultSet generatedKeys = ps.getGeneratedKeys();
            int newId = -1;
            if (generatedKeys.next()) {
                newId = generatedKeys.getInt(1);
            }
            generatedKeys.close();
            
            kargatu();
            garbitu(ins);
            
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
 
    }
 
    // ---------------- UPDATE ----------------
    private void eguneratu() {
        int r = taula.getSelectedRow();
        if (r < 0) { mezua("Aukeratu errenkada."); return; }
 
        Object pk = taula.getValueAt(r, 0);
        ZutabeakDef pkDef = getZutabeak().get(0);
 
        try (Connection cn = new Conexioa().getConnection()) {
 
            List<ZutabeakDef> up = new ArrayList<>();
            for (ZutabeakDef z : getZutabeak())
                if (!z.gakoNagusia) up.add(z);
 
            String set = String.join(",", up.stream().map(z -> z.izena + "=?").collect(Collectors.toList()));
            String sql = "UPDATE " + getTaula() + " SET " + set + " WHERE " + pkDef.izena + "=?";
 
            PreparedStatement ps = cn.prepareStatement(sql);
 
            for (int i=0;i<up.size();i++)
                ps.setObject(i+1, eremuak.get(up.get(i).izena).getText().trim());
 
            ps.setObject(up.size()+1, pk);
 
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
        ZutabeakDef pkDef = getZutabeak().get(0);
 
        try (Connection cn = new Conexioa().getConnection()) {
 
            PreparedStatement ps = cn.prepareStatement(
                "DELETE FROM " + getTaula() + " WHERE " + pkDef.izena + "=?"
            );
            ps.setObject(1, pk);
            ps.executeUpdate();
 
            kargatu();
            garbitu(getZutabeak());
 
        } catch (Exception ex) {
            ex.printStackTrace();
            mezua("Errorea ezabatzean.");
        }
    }
 
    private void taulatikFormera() {
        int r = taula.getSelectedRow();
        if (r < 0) return;
 
        for (int i=0;i<getZutabeak().size();i++) {
            String col = getZutabeak().get(i).izena;
            Object val = taula.getValueAt(r, i);
            eremuak.get(col).setText(val == null ? "" : val.toString());
        }
    }
 
    private void garbitu(List<ZutabeakDef> cols) {
        for (ZutabeakDef z : cols)
            eremuak.get(z.izena).setText("");
    }
 
    private void mezua(String m) {
        JOptionPane.showMessageDialog(this, m);
    }
}
 