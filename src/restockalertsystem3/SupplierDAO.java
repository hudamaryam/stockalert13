package restockalertsystem3;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {
    
    // In SupplierDAO.java:
// You might already have some of this, but ensure the 'if (rowsAffected > 0)' block is EXACTLY this:

    public boolean addSupplier(Supplier supplier) {
    String sql = "INSERT INTO suppliers (name, phone, email, address, reliability_rating, is_active, total_orders, orders_on_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        
        // [Parameter binding for 1 through 8 remains the same]
        
        int rowsAffected = pstmt.executeUpdate();
        
        if (rowsAffected > 0) {
            // FIX: Retrieve the ID the database generated
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int dbId = rs.getInt(1);
                
                // CRITICAL FIX: Set the database ID back on the Java object
                supplier.setId(dbId); 
                
                // Insert specialties using the correct database ID
                for (String specialty : supplier.getSpecialties()) {
                    addSupplierSpecialty(dbId, specialty); // Use dbId here
                }
            }
            return true;
        }
        
    } catch (SQLException e) {
        System.err.println("--- SUPPLIER DAO INSERT FAILED ---");
        System.err.println("SQL Error Code: " + e.getErrorCode());
        System.err.println("SQL State: " + e.getSQLState());
        System.err.println("Database Error Message: " + e.getMessage());
        e.printStackTrace();
        e.printStackTrace();
    }
    return false;
}
    
    private void addSupplierSpecialty(int id, String specialty) {
        String sql = "INSERT INTO supplier_specialties (supplier_id, specialty) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.setString(2, specialty);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // REPLACE the existing getAllSuppliers() method in SupplierDAO.java
public List<Supplier> getAllSuppliers() {
    List<Supplier> suppliers = new ArrayList<>();
    String sql = "SELECT * FROM suppliers ORDER BY name";
    
    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        while (rs.next()) {
            // FIX: Use the new constructor to fully hydrate the object
            Supplier supplier = new Supplier(
                rs.getInt("id"), // New: Set the database ID
                rs.getString("name"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("address"),
                rs.getDouble("reliability_rating"), // New: Load rating
                rs.getBoolean("is_active"),         // New: Load status
                rs.getInt("total_orders"),          // New: Load total orders
                rs.getInt("orders_on_time")         // New: Load on-time orders
            );
            
            // Load specialties (as this still requires a separate call)
            List<String> specialties = getSupplierSpecialties(supplier.getSupplierId()); 
            for (String specialty : specialties) {
                supplier.addSpecialty(specialty);
            }
            
            suppliers.add(supplier);
        }
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    return suppliers;
}
    
    private List<String> getSupplierSpecialties(int id) {
        List<String> specialties = new ArrayList<>();
        String sql = "SELECT specialty FROM supplier_specialties WHERE supplier_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                specialties.add(rs.getString("specialty"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return specialties;
    }
    
    public boolean updateSupplier(Supplier supplier) {
        String sql = "UPDATE suppliers SET phone = ?, email = ?, address = ?, reliability_rating = ?, is_active = ?, total_orders = ?, orders_on_time = ? WHERE name = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, supplier.getPhone());
            pstmt.setString(2, supplier.getEmail());
            pstmt.setString(3, supplier.getAddress());
            pstmt.setDouble(4, supplier.getReliabilityRating());
            pstmt.setBoolean(5, supplier.isActive());
            pstmt.setInt(6, supplier.getTotalOrdersPlaced());
            pstmt.setInt(7, supplier.getOrdersDeliveredOnTime());
            pstmt.setString(8, supplier.getName());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Supplier getSupplierByName(String name) {
        String sql = "SELECT * FROM suppliers WHERE name = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Supplier(
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("address")
                );
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}