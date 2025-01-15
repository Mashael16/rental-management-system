package com.mycompany.car_rental_project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginSignUpPage extends JFrame {
    
    JPanel contentPanel, emailPanel, passwordPanel, buttonPanel, buttonPanel1,buttonPanel11, titlePanel, photo;
    JTextField emailField;
    JPasswordField passwordField;
    JButton loginButton, cancelButton, signInButton;

    public LoginSignUpPage() {
       
        setSize(450, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        contentPanel = (JPanel) getContentPane();
        contentPanel.setBackground(Color.PINK);


        photo = new JPanel();
        
        ImageIcon phot = new ImageIcon("C:\\Users\\amram\\Downloads\\zip_-_3.png");
        Image img = phot.getImage(); 
        Image resizedImage = img.getScaledInstance(217, 120, Image.SCALE_SMOOTH); 
        ImageIcon Icon = new ImageIcon(resizedImage);
        JLabel pho = new JLabel(Icon);
        photo.setBounds(50, 251, 200, 116);
        photo.add(pho);
        contentPanel.add(photo);

       
        titlePanel = new JPanel();
        titlePanel.setBounds(140, 10, 160, 40);
        titlePanel.setBackground(Color.PINK);
        JLabel titleLabel1 = new JLabel("Car Rental ");
        titleLabel1.setFont(new Font("Times New Roman", Font.BOLD, 30));
        titleLabel1.setForeground(Color.white);
        titlePanel.add(titleLabel1);
        contentPanel.add(titlePanel);

        emailPanel = new JPanel();
        emailPanel.setBackground(Color.PINK);
        emailPanel.setBounds(120, 70, 200, 40);
        emailField = new JTextField(20);
        setPlaceholder(emailField, "Email");
        emailPanel.add(emailField);
        contentPanel.add(emailPanel);

        passwordPanel = new JPanel();
       passwordPanel.setBackground(Color.PINK);
        passwordPanel.setBounds(120, 120, 200, 40);
        passwordField = new JPasswordField(20);
        setPlaceholder(passwordField, "Password");
        passwordPanel.add(passwordField);
        contentPanel.add(passwordPanel);

        buttonPanel = new JPanel();
        buttonPanel.setBounds(85, 180, 90, 40);
        buttonPanel.setBackground(Color.pink);
        loginButton = new JButton("Login");
        loginButton.setForeground(Color.BLACK);
        loginButton.setBackground(Color.WHITE);

        buttonPanel1 = new JPanel();
        buttonPanel1.setBounds(185, 180, 90, 40);
        buttonPanel1.setBackground(Color.pink);
        signInButton = new JButton("Sign In");
        signInButton.setForeground(Color.BLACK);
        signInButton.setBackground(Color.WHITE);

        buttonPanel11=new JPanel();
        buttonPanel11.setBounds(285, 180, 90, 40);
        cancelButton = new JButton("Cancel");
        buttonPanel11.setBackground(Color.pink);
       cancelButton.setBackground(Color.WHITE);


        buttonPanel.add(loginButton);
        buttonPanel1.add(signInButton);
        buttonPanel11.add(cancelButton);
        contentPanel.add(buttonPanel);
        contentPanel.add(buttonPanel1);
        contentPanel.add(buttonPanel11);
        
        loginButton.addActionListener(new LoginAction());
        signInButton.addActionListener(new SignUpAction());
        cancelButton.addActionListener(e -> System.exit(0));
        setVisible(true);

    }

    public void setPlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);

        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                }
            }
        });
    }
    
    
     private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            // Check if admin credentials are entered
            if (email.equals("admin@admin.com") && password.equals("12345678")) {
                JOptionPane.showMessageDialog(LoginSignUpPage.this, "Welcome Admin!");
                dispose();
                new AdminDashboard().setVisible(true);
                return;
            }

            // Validate credentials
            try (Connection connection = DatabaseManager.getConnection();
                 PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?")) {

                stmt.setString(1, email);
                stmt.setString(2, password);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(LoginSignUpPage.this, "Login successful!");
                        dispose();
                        new Cars(email).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(LoginSignUpPage.this, "Invalid email or password!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(LoginSignUpPage.this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
     }
    
    
    // Action for sign-up button
    private class SignUpAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            // Validate input
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                JOptionPane.showMessageDialog(LoginSignUpPage.this, "Invalid email format!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (password.length() < 8) {
                JOptionPane.showMessageDialog(LoginSignUpPage.this, "Password must be at least 8 characters long!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Insert new user into the database
            try (Connection connection = DatabaseManager.getConnection();
                 PreparedStatement stmt = connection.prepareStatement("INSERT INTO users (email, password) VALUES (?, ?)")) {

                stmt.setString(1, email);
                stmt.setString(2, password);
                stmt.executeUpdate();
                
                JOptionPane.showMessageDialog(LoginSignUpPage.this, "Sign Up successful! You can now log in.");
                dispose();
                new Cars(email).setVisible(true);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(LoginSignUpPage.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
