package Bistak;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

import Taulak.*;

public class PantailaTaulak extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private final String rola;

    // Botoiak
    private JButton btnBezeroak, btnErosketak, btnHornitzaileak, btnLangileak,
                    btnProduktuak, btnSalmentak, btnSaskiak, btnArazoak;

    public PantailaTaulak(String rola) {
        this.rola = rola;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 720, 420);
        initComponents();
        aplikatuBaimenak();
    }

    public PantailaTaulak() {
        this("langilea");
    }

    private void initComponents() {
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10,10,10,10));
        contentPane.setLayout(new GridLayout(0, 3, 10, 10));
        setContentPane(contentPane);

        btnBezeroak      = new JButton("Bezeroak");       btnBezeroak.addActionListener(this::irikiBezeroak);
        btnErosketak     = new JButton("Erosketak");      btnErosketak.addActionListener(this::irikiErosketak);
        btnHornitzaileak = new JButton("Hornitzaileak");  btnHornitzaileak.addActionListener(this::irikiHornitzaileak);
        btnLangileak     = new JButton("Langileak");      btnLangileak.addActionListener(this::irikiLangileak);
        btnProduktuak    = new JButton("Produktuak");     btnProduktuak.addActionListener(this::irikiProduktuak);
        btnSalmentak     = new JButton("Salmentak");      btnSalmentak.addActionListener(this::irikiSalmentak);
        btnSaskiak       = new JButton("Saskiak");        btnSaskiak.addActionListener(this::irikiSaskiak);
        btnArazoak      = new JButton("Arazoak");       btnArazoak.addActionListener(this::irikiArazoak);

        contentPane.add(btnBezeroak);
        contentPane.add(btnErosketak);
        contentPane.add(btnHornitzaileak);
        contentPane.add(btnLangileak);
        contentPane.add(btnProduktuak);
        contentPane.add(btnSalmentak);
        contentPane.add(btnSaskiak);
        contentPane.add(btnArazoak);
    }

    private void kendu(JButton b) {
        if (b.getParent() == contentPane) contentPane.remove(b);
    }

    // Rolen arabera botoiak ezkutatu/erakutsi
    private void aplikatuBaimenak() {
        if ("langilea".equalsIgnoreCase(rola)) {
            kendu(btnHornitzaileak);
            kendu(btnLangileak);
        }
        contentPane.revalidate();
        contentPane.repaint();
    }


    private void irikiBezeroak(ActionEvent e) {
        new TaulaLeihoak.Bezeroak()
                .setBaimenak(perIns("bezeroak"), perUpd("bezeroak"), perDel("bezeroak"))
                .setVisible(true);
    }

    private void irikiErosketak(ActionEvent e) {
        new TaulaLeihoak.Erosketak()
                .setBaimenak(perIns("erosketak"), perUpd("erosketak"), perDel("erosketak"))
                .setVisible(true);
    }

    private void irikiHornitzaileak(ActionEvent e) {
        new TaulaLeihoak.Hornitzaileak()
                .setBaimenak(perIns("hornitzaileak"), perUpd("hornitzaileak"), perDel("hornitzaileak"))
                .setVisible(true);
    }

    private void irikiLangileak(ActionEvent e) {
        new TaulaLeihoak.Langileak()
                .setBaimenak(perIns("langileak"), perUpd("langileak"), perDel("langileak"))
                .setVisible(true);
    }

    private void irikiProduktuak(ActionEvent e) {
        new TaulaLeihoak.Produktuak()
                .setBaimenak(perIns("produktuak"), perUpd("produktuak"), perDel("produktuak"))
                .setVisible(true);
    }

    private void irikiSalmentak(ActionEvent e) {
        new TaulaLeihoak.Salmentak()
                .setBaimenak(perIns("salmentak"), perUpd("salmentak"), perDel("salmentak"))
                .setVisible(true);
    }

    private void irikiSaskiak(ActionEvent e) {
        new TaulaLeihoak.Saskiak()
                .setBaimenak(perIns("saskiak"), perUpd("saskiak"), perDel("saskiak"))
                .setVisible(true);
    }

    private void irikiArazoak(ActionEvent e) {
        new TaulaLeihoak.Arazoak()
                .setBaimenak(perIns("arazoak"), perUpd("arazoak"), perDel("arazoak"))
                .setVisible(true);
    }


    private boolean perIns(String taula) {
        String r = rola.toLowerCase();
        String t = taula.toLowerCase();

        if (r.equals("langilea")) {
            if (t.equals("bezeroak") || t.equals("produktuak")) return true;
            return false;
        }

        if (r.equals("arduraduna")) {
            if (t.equals("bezeroak") || t.equals("produktuak") || t.equals("hornitzaileak")) return true;
            if (t.equals("erosketak")) return true; 
            return false;
        }

        if (r.equals("kudeatzailea")) {
            if (t.equals("bezeroak") || t.equals("produktuak") || t.equals("hornitzaileak") || t.equals("erosketak"))
                return true;
            if (t.equals("langileak")) return true;
            return false;
        }

        return false;
    }

    private boolean perUpd(String taula) {
        String r = rola.toLowerCase();
        String t = taula.toLowerCase();

        if (r.equals("langilea")) {
            if (t.equals("bezeroak") || t.equals("produktuak")) return true;
            return false;
        }

        if (r.equals("arduraduna")) {
            if (t.equals("bezeroak") || t.equals("produktuak") || t.equals("hornitzaileak")) return true;
            if (t.equals("langileak")) return true; 
            return false;
        }

        if (r.equals("kudeatzailea")) {
            if (t.equals("bezeroak") || t.equals("produktuak") || t.equals("hornitzaileak") || t.equals("langileak"))
                return true;
            return false;
        }

        return false;
    }

    private boolean perDel(String taula) {
        String r = rola.toLowerCase();
        String t = taula.toLowerCase();

        if (r.equals("langilea")) {
            if (t.equals("bezeroak") || t.equals("produktuak")) return true;
            if (t.equals("arazoak")) return true; 
            return false;
        }

        if (r.equals("arduraduna")) {
            if (t.equals("bezeroak") || t.equals("produktuak") || t.equals("hornitzaileak")) return true;
            if (t.equals("arazoak")) return true;
            return false;
        }

        if (r.equals("kudeatzailea")) {
            if (t.equals("bezeroak") || t.equals("produktuak") || t.equals("hornitzaileak")) return true;
            if (t.equals("langileak")) return true; 
            if (t.equals("arazoak")) return true;
            return false;
        }

        return false;
    }
}