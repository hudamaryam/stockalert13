package restockalertsystem1;

import java.util.ArrayList;
import java.util.List;

public class Supplier {
    private static int supplierIdCounter = 1;
    
    private int supplierId;
    private String name;
    private String contactInfo;
    private String email;
    private String address;
    private String phone;
    private double reliabilityRating; // 1.0 to 5.0
    private List<String> specialties; // Product categories they specialize in
    private boolean isActive;
    private int totalOrdersPlaced;
    private int ordersDeliveredOnTime;
    
    public Supplier(String name, String contactInfo) {
        this.supplierId = supplierIdCounter++;
        this.name = name;
        this.contactInfo = contactInfo;
        this.email = "";
        this.address = "";
        this.phone = contactInfo; // Assuming contactInfo is phone by default
        this.reliabilityRating = 5.0; // Start with perfect rating
        this.specialties = new ArrayList<>();
        this.isActive = true;
        this.totalOrdersPlaced = 0;
        this.ordersDeliveredOnTime = 0;
    }
    
    public Supplier(String name, String phone, String email, String address) {
        this(name, phone);
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.contactInfo = phone + " / " + email;
    }
    
    // Getters
    public int getSupplierId() {
        return supplierId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getContactInfo() {
        return contactInfo;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getAddress() {
        return address;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public double getReliabilityRating() {
        return reliabilityRating;
    }
    
    public List<String> getSpecialties() {
        return new ArrayList<>(specialties);
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public int getTotalOrdersPlaced() {
        return totalOrdersPlaced;
    }
    
    public int getOrdersDeliveredOnTime() {
        return ordersDeliveredOnTime;
    }
    
    // Setters
    public void setName(String name) {
        this.name = name;
    }
    
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
    
    public void setEmail(String email) {
        this.email = email;
        updateContactInfo();
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
        updateContactInfo();
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
    
    private void updateContactInfo() {
        StringBuilder contact = new StringBuilder();
        if (!phone.isEmpty()) {
            contact.append(phone);
        }
        if (!email.isEmpty()) {
            if (contact.length() > 0) {
                contact.append(" / ");
            }
            contact.append(email);
        }
        this.contactInfo = contact.toString();
    }
    
    // Business Logic Methods
    public void addSpecialty(String specialty) {
        if (!specialties.contains(specialty)) {
            specialties.add(specialty);
        }
    }
    
    public void removeSpecialty(String specialty) {
        specialties.remove(specialty);
    }
    
    public boolean hasSpecialty(String specialty) {
        return specialties.contains(specialty);
    }
    
    public void recordOrder() {
        totalOrdersPlaced++;
    }
    
    public void recordOnTimeDelivery() {
        ordersDeliveredOnTime++;
        updateReliabilityRating();
    }
    
    public void recordLateDelivery() {
        updateReliabilityRating();
    }
    
    private void updateReliabilityRating() {
        if (totalOrdersPlaced > 0) {
            double onTimePercentage = (double) ordersDeliveredOnTime / totalOrdersPlaced;
            reliabilityRating = 1.0 + (onTimePercentage * 4.0); // Scale to 1.0-5.0
        }
    }
    
    public double getOnTimeDeliveryPercentage() {
        if (totalOrdersPlaced == 0) return 100.0;
        return ((double) ordersDeliveredOnTime / totalOrdersPlaced) * 100.0;
    }
    
    public String getReliabilityDescription() {
        if (reliabilityRating >= 4.5) {
            return "Excellent";
        } else if (reliabilityRating >= 3.5) {
            return "Good";
        } else if (reliabilityRating >= 2.5) {
            return "Average";
        } else if (reliabilityRating >= 1.5) {
            return "Poor";
        } else {
            return "Very Poor";
        }
    }
    
    public String getStatusDescription() {
        return isActive ? "Active" : "Inactive";
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s) - Rating: %.1f/5.0 (%s)", 
                           name, contactInfo, reliabilityRating, getReliabilityDescription());
    }
    
    public String getDetailedInfo() {
        StringBuilder info = new StringBuilder();
        info.append("SUPPLIER DETAILS\n");
        info.append("=".repeat(40)).append("\n");
        info.append("ID: ").append(supplierId).append("\n");
        info.append("Name: ").append(name).append("\n");
        info.append("Phone: ").append(phone.isEmpty() ? "N/A" : phone).append("\n");
        info.append("Email: ").append(email.isEmpty() ? "N/A" : email).append("\n");
        info.append("Address: ").append(address.isEmpty() ? "N/A" : address).append("\n");
        info.append("Status: ").append(getStatusDescription()).append("\n");
        info.append("Reliability Rating: ").append(String.format("%.1f/5.0 (%s)", reliabilityRating, getReliabilityDescription())).append("\n");
        info.append("Total Orders: ").append(totalOrdersPlaced).append("\n");
        info.append("On-Time Deliveries: ").append(ordersDeliveredOnTime).append("\n");
        info.append("On-Time Delivery Rate: ").append(String.format("%.1f%%", getOnTimeDeliveryPercentage())).append("\n");
        
        if (!specialties.isEmpty()) {
            info.append("Specialties: ").append(String.join(", ", specialties)).append("\n");
        }
        
        return info.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Supplier supplier = (Supplier) obj;
        return supplierId == supplier.supplierId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(supplierId);
    }
}
