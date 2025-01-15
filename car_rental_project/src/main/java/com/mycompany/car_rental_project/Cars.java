package com.mycompany.car_rental_project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Cars extends JFrame {
    private JTable carTable;
    private JTextArea notificationArea;
    private JTextField modelField, typeField;
    private JButton searchButton, manageRentalsButton, clearNotificationsButton;
    private String userEmail;

    public Cars(String userEmail) {
        this.userEmail = userEmail;

        Color pinkColor = new Color(255, 192, 203); 

        
        setTitle("Car Browser");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

  
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(pinkColor);
        JLabel modelLabel = new JLabel("Model:");
        modelField = new JTextField(10);
        JLabel typeLabel = new JLabel("Type:");
        typeField = new JTextField(10);
        searchButton = new JButton("Search");
        searchButton.setBackground(Color.WHITE);
        manageRentalsButton = new JButton("Manage Rentals");
        manageRentalsButton.setBackground(Color.WHITE);

        searchPanel.add(modelLabel);
        searchPanel.add(modelField);
        searchPanel.add(typeLabel);
        searchPanel.add(typeField);
        searchPanel.add(searchButton);
        searchPanel.add(manageRentalsButton);

        add(searchPanel, BorderLayout.NORTH);

      
        carTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(carTable);
        scrollPane.getViewport().setBackground(pinkColor); 
        add(scrollPane, BorderLayout.CENTER);

        
        notificationArea = new JTextArea();
        notificationArea.setEditable(false);
        JScrollPane notificationScrollPane = new JScrollPane(notificationArea);
        notificationScrollPane.setBorder(BorderFactory.createTitledBorder("Notifications"));
        notificationScrollPane.getViewport().setBackground(pinkColor); 

        
        clearNotificationsButton = new JButton("Clear Notifications");
        clearNotificationsButton.setBackground(Color.WHITE);
        clearNotificationsButton.addActionListener(new ClearNotificationsAction());

        JPanel notificationPanel = new JPanel(new BorderLayout());
        notificationPanel.setBackground(pinkColor);
        notificationPanel.add(notificationScrollPane, BorderLayout.CENTER);
        notificationPanel.add(clearNotificationsButton, BorderLayout.SOUTH);

        
        add(notificationPanel, BorderLayout.WEST);

       
        loadCars("", "");
        loadNotifications(); 

        searchButton.addActionListener(new SearchAction());
        manageRentalsButton.addActionListener(new ManageRentalsAction());

        getContentPane().setBackground(pinkColor);

        setLocationRelativeTo(null); 
        setVisible(true);
    }

    public void loadCars(String model, String type) {
        DefaultTableModel tableModel = new DefaultTableModel(new String[]{"ID", "Factory", "Model", "Year", "Type", "Price/Day", "Availability"}, 0);

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM cars WHERE model LIKE ? AND type LIKE ?")) {

            stmt.setString(1, "%" + model + "%");
            stmt.setString(2, "%" + type + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    boolean availabilityStatus = rs.getBoolean("availability_status");
                    String availability = availabilityStatus ? "Yes" : "No";

                    tableModel.addRow(new Object[]{
                            rs.getInt("car_id"),
                            rs.getString("factory"),
                            rs.getString("model"),
                            rs.getInt("year"),
                            rs.getString("type"),
                            rs.getDouble("price_per_day"),
                            availability
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading cars: " + e.getMessage());
        }
        carTable.setModel(tableModel);
    }

    private void loadNotifications() {
        notificationArea.setText(""); 

     
        addNewRentNotifications();
        addUpcomingRentNotifications();
        addReminderNotifications();
    }

    private void addNewRentNotifications() {
        String query = "SELECT c.model, c.factory, r.end_date FROM rent r JOIN cars c ON r.car_id = c.car_id " +
                "WHERE r.user_email = ? AND DATE(r.start_date) = CURDATE()";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notificationArea.append("New Rent:\n");
                    notificationArea.append("Car: " + rs.getString("factory") + " " + rs.getString("model") + "\n");
                    notificationArea.append("Due Date: " + rs.getDate("end_date") + "\n\n");
                }
            }
        } catch (SQLException e) {
            notificationArea.append("Error fetching new rent notifications: " + e.getMessage() + "\n");
        }
    }

    private void addUpcomingRentNotifications() {
        String query = "SELECT c.model, c.factory, r.start_date FROM rent r JOIN cars c ON r.car_id = c.car_id " +
                "WHERE r.user_email = ? AND DATE(r.start_date) > CURDATE() AND DATE(r.start_date) <= CURDATE() + INTERVAL 3 DAY";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notificationArea.append("Upcoming Rent:\n");
                    notificationArea.append("Car: " + rs.getString("factory") + " " + rs.getString("model") + "\n");
                    notificationArea.append("Start Date: " + rs.getDate("start_date") + "\n\n");
                }
            }
        } catch (SQLException e) {
            notificationArea.append("Error fetching upcoming rent notifications: " + e.getMessage() + "\n");
        }
    }

    private void addReminderNotifications() {
        String query = "SELECT c.model, c.factory, r.end_date, DATEDIFF(r.end_date, CURDATE()) AS days_left " +
                "FROM rent r JOIN cars c ON r.car_id = c.car_id " +
                "WHERE r.user_email = ? AND DATE(r.end_date) > CURDATE() AND DATEDIFF(r.end_date, CURDATE()) <= 3";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notificationArea.append("Reminder:\n");
                    notificationArea.append("Car: " + rs.getString("factory") + " " + rs.getString("model") + "\n");
                    notificationArea.append("Ends in: " + rs.getInt("days_left") + " day(s)\n\n");
                }
            }
        } catch (SQLException e) {
            notificationArea.append("Error fetching reminders: " + e.getMessage() + "\n");
        }
    }

    
    private class ClearNotificationsAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            notificationArea.setText(""); 
        }
    }

    private class SearchAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String model = modelField.getText();
            String type = typeField.getText();
            loadCars(model, type);
        }
    }

    private class ManageRentalsAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new RentManager(userEmail).setVisible(true);
        }
    }
}



