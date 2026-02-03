package Bistak;

import java.awt.EventQueue;
import javax.swing.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.sql.*;

public class PantailaPrintzipala extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField Nan_field;
    private JTextField Pasahitza_field;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                PantailaPrintzipala frame = new PantailaPrintzipala();
                frame.setVisible(true);
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    public PantailaPrintzipala() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 460, 300);
        contentPane = new JPanel();
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lbl = new JLabel("MAHAIGAINEKO APLIKAZIOA:");
        lbl.setFont(new Font("Tahoma", Font.PLAIN, 20));
        lbl.setBounds(84, 26, 300, 30);
        contentPane.add(lbl);

        JLabel l1 = new JLabel("NAN-a:");
        l1.setFont(new Font("Tahoma", Font.PLAIN, 15));
        l1.setBounds(101, 95, 80, 13);
        contentPane.add(l1);

        Nan_field = new JTextField();
        Nan_field.setBounds(101, 118, 120, 19);
        contentPane.add(Nan_field);

        JLabel l2 = new JLabel("Pasahitza:");
        l2.setFont(new Font("Tahoma", Font.PLAIN, 15));
        l2.setBounds(101, 147, 96, 13);
        contentPane.add(l2);

        Pasahitza_field = new JTextField();
        Pasahitza_field.setBounds(101, 170, 120, 19);
        contentPane.add(Pasahitza_field);

        JButton btn = new JButton("SARTU");
        btn.addActionListener((ActionEvent e) -> {
            String rola = login();
            if (rola != null && !rola.isEmpty()) {
                PantailaTaulak pantalla = new PantailaTaulak(rola);
                pantalla.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "NAN edo pasahitza okerra", "Errorea", JOptionPane.ERROR_MESSAGE);
            }
        });
        btn.setBounds(244, 118, 100, 71);
        contentPane.add(btn);
    }

    public String login() {
        String nan = Nan_field.getText().trim();
        String pasahitza = Pasahitza_field.getText().trim();
        if (nan.isEmpty() || pasahitza.isEmpty()) return null;

        String rola = null;
        try (Connection cn = new Conexioa().getConnection()) {
            if (cn == null) {
                JOptionPane.showMessageDialog(this, "Ezin izan da MySQL-era konektatu", "Errorea", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            PreparedStatement ps = cn.prepareStatement(
                "SELECT rol FROM langileak WHERE nan = ? AND pasahitza = ?"
            );
            ps.setString(1, nan);
            ps.setString(2, pasahitza);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) rola = rs.getString("rol");
            rs.close(); ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Errorea login egitean:\n" + e.getMessage(),
                    "Errorea", JOptionPane.ERROR_MESSAGE);
        }
        return rola;
    }
}