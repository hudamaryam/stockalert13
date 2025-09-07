package restockalertsystem1;

public class Alert {
    private Product product;
    private String message;
    
    public Alert(Product product) {
        this.product = product;
        this.message = generateMessage();
    }
    
    private String generateMessage() {
        if (product.getQuantity() == 0) {
            return "CRITICAL: " + product.getName() + " is OUT OF STOCK!";
        } else if (product.isLowStock()) {
            return "WARNING: " + product.getName() + " is below minimum threshold! Current: " + 
                   product.getQuantity() + ", Min: " + product.getMinThreshold();
        } else {
            return "INFO: " + product.getName() + " stock level is normal";
        }
    }
    
    public Product getProduct() {
        return product;
    }
    
    public String getMessage() {
        return message;
    }
    
    @Override
    public String toString() {
        return message;
    }
}