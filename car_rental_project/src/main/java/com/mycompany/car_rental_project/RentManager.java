package com.mycompany.car_rental_project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RentManager extends JFrame {
    private JTable rentalsTable;
    private JTextField modelField, yearField, startDateField, endDateField, totalPriceField;
    private JButton calculateButton, confirmButton, cancelButton, refreshButton;
    private String userEmail;
    

public RentManager(String userEmail) {
    this.userEmail = userEmail;

     Color pinkColor = new Color(255, 192, 203); 

    setTitle("Manage Rentals");
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLayout(new BorderLayout(10, 10));


    rentalsTable = new JTable();
    rentalsTable.setBackground(Color.white);
    rentalsTable.setForeground(Color.black);  
    rentalsTable.setFont(new Font("Arial", Font.BOLD, 14)); 
    JScrollPane scrollPane = new JScrollPane(rentalsTable);
    add(scrollPane, BorderLayout.CENTER);

    JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
    inputPanel.setBackground(pinkColor);

    JLabel modelLabel = new JLabel("Model:");
    JLabel yearLabel = new JLabel("Year:");
    JLabel startDateLabel = new JLabel("Start Date (YYYY-MM-DD):");
    JLabel endDateLabel = new JLabel("End Date (YYYY-MM-DD):");
    JLabel totalPriceLabel = new JLabel("Total Price:");

   
    JLabel[] labels = {modelLabel, yearLabel, startDateLabel, endDateLabel, totalPriceLabel};
    for (JLabel label : labels) {
        label.setBackground(Color.white);
        label.setForeground(Color.black); 
        label.setFont(new Font("Arial", Font.BOLD, 14)); 
    }

    modelField = new JTextField();
    yearField = new JTextField();
    startDateField = new JTextField();
    endDateField = new JTextField();
    totalPriceField = new JTextField();
    totalPriceField.setEditable(false);

    JTextField[] textFields = {modelField, yearField, startDateField, endDateField, totalPriceField};
    for (JTextField textField : textFields) {
        textField.setBackground(Color.WHITE); 
        textField.setForeground(Color.black);  
        textField.setFont(new Font("Arial", Font.PLAIN, 14)); 
    }

    inputPanel.add(modelLabel);
    inputPanel.add(modelField);
    inputPanel.add(yearLabel);
    inputPanel.add(yearField);
    inputPanel.add(startDateLabel);
    inputPanel.add(startDateField);
    inputPanel.add(endDateLabel);
    inputPanel.add(endDateField);
    inputPanel.add(totalPriceLabel);
    inputPanel.add(totalPriceField);

    
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    buttonPanel.setBackground(pinkColor);


    calculateButton = new JButton("Calculate Price");
    confirmButton = new JButton("Confirm Rental");
    cancelButton = new JButton("Cancel Rental");
    refreshButton = new JButton("Refresh Rentals");

    
    JButton[] buttons = {calculateButton, confirmButton, cancelButton, refreshButton};
    for (JButton button : buttons) {
        button.setBackground(Color.white); 
        button.setForeground(Color.black);  
        button.setFont(new Font("Arial", Font.BOLD, 14)); 
    }

    buttonPanel.add(calculateButton);
    buttonPanel.add(confirmButton);

    JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    bottomButtonPanel.setBackground(pinkColor); 
    bottomButtonPanel.add(cancelButton);
    bottomButtonPanel.add(refreshButton);

    add(bottomButtonPanel, BorderLayout.SOUTH);

   
    JPanel southPanel = new JPanel(new BorderLayout(10, 10));
    southPanel.setBackground(pinkColor); 
    southPanel.add(inputPanel, BorderLayout.CENTER);
    southPanel.add(buttonPanel, BorderLayout.SOUTH);
    add(southPanel, BorderLayout.NORTH); 

   
    calculateButton.addActionListener(new CalculateAction());
    confirmButton.addActionListener(new ConfirmAction());
    cancelButton.addActionListener(new CancelAction());
    refreshButton.addActionListener(new RefreshAction());

    
    loadRentals();

    setLocationRelativeTo(null);
    setVisible(true);
}

    private void loadRentals() {
        DefaultTableModel tableModel = new DefaultTableModel(
                new String[]{"ID", "Car Model", "Year", "Start Date", "End Date", "Total Price", "Status", "Overdue Days", "Fee"}, 0
        );

       
        String updatePendingToActive = "UPDATE rent SET status = 'active' WHERE status = 'pending' AND start_date = CURDATE()";//
        String query = "SELECT r.rent_id, c.model, c.year, r.start_date, r.end_date, r.total_price, r.status, r.overdue_days, r.fee " +
                       "FROM rent r " +
                       "JOIN cars c ON r.car_id = c.car_id " +
                       "WHERE r.user_email = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement updateStmt = conn.prepareStatement(updatePendingToActive);//active
             PreparedStatement stmt = conn.prepareStatement(query)) {

            updateStmt.executeUpdate();
            stmt.setString(1, userEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getInt("rent_id"),
                            rs.getString("model"),
                            rs.getInt("year"),
                            rs.getDate("start_date"),
                            rs.getDate("end_date"),
                            rs.getDouble("total_price"),
                            rs.getString("status"),
                            rs.getInt("overdue_days"),
                            rs.getDouble("fee")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading rentals: " + e.getMessage());
        }
        rentalsTable.setModel(tableModel);
    }

    
    
    
    
    
    private class CalculateAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            calculatePrice();
        }
    }

    private class ConfirmAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            confirmRental();
        }
    }
    
    private void calculatePrice() {
    String model = modelField.getText().trim();
    String year = yearField.getText().trim();
    String startDateText = startDateField.getText().trim();
    String endDateText = endDateField.getText().trim();

    if (model.isEmpty() || year.isEmpty() || startDateText.isEmpty() || endDateText.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please fill in all fields.");
        return;
    }

    try {
        LocalDate startDate = LocalDate.parse(startDateText);
        LocalDate endDate = LocalDate.parse(endDateText);

        
        if (startDate.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "Start date cannot be in the past.");
            return;
        }

        if (endDate.isBefore(startDate) || endDate.isEqual(startDate)) {
            JOptionPane.showMessageDialog(this, "End date must be after the start date.");
            return;
        }

        long rentalDays = ChronoUnit.DAYS.between(startDate, endDate);

        String query = "SELECT price_per_day, stock FROM cars WHERE model = ? AND year = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, model);
            stmt.setString(2, year);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int stock = rs.getInt("stock");
                    if (stock > 0) {
                        double pricePerDay = rs.getDouble("price_per_day");
                        double totalPrice = pricePerDay * rentalDays;
                        totalPriceField.setText(String.format("%.2f", totalPrice));
                        JOptionPane.showMessageDialog(this, "Price calculated successfully!");
                    } else {
                       
                        JOptionPane.showMessageDialog(this, "Car is out of stock. Would you like to select a different car?");
                        showAvailableCars(model, year);  
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Car not found. Please check the model and year.");
                }
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error calculating price: " + e.getMessage());
    }
}

    
private void showAvailableCars(String model, String year) {
    String query = "SELECT model, year FROM cars WHERE model LIKE ? AND year = ? AND stock > 0";
    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, "%" + model + "%");
        stmt.setString(2, year);

        try (ResultSet rs = stmt.executeQuery()) {
            StringBuilder availableCars = new StringBuilder("Available Cars:\n");
            boolean availableFound = false;

            while (rs.next()) {
                availableCars.append(rs.getString("model")).append(" - ").append(rs.getString("year")).append("\n");
                availableFound = true;
            }

            if (availableFound) {
                JOptionPane.showMessageDialog(this, availableCars.toString());
            } else {
                JOptionPane.showMessageDialog(this, "No available cars found for the selected model and year.");
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error fetching available cars: " + e.getMessage());
    }
}
    

    private void confirmRental() {
    String model = modelField.getText().trim();
    String year = yearField.getText().trim();
    String startDateText = startDateField.getText().trim();
    String endDateText = endDateField.getText().trim();
    String totalPriceText = totalPriceField.getText().trim();
    

    if (model.isEmpty() || year.isEmpty() || startDateText.isEmpty() || endDateText.isEmpty() || totalPriceText.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please fill in all fields.");
        return;
    }
    String message = "By confirming this rental, you agree to the following terms:\n" +
                     "- You are responsible for returning the car on time.\n" +
                     "- Late returns may incur additional fees.\n" +
                     "- You will handle the car with care and follow all safety regulations.\n\n" +
                     "Do you accept these terms and wish to proceed with the rental?";

    int result = JOptionPane.showConfirmDialog(this, message, "Rental Agreement", JOptionPane.YES_NO_OPTION);
    if (result == JOptionPane.NO_OPTION) {
        JOptionPane.showMessageDialog(this, "Rental process canceled.");
        return; 
    }
	try (Connection conn = DatabaseManager.getConnection()) {
			String confirmQuery = "INSERT INTO rent (user_email, car_id, start_date, end_date, total_price, status) " +
				"SELECT ?, car_id, ?, ?, ?, 'pending' FROM cars WHERE model = ? AND year = ? AND stock > 0";
			try (PreparedStatement stmt = conn.prepareStatement(confirmQuery)) {
				stmt.setString(1, userEmail);
				stmt.setString(2, startDateText);
				stmt.setString(3, endDateText);
				stmt.setDouble(4, Double.parseDouble(totalPriceText));
				stmt.setString(5, model);
				stmt.setString(6, year);

				int rowsAffected = stmt.executeUpdate();
				if (rowsAffected > 0) {
					try (PreparedStatement updateStock = conn.prepareStatement(
						"UPDATE cars SET stock = stock - 1 WHERE model = ? AND year = ?")) {
						updateStock.setString(1, model);
						updateStock.setString(2, year);
						updateStock.executeUpdate();
					}
					JOptionPane.showMessageDialog(this, "Rental confirmed.");
					loadRentals();
				}
				else {
					JOptionPane.showMessageDialog(this, "Car not available.");
				}
			}
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Error confirming rental: " + e.getMessage());
		}
    }
    

    private class CancelAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            cancelRental();

        }
    }
    
    private void cancelRental() {
    int selectedRow = rentalsTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a rental to cancel.");
        return;
    }

    int rentId = (int) rentalsTable.getValueAt(selectedRow, 0);
    String status = (String) rentalsTable.getValueAt(selectedRow, 6);

    if (!"pending".equals(status)) {
        JOptionPane.showMessageDialog(this, "Only pending rentals can be canceled.");
        return;
    }

    String query = "DELETE FROM rent WHERE rent_id = ? AND status = 'pending'";
    String getCarIdQuery = "SELECT car_id FROM rent WHERE rent_id = ?";
    String updateStockQuery = "UPDATE cars SET stock = stock + 1 WHERE car_id = ?";

    try (Connection conn = DatabaseManager.getConnection()) {
        int carId;
        try (PreparedStatement getCarIdStmt = conn.prepareStatement(getCarIdQuery)) {
            getCarIdStmt.setInt(1, rentId);
            try (ResultSet rs = getCarIdStmt.executeQuery()) {
                if (rs.next()) {
                    carId = rs.getInt("car_id");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to fetch car information for the selected rental.");
                    return;
                }
            }
        }

       
        try (PreparedStatement deleteStmt = conn.prepareStatement(query)) {
            deleteStmt.setInt(1, rentId);
            int rowsAffected = deleteStmt.executeUpdate();

            if (rowsAffected > 0) {
                try (PreparedStatement updateStockStmt = conn.prepareStatement(updateStockQuery)) {
                    updateStockStmt.setInt(1, carId);
                    updateStockStmt.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Rental canceled successfully.");
                loadRentals();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel the rental.");
            }
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error canceling rental: " + ex.getMessage());
    }
}

 private class RefreshAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            loadRentals();
            
            
        }
    }
}
    
   