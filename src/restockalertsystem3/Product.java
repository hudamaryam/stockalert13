package restockalertsystem3;

public class Product {
    private String name;
    private int quantity;
    private int minThreshold;
    private double price;
    private int soldCount;
    private String category;
    
    // Main constructor
    public Product(String name, int quantity, int minThreshold, double price, String category) {
        this.name = name;
        this.quantity = quantity;
        this.minThreshold = minThreshold;
        this.price = price;
        this.category = category;
        this.soldCount = 0;
    }
    
    // Backward compatibility constructor
    public Product(String name, int quantity, int minThreshold) {
        this(name, quantity, minThreshold, 0.0, "General");
    }
    
    // Getters
    public String getName() {
        return name;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public int getMinThreshold() {
        return minThreshold;
    }
    
    public double getPrice() {
        return price;
    }
    
    public int getSoldCount() {
        return soldCount;
    }
    
    public String getCategory() {
        return category;
    }
    
    // Setters
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public void setMinThreshold(int minThreshold) {
        this.minThreshold = minThreshold;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    // Business methods
    
    // Add this method to Product class
    public void setSoldCount(int soldCount) {
        this.soldCount = soldCount;
    }
    public boolean isLowStock() {
        return quantity < minThreshold;
    }
    
    public void sellProduct(int quantityToSell) {
        if (quantityToSell > 0 && quantityToSell <= quantity) {
            quantity -= quantityToSell;
            soldCount += quantityToSell;
        }
    }
    
    public void restockProduct(int quantityToAdd) {
        if (quantityToAdd > 0) {
            quantity += quantityToAdd;
        }
    }
    
    public double getTotalRevenue() {
        return soldCount * price;
    }
    
    public String getStockStatus() {
        if (quantity == 0) {
            return "OUT OF STOCK";
        } else if (isLowStock()) {
            return "LOW STOCK";
        } else {
            return "IN STOCK";
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s - Qty: %d - Price: $%.2f - Sold: %d - Status: %s", 
                           name, quantity, price, soldCount, getStockStatus());
    }
}