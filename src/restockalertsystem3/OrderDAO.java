package restockalertsystem3;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    
    public boolean addOrder(Order order) {
        String sql = "INSERT INTO orders (product_id, supplier_id, quantity_ordered, order_date, expected_delivery_date, status, total_cost, notes) VALUES ((SELECT id FROM products WHERE name = ? LIMIT 1), (SELECT id FROM suppliers WHERE name = ? LIMIT 1), ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, order.getProduct().getName());
            pstmt.setString(2, order.getSupplier().getName());
            pstmt.setInt(3, order.getQuantityOrdered());
            pstmt.setDate(4, Date.valueOf(order.getOrderDate()));
            pstmt.setDate(5, Date.valueOf(order.getExpectedDeliveryDate()));
            pstmt.setString(6, order.getStatus().toString());
            pstmt.setDouble(7, order.getTotalCost());
            pstmt.setString(8, order.getNotes());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, p.name as product_name, p.quantity, p.min_threshold, p.price, p.category, " +
                     "s.name as supplier_name, s.phone, s.email, s.address " +
                     "FROM orders o " +
                     "JOIN products p ON o.product_id = p.id " +
                     "JOIN suppliers s ON o.supplier_id = s.id " +
                     "ORDER BY o.order_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Product product = new Product(
                    rs.getString("product_name"),
                    rs.getInt("quantity"),
                    rs.getInt("min_threshold"),
                    rs.getDouble("price"),
                    rs.getString("category")
                );
                
                Supplier supplier = new Supplier(
                    rs.getString("supplier_name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("address")
                );
                
                Order order = new Order(
                    product,
                    rs.getInt("quantity_ordered"),
                    supplier,
                    rs.getDate("expected_delivery_date").toLocalDate()
                );
                
                // Set status
                order.setStatus(Order.OrderStatus.valueOf(rs.getString("status")));
                order.setNotes(rs.getString("notes"));
                
                orders.add(order);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return orders;
    }
    
    public boolean updateOrderStatus(int orderId, Order.OrderStatus status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status.toString());
            pstmt.setInt(2, orderId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}