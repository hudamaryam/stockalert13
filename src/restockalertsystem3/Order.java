package restockalertsystem1;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Order {
    public enum OrderStatus {
        PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    }
    
    private static int orderCounter = 1000; // Starting order ID
    
    private int orderId;
    private Product product;
    private int quantityOrdered;
    private LocalDate orderDate;
    private LocalDate expectedDeliveryDate;
    private Supplier supplier;
    private OrderStatus status;
    private double totalCost;
    private String notes;
    
    public Order(Product product, int quantityOrdered, Supplier supplier) {
        this.orderId = ++orderCounter;
        this.product = product;
        this.quantityOrdered = quantityOrdered;
        this.supplier = supplier;
        this.orderDate = LocalDate.now();
        this.expectedDeliveryDate = orderDate.plusDays(7); // Default 7 days delivery
        this.status = OrderStatus.PENDING;
        this.totalCost = calculateTotalCost();
        this.notes = "";
    }
    
    public Order(Product product, int quantityOrdered, Supplier supplier, LocalDate expectedDelivery) {
        this(product, quantityOrdered, supplier);
        this.expectedDeliveryDate = expectedDelivery;
    }
    
    private double calculateTotalCost() {
        // Assuming we order at the same price as selling price for simplicity
        // In real scenario, this would be a different wholesale/purchase price
        return quantityOrdered * product.getPrice() * 0.6; // 60% of retail price
    }
    
    // Getters
    public int getOrderId() {
        return orderId;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public int getQuantityOrdered() {
        return quantityOrdered;
    }
    
    public LocalDate getOrderDate() {
        return orderDate;
    }
    
    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }
    
    public Supplier getSupplier() {
        return supplier;
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public double getTotalCost() {
        return totalCost;
    }
    
    public String getNotes() {
        return notes;
    }
    
    // Setters
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    // Business Logic Methods
    public void confirmOrder() {
        if (status == OrderStatus.PENDING) {
            status = OrderStatus.CONFIRMED;
        }
    }
    
    public void shipOrder() {
        if (status == OrderStatus.CONFIRMED) {
            status = OrderStatus.SHIPPED;
        }
    }
    
    public void deliverOrder() {
        if (status == OrderStatus.SHIPPED) {
            status = OrderStatus.DELIVERED;
            // Automatically restock the product when delivered
            product.restockProduct(quantityOrdered);
        }
    }
    
    public void cancelOrder() {
        if (status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED) {
            status = OrderStatus.CANCELLED;
        }
    }
    
    public boolean isOverdue() {
        return LocalDate.now().isAfter(expectedDeliveryDate) && 
               (status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED || status == OrderStatus.SHIPPED);
    }
    
    public long getDaysUntilDelivery() {
        return LocalDate.now().until(expectedDeliveryDate).getDays();
    }
    
    public String getFormattedOrderDate() {
        return orderDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }
    
    public String getFormattedDeliveryDate() {
        return expectedDeliveryDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }
    
    public String getStatusDescription() {
        switch (status) {
            case PENDING:
                return "Order placed, awaiting confirmation";
            case CONFIRMED:
                return "Order confirmed by supplier";
            case SHIPPED:
                return "Order shipped, in transit";
            case DELIVERED:
                return "Order delivered successfully";
            case CANCELLED:
                return "Order cancelled";
            default:
                return "Unknown status";
        }
    }
    
    @Override
    public String toString() {
        return String.format("Order #%d - %s | Qty: %d | Status: %s | Delivery: %s | Supplier: %s | Cost: $%.2f",
                           orderId, product.getName(), quantityOrdered, status, 
                           getFormattedDeliveryDate(), supplier.getName(), totalCost);
    }
    
    public String getDetailedInfo() {
        StringBuilder info = new StringBuilder();
        info.append("ORDER DETAILS\n");
        info.append("=".repeat(40)).append("\n");
        info.append("Order ID: ").append(orderId).append("\n");
        info.append("Product: ").append(product.getName()).append("\n");
        info.append("Quantity: ").append(quantityOrdered).append("\n");
        info.append("Order Date: ").append(getFormattedOrderDate()).append("\n");
        info.append("Expected Delivery: ").append(getFormattedDeliveryDate()).append("\n");
        info.append("Status: ").append(status).append(" - ").append(getStatusDescription()).append("\n");
        info.append("Supplier: ").append(supplier.getName()).append("\n");
        info.append("Supplier Contact: ").append(supplier.getContactInfo()).append("\n");
        info.append("Total Cost: $").append(String.format("%.2f", totalCost)).append("\n");
        
        if (isOverdue()) {
            info.append("⚠️ WARNING: This order is overdue!\n");
        } else if (getDaysUntilDelivery() <= 2) {
            info.append("📦 Order expected to arrive soon!\n");
        }
        
        if (!notes.isEmpty()) {
            info.append("Notes: ").append(notes).append("\n");
        }
        
        return info.toString();
    }
}
