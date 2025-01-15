package com.mycompany.car_rental_project;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.*;
//import java.io.File;
import java.io.FileWriter;

public class AdminDashboard extends JFrame {
    private JTable carTable, rentalTable;
    private JTextArea notificationArea;
    private JButton refreshCarsButton, refreshRentalsButton,markResolvedButton, generateInvoiceButton,markAsReturnedButton, generateReportButton;
    
    private JButton addCarButton, editCarButton, deleteCarButton, toggleAvailabilityButton, adjustStockButton;

    public AdminDashboard() {
        // Frame setup
        setTitle("Admin Dashboard");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        Color pinkColor = new Color(255, 192, 203); // RGB values for pink

        JPanel notificationPanel = new JPanel(new BorderLayout(10, 10));
        notificationPanel.setBackground(pinkColor);

        notificationArea = new JTextArea(10, 30);
        notificationArea.setEditable(false);
        notificationArea.setBackground(Color.WHITE);
        JScrollPane notificationScrollPane = new JScrollPane(notificationArea);
        notificationScrollPane.getViewport().setBackground(pinkColor);

        markResolvedButton = new JButton("Mark Resolved");
        markResolvedButton.setBackground(Color.WHITE);
        notificationPanel.add(new JLabel("Notifications", JLabel.CENTER), BorderLayout.NORTH);
        notificationPanel.add(notificationScrollPane, BorderLayout.CENTER);
        notificationPanel.add(markResolvedButton, BorderLayout.SOUTH);

        JPanel carPanel = new JPanel(new BorderLayout(10, 10));
        carPanel.setBackground(pinkColor);

        carTable = new JTable();
        JScrollPane carScrollPane = new JScrollPane(carTable);
        carScrollPane.getViewport().setBackground(pinkColor);

        JPanel carButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        carButtonsPanel.setBackground(pinkColor);

        refreshCarsButton = new JButton("Refresh Cars");
        refreshCarsButton.setBackground(Color.WHITE);
        addCarButton = new JButton("Add Car");
        addCarButton.setBackground(Color.WHITE);
        editCarButton = new JButton("Edit Car");
        editCarButton.setBackground(Color.WHITE);
        deleteCarButton = new JButton("Delete Car");
        deleteCarButton.setBackground(Color.WHITE);
        toggleAvailabilityButton = new JButton("Toggle Availability");
        toggleAvailabilityButton.setBackground(Color.WHITE);
        adjustStockButton = new JButton("Adjust Stock");
        adjustStockButton.setBackground(Color.WHITE);

        carButtonsPanel.add(refreshCarsButton);
        carButtonsPanel.add(addCarButton);
        carButtonsPanel.add(editCarButton);
        carButtonsPanel.add(deleteCarButton);
        carButtonsPanel.add(adjustStockButton);
        carButtonsPanel.add(toggleAvailabilityButton);
        
        carPanel.add(new JLabel("Car Inventory", JLabel.CENTER), BorderLayout.NORTH);
        carPanel.add(carScrollPane, BorderLayout.CENTER);
        carPanel.add(carButtonsPanel, BorderLayout.SOUTH);

        JPanel rentalPanel = new JPanel(new BorderLayout(10, 10));
        rentalPanel.setBackground(pinkColor);

        rentalTable = new JTable();
        JScrollPane rentalScrollPane = new JScrollPane(rentalTable);
        rentalScrollPane.getViewport().setBackground(pinkColor);

        JPanel rentalButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        rentalButtonsPanel.setBackground(pinkColor);

        refreshRentalsButton = new JButton("Refresh Rentals");
        refreshRentalsButton.setBackground(Color.WHITE);
        generateInvoiceButton = new JButton("Generate Invoice");
        generateInvoiceButton.setBackground(Color.WHITE);
        markAsReturnedButton = new JButton("Mark as Returned");
        markAsReturnedButton.setBackground(Color.WHITE);
        generateReportButton = new JButton("Generate Report");
        generateReportButton.setBackground(Color.WHITE);

        rentalButtonsPanel.add(refreshRentalsButton);
        rentalButtonsPanel.add(generateInvoiceButton);
        rentalButtonsPanel.add(markAsReturnedButton);
        rentalButtonsPanel.add(generateReportButton);

        rentalPanel.add(new JLabel("Rental History", JLabel.CENTER), BorderLayout.NORTH);
        rentalPanel.add(rentalScrollPane, BorderLayout.CENTER);
        rentalPanel.add(rentalButtonsPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, rentalPanel, carPanel);
        splitPane.setDividerLocation(350);

        add(splitPane, BorderLayout.CENTER);
        add(notificationPanel, BorderLayout.WEST);

        getContentPane().setBackground(pinkColor);

        refreshCarsButton.addActionListener(new RefreshCarsAction());
        refreshRentalsButton.addActionListener(new RefreshRentalsAction());
        markResolvedButton.addActionListener(new MarkResolvedAction());
        generateInvoiceButton.addActionListener(new GenerateInvoiceAction());
        markAsReturnedButton.addActionListener(new MarkAsReturnedAction());
        generateReportButton.addActionListener(new GenerateReportAction());
        addCarButton.addActionListener(new AddCarAction());
        editCarButton.addActionListener(new EditCarAction());
        deleteCarButton.addActionListener(new DeleteCarAction());
        adjustStockButton.addActionListener(new AdjustStockAction());
        toggleAvailabilityButton.addActionListener(new ToggleAvailabilityAction());

        loadCars();
        loadRentals();
        loadNotifications();

        setLocationRelativeTo(null);
        setVisible(true);
    }

     private void loadCars() {
        DefaultTableModel CarModel = new DefaultTableModel(
                new String[]{"ID", "factory", "Model", "Year", "Type", "Price/Day", "Stock","Available"}, 0//JJJJJJJJ
        );
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM cars")) {

            while (rs.next()) {
                CarModel.addRow(new Object[]{
                        rs.getInt("car_id"),
                        rs.getString("factory"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getString("type"),
                        rs.getDouble("price_per_day"),
                        rs.getInt("stock"), 
                        rs.getBoolean("availability_status") ? "Yes" : "No"
                      
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading cars: " + e.getMessage());
        }
        carTable.setModel(CarModel);
    }

    private void loadRentals() {
    DefaultTableModel tableModel = new DefaultTableModel(
        
              new String[]{"ID", "User Email", "Car Model", "Start Date", "End Date", "Total Price", "Status"}, 0    
    );

              
            String query = "SELECT r.rent_id, r.user_email, c.model, r.start_date, r.end_date, r.total_price, r.status " +
                       "FROM rent r JOIN cars c ON r.car_id = c.car_id";



        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("rent_id"),
                        rs.getString("user_email"),
                        rs.getString("model"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getDouble("total_price"),
                        rs.getString("status")
                });

            }
        }
     catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error loading rentals: " + e.getMessage());
    }
    rentalTable.setModel(tableModel);
}

    private void loadNotifications() {
        notificationArea.setText(""); 
    
        String overdueQuery = "SELECT r.user_email, c.model, c.year, r.end_date " +
                "FROM rent r JOIN cars c ON r.car_id = c.car_id " +
                "WHERE r.status = 'active' AND CURDATE() > r.end_date";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet overdueRs = stmt.executeQuery(overdueQuery/*query*/)) {
            notificationArea.append("Overdue Rentals:\n");
            while (overdueRs.next()) {
                notificationArea.append("User: " + overdueRs.getString("user_email") +
                        ", Cars: " + overdueRs.getString("model") +
                        ", Due Date: " + overdueRs.getDate("end_date") + "\n");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading notifications: " + e.getMessage());
        }
    }

    private class RefreshCarsAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            loadCars();
        }
    }

    private class RefreshRentalsAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            loadRentals();
        }
    }

    private class MarkResolvedAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            notificationArea.setText("");
            JOptionPane.showMessageDialog(AdminDashboard.this, "All notifications marked as resolved!");
        }
    }
   
private class  GenerateInvoiceAction implements ActionListener {
              @Override
  
public void actionPerformed(ActionEvent e) {
    int selectedRow = rentalTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(AdminDashboard.this, "Please select a rental to generate an invoice.");
        return;
    }

    try {
        // Retrieve values from the table
        int rentalId = Integer.parseInt(rentalTable.getValueAt(selectedRow, 0).toString());
        String userEmail = rentalTable.getValueAt(selectedRow, 1).toString();
        String carModel = rentalTable.getValueAt(selectedRow, 2).toString();
        String startDate = rentalTable.getValueAt(selectedRow, 3).toString();
        String endDate = rentalTable.getValueAt(selectedRow, 4).toString();
        double totalPrice = Double.parseDouble(rentalTable.getValueAt(selectedRow, 5).toString());
        String status = rentalTable.getValueAt(selectedRow, 6).toString();


        // Prepare invoice content
        String invoiceContent = "Rental Invoice\n"
                + "--------------\n"
                + "Rental ID: " + rentalId + "\n"
                + "User Email: " + userEmail + "\n"
                + "Car Model: " + carModel + "\n"
                + "Rental Period: " + startDate + " to " + endDate + "\n"
                + "Total Price: $" + totalPrice + "\n"
               // + "Fee (Overdue): $" + fee + "\n"
                + "Final Price: $" + (totalPrice ) + "\n";

        // Save the invoice to a file
        try (FileWriter writer = new FileWriter("Invoice_" + rentalId + ".txt")) {
            writer.write(invoiceContent);
            JOptionPane.showMessageDialog(AdminDashboard.this, "Invoice saved as: Invoice_" + rentalId + ".txt");
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(AdminDashboard.this, "Error generating invoice: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

        
    }    
    
private class MarkAsReturnedAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = rentalTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(AdminDashboard.this, "Please select a rental to mark as returned.");
                return;
            }

            int rentalId = (int) rentalTable.getValueAt(selectedRow, 0);
            int carId = getCarIdForRent(rentalId); 

            try (Connection conn = DatabaseManager.getConnection()){
                 
                    String updateStatusQuery = "UPDATE rent SET status = 'returned' WHERE rent_id = ?";
               try (PreparedStatement stmt = conn.prepareStatement(updateStatusQuery)) {

                stmt.setInt(1, rentalId);
                int rowsUpdated = stmt.executeUpdate();

                if (rowsUpdated > 0) {
                  String updateStockQuery = "UPDATE cars SET stock = stock + 1 WHERE car_id = ?";
                    try (PreparedStatement stockStmt = conn.prepareStatement(updateStockQuery)) {
                        stockStmt.setInt(1, carId);
                        stockStmt.executeUpdate();
                    }  
                    
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Rental marked as returned.");
                    loadRentals();
                } else {
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Failed to mark rental as returned.");
                }
               }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(AdminDashboard.this, "Error updating status: " + ex.getMessage());
            }
        
    }
  
      private int getCarIdForRent(int rentId) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT car_id FROM rent WHERE rent_id = ?")) {
            stmt.setInt(1, rentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("car_id");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(AdminDashboard.this, "Error fetching car ID: " + ex.getMessage());
        }
        return -1;
    }
}

    private class GenerateReportAction implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        String totalBookingsQuery = "SELECT COUNT(*) AS total_bookings FROM rent";
        String totalCustomersQuery = "SELECT COUNT(DISTINCT user_email) AS total_customers FROM rent";
        String totalRevenueQuery = "SELECT SUM(total_price + fee) AS total_revenue FROM rent";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             FileWriter writer = new FileWriter("Report.txt")) {

            writer.write("Rental Report\n\n");
            try (ResultSet rs = stmt.executeQuery(totalBookingsQuery)) {
                if (rs.next()) {
                    writer.write("Total Bookings: " + rs.getInt("total_bookings") + "\n");
                }
            }

            try (ResultSet rs = stmt.executeQuery(totalCustomersQuery)) {
                if (rs.next()) {
                    writer.write("Total Customers: " + rs.getInt("total_customers") + "\n");
                }
            }

            try (ResultSet rs = stmt.executeQuery(totalRevenueQuery)) {
                if (rs.next()) {
                    writer.write("Total Sum of Total Prices: $" + rs.getDouble("total_revenue") + "\n");
                }
            }

            writer.close();
            JOptionPane.showMessageDialog(AdminDashboard.this, "Report saved as Report.txt");

        } catch (SQLException | IOException ex) {
            JOptionPane.showMessageDialog(AdminDashboard.this, "Error generating report: " + ex.getMessage());
        }
    }
}

private class AddCarAction implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        String factory = JOptionPane.showInputDialog("Enter Car factory:");
        String model = JOptionPane.showInputDialog("Enter Car Model:");
        String year = JOptionPane.showInputDialog("Enter Car Year:");
        String type = JOptionPane.showInputDialog("Enter Car Type:");
        String price = JOptionPane.showInputDialog("Enter Price/Day:");
        String stock = JOptionPane.showInputDialog("Enter Stock:");
        String availability = JOptionPane.showInputDialog("Enter availability (yes/no):");

        if (factory == null || model == null || year == null || type == null || price == null || stock == null || availability == null) {
            JOptionPane.showMessageDialog(null, "All fields are required.");
            return;
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO cars (factory, model, year, type, price_per_day, stock, availability_status) VALUES (?, ?, ?, ?, ?, ?, ?)")) {

            stmt.setString(1, factory);
            stmt.setString(2, model);
            stmt.setInt(3, Integer.parseInt(year));
            stmt.setString(4, type);
            stmt.setDouble(5, Double.parseDouble(price));
            stmt.setInt(6, Integer.parseInt(stock));

            // تحويل availability إلى رقم منطقي
            boolean isAvailable = availability.equalsIgnoreCase("yes");
            stmt.setBoolean(7, isAvailable);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Car added successfully!");
            loadCars();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error adding car: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid numbers for year, price, and stock.");
        }
    }
}

    private class EditCarAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = carTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a car to edit.");
                return;
            }

            int carId = (int) carTable.getValueAt(selectedRow, 0);

            String factory = JOptionPane.showInputDialog("Enter new factory:", carTable.getValueAt(selectedRow, 1));
            String model = JOptionPane.showInputDialog("Enter new Model:", carTable.getValueAt(selectedRow, 2));
            String year = JOptionPane.showInputDialog("Enter new Year:", carTable.getValueAt(selectedRow, 3));
            String type = JOptionPane.showInputDialog("Enter new Type:", carTable.getValueAt(selectedRow, 4));
            String price = JOptionPane.showInputDialog("Enter new Price/Day:", carTable.getValueAt(selectedRow, 5));
            String stock = JOptionPane.showInputDialog("Enter new Stock:", carTable.getValueAt(selectedRow, 6));
            
            if (factory == null || model == null || year == null || type == null || price == null || stock == null) {
                JOptionPane.showMessageDialog(AdminDashboard.this, "All fields are required.");
                return;
            }
            
            
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("UPDATE cars SET factory = ?, model = ?, year = ?, type = ?, price_per_day = ?, stock = ? WHERE car_id = ?")) {

                 stmt.setString(1, factory);
                stmt.setString(2, model);
                stmt.setInt(3, Integer.parseInt(year));
                stmt.setString(4, type);
                stmt.setDouble(5, Double.parseDouble(price));
                stmt.setInt(6, Integer.parseInt(stock));
                stmt.setInt(7, carId);


                stmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Car details updated successfully!");
                loadCars();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error editing car: " + ex.getMessage());
            }
        }
    }

    private class DeleteCarAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = carTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a car to delete.");
                return;
            }

            int carId = (int) carTable.getValueAt(selectedRow, 0);
            

            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this car?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM cars WHERE car_id = ?")) {

               stmt.setInt(1, carId);
               // stmt.executeUpdate();
                 int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Car deleted successfully!");
                    loadCars();
                } else {
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Failed to delete car.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error deleting car: " + ex.getMessage());
            }
        }
    }

    private class ToggleAvailabilityAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = carTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a car to toggle availability.");
                return;
            }

            int carId = (int) carTable.getValueAt(selectedRow, 0);
            String query = "UPDATE cars SET availability_status = NOT availability_status WHERE car_id = ?";

            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, carId);

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Car availability toggled successfully!");
                loadCars();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error toggling availability: " + ex.getMessage());
            }
        }
    }
    
    private class AdjustStockAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = carTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(AdminDashboard.this, "Please select a car to adjust stock.");
                return;
            }

            int carId = (int) carTable.getValueAt(selectedRow, 0);
            String newStock = JOptionPane.showInputDialog("Enter new stock value:", carTable.getValueAt(selectedRow, 6));

            if (newStock == null) {
                JOptionPane.showMessageDialog(AdminDashboard.this, "Stock adjustment canceled.");
                return;
            }

            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("UPDATE cars SET stock = ? WHERE car_id = ?")) {

                stmt.setInt(1, Integer.parseInt(newStock));
                stmt.setInt(2, carId);

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(AdminDashboard.this, "Stock updated successfully!");
                loadCars();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(AdminDashboard.this, "Error updating stock: " + ex.getMessage());
            }
        }
    }  
    
}