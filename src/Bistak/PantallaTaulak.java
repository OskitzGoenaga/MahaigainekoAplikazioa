package Bistak;

import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PantallaTaulak extends JFrame {

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
    private JButton btnAtzera;

    // Konstruktorea rolarekin
    public PantallaTaulak(String rol) {
        this.rol = rol;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);

        initComponents();

        // Botoiak ezkutatzeko rolaren arabera.
        if ("langilea".equals(rol)) {
            btnHornitzaileak.setVisible(false);
            btnLangileak.setVisible(false);
        } else if (!"langilea".equals(rol)) {
        	JOptionPane.showMessageDialog(this, "Rol ezezaguna "+rol);
            return;
        }
    }

    // Kontsuktore hutsa
    public PantallaTaulak() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        initComponents();
    }

    // Konponenteak inizializatzen dituen metodoa.
    private void initComponents() {
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5,5,5,5));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        //Bezeroak botoia 
        btnBezeroak = new JButton("Bezeroak");
        btnBezeroak.setBounds(47, 31, 91, 45);
        contentPane.add(btnBezeroak);

        //Erosketak botoia
        btnErosketak = new JButton("Erosketak");
        btnErosketak.setBounds(172, 31, 91, 45);
        contentPane.add(btnErosketak);

        //Hornitzaileak botoia
        btnHornitzaileak = new JButton("Hornitzaileak");
        btnHornitzaileak.setBounds(301, 31, 91, 45);
        contentPane.add(btnHornitzaileak);

        //Langileak botoia
        btnLangileak = new JButton("Langileak");
        btnLangileak.setBounds(47, 107, 91, 45);
        contentPane.add(btnLangileak);

        //Produktuak botoia
        btnProduktuak = new JButton("Produktuak");
        btnProduktuak.setBounds(172, 107, 91, 45);
        contentPane.add(btnProduktuak);

        //Salmenta botoia
        btnSalmentak = new JButton("Salmentak");
        btnSalmentak.setBounds(301, 107, 91, 45);
        contentPane.add(btnSalmentak);

        //Soporteak botoia
        btnSoporteak = new JButton("Soporteak");
        btnSoporteak.setBounds(47, 178, 91, 45);
        contentPane.add(btnSoporteak);
        btnSoporteak.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PantallaKonsultak pantalla = new PantallaKonsultak();
                pantalla.setVisible(true);
                dispose();
            }
        });

        // Atzera botoia
        btnAtzera = new JButton("Atzera");
        btnAtzera.setBounds(172, 178, 91, 45);
        contentPane.add(btnAtzera);
        btnAtzera.addActionListener(e -> {
            dispose(); // lehio ixteko.
        });
    }

    // Main-a lehio probatzeko.
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                PantallaTaulak frame = new PantallaTaulak("langilea"); // proba
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
