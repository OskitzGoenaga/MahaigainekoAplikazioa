package Bistak;
 
import java.awt.EventQueue;
import java.awt.GridLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
 
public class PantailaTaulak extends JFrame {
 
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private String rol;
 
    // Botoiak atributu bezala gero rolarekin ezkutatzeko.
    private JButton btnBezeroak;
    private JButton btnErosketak;
    private JButton btnHornitzaileak;
    private JButton btnLangileak;
    private JButton btnProduktuak;
    private JButton btnSalmentak;
    private JButton btnSoporteak;
 
    // Konstruktorea rolarekin
    public PantailaTaulak(String rol) {
        this.rol = rol;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 500, 350);
 
        initComponents();
        aplicarPermisos();
    }
 
    // Kontsuktore hutsa
    public PantailaTaulak() {
        this("langilea"); // default moduan proba
    }
 
    // Konponenteak inizializatzen dituen metodoa.
    private void initComponents() {
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10,10,10,10));
        // 3 zutabe, errenkada automatikoak; 10px tarte horizontala eta bertikala
        contentPane.setLayout(new GridLayout(0, 3, 10, 10));
        setContentPane(contentPane);
 
        // Bezeroak botoia
        btnBezeroak = new JButton("Bezeroak");
        contentPane.add(btnBezeroak);
 
        // Erosketak botoia
        btnErosketak = new JButton("Erosketak");
        contentPane.add(btnErosketak);
 
        // Hornitzaileak botoia
        btnHornitzaileak = new JButton("Hornitzaileak");
        contentPane.add(btnHornitzaileak);
 
        // Langileak botoia
        btnLangileak = new JButton("Langileak");
        contentPane.add(btnLangileak);
 
        // Produktuak botoia
        btnProduktuak = new JButton("Produktuak");
        contentPane.add(btnProduktuak);
 
        // Salmentak botoia
        btnSalmentak = new JButton("Salmentak");
        contentPane.add(btnSalmentak);
 
        // Soporteak botoia
        btnSoporteak = new JButton("Soporteak");
        contentPane.add(btnSoporteak);
        btnSoporteak.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	PantailaKontsultak pantalla = new PantailaKontsultak();
                pantalla.setVisible(true);
                dispose();
            }
        });
    }
 
    // Rolaren arabera botoiak erakutsi/kendu (kendu = ez du hutsunerik uzten)
    private void aplicarPermisos() {
        // Adibidea: "langilea" ez da admin; kendu bi botoi
        if ("langilea".equalsIgnoreCase(rol)) {
            removeIfPresent(btnHornitzaileak);
            removeIfPresent(btnLangileak);
        } else {
            // Bestelako rolak (admin, kudeatzaile, etab.) guztia ikus dezakete (adib.)
            JOptionPane.showMessageDialog(this, "Rol: " + rol);
        }
        contentPane.revalidate();
        contentPane.repaint();
    }
 
    // Laguntzailea: botoia baldin badago panelaren barruan, kendu
    private void removeIfPresent(JButton btn) {
        if (btn.getParent() == contentPane) {
            contentPane.remove(btn);
        }
    }
 
    // Main-a lehio probatzeko.
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                PantailaTaulak frame = new PantailaTaulak("langilea"); // proba
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}