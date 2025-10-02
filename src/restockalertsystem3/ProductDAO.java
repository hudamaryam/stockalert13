package restockalertsystem3;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    
    public boolean addProduct(Product product) {
        String sql = "INSERT INTO products (name, quantity, min_threshold, price, sold_count, category) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, product.getName());
            pstmt.setInt(2, product.getQuantity());
            pstmt.setInt(3, product.getMinThreshold());
            pstmt.setDouble(4, product.getPrice());
            pstmt.setInt(5, product.getSoldCount());
            pstmt.setString(6, product.getCategory());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Product product = new Product(
                    rs.getString("name"),
                    rs.getInt("quantity"),
                    rs.getInt("min_threshold"),
                    rs.getDouble("price"),
                    rs.getString("category")
                );
                
                // Set sold count manually since constructor doesn't support it
                int soldCount = rs.getInt("sold_count");
                for (int i = 0; i < soldCount; i++) {
                    product.sellProduct(0); // This is a workaround
                }
                // Better approach: modify Product to have setSoldCount method
                
                products.add(product);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return products;
    }
    
    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET quantity = ?, min_threshold = ?, price = ?, sold_count = ?, category = ? WHERE name = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, product.getQuantity());
            pstmt.setInt(2, product.getMinThreshold());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setInt(4, product.getSoldCount());
            pstmt.setString(5, product.getCategory());
            pstmt.setString(6, product.getName());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteProduct(String productName) {
        String sql = "DELETE FROM products WHERE name = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, productName);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Product getProductByName(String name) {
        String sql = "SELECT * FROM products WHERE name = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Product(
                    rs.getString("name"),
                    rs.getInt("quantity"),
                    rs.getInt("min_threshold"),
                    rs.getDouble("price"),
                    rs.getString("category")
                );
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public List<Product> getLowStockProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE quantity < min_threshold";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                products.add(new Product(
                    rs.getString("name"),
                    rs.getInt("quantity"),
                    rs.getInt("min_threshold"),
                    rs.getDouble("price"),
                    rs.getString("category")
                ));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return products;
    }
}
