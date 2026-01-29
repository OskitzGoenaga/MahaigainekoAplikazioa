package Bistak;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class PantallaPrintzipala extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField Nan_field;
	private JTextField Pasahitza_field;
	
   

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PantallaPrintzipala frame = new PantallaPrintzipala();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	 public PantallaPrintzipala() {
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        setBounds(100, 100, 450, 300);
	        contentPane = new JPanel();
	        contentPane.setForeground(new Color(255, 255, 255));
	        contentPane.setBackground(new Color(255, 255, 255));
	        setContentPane(contentPane);
	        contentPane.setLayout(null);
	        
	        JLabel lblNewLabel = new JLabel("MAHAIGAINEKO APLIKAZIOA:");
	        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
	        lblNewLabel.setBounds(84, 26, 280, 30);
	        contentPane.add(lblNewLabel);
	        
	        JLabel lblNewLabel_1 = new JLabel("NAN-a:");
	        lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
	        lblNewLabel_1.setBounds(101, 95, 57, 13);
	        contentPane.add(lblNewLabel_1);
	        
	        Nan_field = new JTextField();
	        Nan_field.setBounds(101, 118, 96, 19);
	        contentPane.add(Nan_field);
	        Nan_field.setColumns(10);
	        
	        Pasahitza_field = new JTextField();
	        Pasahitza_field.setColumns(10);
	        Pasahitza_field.setBounds(101, 170, 96, 19);
	        contentPane.add(Pasahitza_field);
	        
	        JLabel lblNewLabel_1_1 = new JLabel("Pasahitza:");
	        lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
	        lblNewLabel_1_1.setBounds(101, 147, 96, 13);
	        contentPane.add(lblNewLabel_1_1);
	        
	        JButton btnNewButton = new JButton("SARTU");
	        btnNewButton.setForeground(Color.BLACK);
	        btnNewButton.setBackground(Color.WHITE);

	        // Listener
	        btnNewButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                String rol = login();

	                if (rol != null && !rol.isEmpty()) {
	                    PantailaTaulak pantalla = new PantailaTaulak(rol);
	                    pantalla.setVisible(true);
	                    dispose();
	                } else {
	                    JOptionPane.showMessageDialog(
	                        PantallaPrintzipala.this,
	                        "NAN edo pasahitza okerra",
	                        "Errorea",
	                        JOptionPane.ERROR_MESSAGE
	                    );
	                }
	            }
	        });

	        btnNewButton.setBounds(244, 118, 85, 71);
	        contentPane.add(btnNewButton);
	 }


	protected void abrirPantalla(String tipo) {
		// TODO Auto-generated method stub
		
	}
	public String login() {
	    String nan = Nan_field.getText().trim();
	    String pasahitza = Pasahitza_field.getText().trim();

	    if (nan.isEmpty() || pasahitza.isEmpty()) {
	        return null;
	    }

	    String rol = null;
	    Connection cn = null;

	    try {
	        cn = new Conexioa().getConnection();

	        if (cn == null) {  
	            JOptionPane.showMessageDialog(
	                this,
	                "Ezin izan da MySQL-era konektatu",
	                "Errorea",
	                JOptionPane.ERROR_MESSAGE
	            );
	            return null; 
	        }

	        PreparedStatement ps = cn.prepareStatement(
	            "SELECT rol FROM langileak WHERE nan = ? AND pasahitza = ?"
	        );
	        ps.setString(1, nan);
	        ps.setString(2, pasahitza);

	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) {
	            rol = rs.getString("rol");
	        }

	        rs.close();
	        ps.close();
	        cn.close();

	    } catch (SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(
	            this,
	            "Errorea login egitean:\n" + e.getMessage(),
	            "Errorea",
	            JOptionPane.ERROR_MESSAGE
	        );
	    }

	    return rol;
	}

}
