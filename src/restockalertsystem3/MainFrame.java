package restockalertsystem3;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.time.LocalDate;

public class MainFrame extends JFrame {
    private ArrayList<Product> products = new ArrayList<>();
    private ArrayList<Order> orders = new ArrayList<>();
    private ArrayList<Supplier> suppliers = new ArrayList<>();
    private JTable productTable;
    private JTable orderTable;
    private ProductTableModel tableModel;
    private OrderTableModel orderTableModel;
    private JLabel statsLabel;
    private JLabel alertLabel;
    private JProgressBar stockProgressBar;
    private JTextField searchField;
    private JComboBox<String> categoryFilter;
    private JTabbedPane tabbedPane;
    
    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(33, 150, 243);
    private static final Color SECONDARY_COLOR = new Color(76, 175, 80);
    private static final Color WARNING_COLOR = new Color(255, 152, 0);
    private static final Color DANGER_COLOR = new Color(244, 67, 54);
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(117, 117, 117);
    
    public MainFrame() {
        initializeGUI();
        addSampleData();
        updateDisplay();
        setVisible(true);
    }
    
    private void initializeGUI() {
        setTitle("Professional Inventory & Order Management System");
        setSize(1500, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Set modern look and feel
        try {
        String className = UIManager.getSystemLookAndFeelClassName();
        UIManager.setLookAndFeel(className);
    } catch (Exception e) {
        e.printStackTrace();
    }
        
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Create main components
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        // Title section
        JLabel titleLabel = new JLabel("Inventory & Order Management Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Complete inventory, orders & supplier management");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 230, 255));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.CENTER);
        
        // User info section
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        
        JLabel userLabel = new JLabel("Admin User");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(Color.WHITE);
        
        JButton logoutBtn = createModernButton("Logout", DANGER_COLOR, 80, 30);
        logoutBtn.addActionListener(e -> logout());
        
        userPanel.add(userLabel);
        userPanel.add(Box.createHorizontalStrut(15));
        userPanel.add(logoutBtn);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        // Create tabbed interface
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabbedPane.setBackground(CARD_COLOR);
        
        // Inventory Tab
        JPanel inventoryPanel = new JPanel(new BorderLayout(10, 10));
        inventoryPanel.setBackground(BACKGROUND_COLOR);
        inventoryPanel.add(createInventoryControlPanel(), BorderLayout.NORTH);
        inventoryPanel.add(createInventoryTablePanel(), BorderLayout.CENTER);
        
        // Orders Tab
        JPanel ordersPanel = new JPanel(new BorderLayout(10, 10));
        ordersPanel.setBackground(BACKGROUND_COLOR);
        ordersPanel.add(createOrderControlPanel(), BorderLayout.NORTH);
        ordersPanel.add(createOrderTablePanel(), BorderLayout.CENTER);
        
        // Suppliers Tab
        JPanel suppliersPanel = new JPanel(new BorderLayout(10, 10));
        suppliersPanel.setBackground(BACKGROUND_COLOR);
        suppliersPanel.add(createSupplierControlPanel(), BorderLayout.NORTH);
        suppliersPanel.add(createSupplierInfoPanel(), BorderLayout.CENTER);
        
        tabbedPane.addTab("📦 Inventory", inventoryPanel);
        tabbedPane.addTab("🚛 Orders", ordersPanel);
        tabbedPane.addTab("🏪 Suppliers", suppliersPanel);
        tabbedPane.addTab("📊 Analytics", createAnalyticsPanel());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createInventoryControlPanel() {
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(CARD_COLOR);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        // Left side - Search and filter
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchLabel.setForeground(TEXT_PRIMARY);
        
        searchField = new JTextField(20);
        styleTextField(searchField);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterTable();
            }
        });
        
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        categoryLabel.setForeground(TEXT_PRIMARY);
        
        categoryFilter = new JComboBox<>(new String[]{"All Categories", "Electronics", "General"});
        styleComboBox(categoryFilter);
        categoryFilter.addActionListener(e -> filterTable());
        
        leftPanel.add(searchLabel);
        leftPanel.add(searchField);
        leftPanel.add(Box.createHorizontalStrut(20));
        leftPanel.add(categoryLabel);
        leftPanel.add(categoryFilter);
        
        // Right side - Action buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        JButton addBtn = createModernButton("Add Product", SECONDARY_COLOR, 120, 35);
        JButton sellBtn = createModernButton("Sell", WARNING_COLOR, 80, 35);
        JButton restockBtn = createModernButton("Restock", PRIMARY_COLOR, 100, 35);
        JButton alertsBtn = createModernButton("Alerts", DANGER_COLOR, 90, 35);
        JButton orderBtn = createModernButton("Create Order", new Color(156, 39, 176), 130, 35);
        
        addBtn.addActionListener(e -> addProduct());
        sellBtn.addActionListener(e -> sellProduct());
        restockBtn.addActionListener(e -> restockProduct());
        alertsBtn.addActionListener(e -> checkAlerts());
        orderBtn.addActionListener(e -> createOrder());
        
        rightPanel.add(addBtn);
        rightPanel.add(sellBtn);
        rightPanel.add(restockBtn);
        rightPanel.add(alertsBtn);
        rightPanel.add(orderBtn);
        
        controlPanel.add(leftPanel, BorderLayout.WEST);
        controlPanel.add(rightPanel, BorderLayout.EAST);
        
        return controlPanel;
    }
    
    private JPanel createInventoryTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(0, 0, 0, 0)
        ));
        
        // Create table model
        tableModel = new ProductTableModel();
        productTable = new JTable(tableModel);
        styleTable(productTable);
        
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Add table header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(248, 249, 250));
        headerPanel.setBorder(new EmptyBorder(15, 20, 10, 20));
        
        JLabel tableTitle = new JLabel("Product Inventory");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(TEXT_PRIMARY);
        
        headerPanel.add(tableTitle);
        
        tablePanel.add(headerPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private JPanel createOrderControlPanel() {
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(CARD_COLOR);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        // Left side - Order filters
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);
        
        JLabel statusLabel = new JLabel("Status Filter:");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setForeground(TEXT_PRIMARY);
        
        JComboBox<String> statusFilter = new JComboBox<>(new String[]{
            "All Orders", "Pending", "Confirmed", "Shipped", "Delivered", "Cancelled"
        });
        styleComboBox(statusFilter);
        
        leftPanel.add(statusLabel);
        leftPanel.add(statusFilter);
        
        // Right side - Order actions
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        JButton newOrderBtn = createModernButton("New Order", SECONDARY_COLOR, 120, 35);
        JButton confirmBtn = createModernButton("Confirm", WARNING_COLOR, 100, 35);
        JButton cancelBtn = createModernButton("Cancel", DANGER_COLOR, 90, 35);
        JButton deliverBtn = createModernButton("Deliver", PRIMARY_COLOR, 100, 35);
        
        newOrderBtn.addActionListener(e -> createOrder());
        confirmBtn.addActionListener(e -> confirmOrder());
        cancelBtn.addActionListener(e -> cancelOrder());
        deliverBtn.addActionListener(e -> deliverOrder());
        
        rightPanel.add(newOrderBtn);
        rightPanel.add(confirmBtn);
        rightPanel.add(cancelBtn);
        rightPanel.add(deliverBtn);
        
        controlPanel.add(leftPanel, BorderLayout.WEST);
        controlPanel.add(rightPanel, BorderLayout.EAST);
        
        return controlPanel;
    }
    
    private JPanel createOrderTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(0, 0, 0, 0)
        ));
        
        orderTableModel = new OrderTableModel();
        orderTable = new JTable(orderTableModel);
        styleOrderTable(orderTable);
        
        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Add table header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(248, 249, 250));
        headerPanel.setBorder(new EmptyBorder(15, 20, 10, 20));
        
        JLabel tableTitle = new JLabel("Purchase Orders");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(TEXT_PRIMARY);
        
        headerPanel.add(tableTitle);
        
        tablePanel.add(headerPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    // Add this method inside the 'MainFrame' class, 
// likely around line 430, near other create*Panel methods.

private JPanel createSupplierControlPanel() {
    JPanel controlPanel = new JPanel(new BorderLayout());
    controlPanel.setBackground(CARD_COLOR);
    controlPanel.setBorder(BorderFactory.createCompoundBorder(
        new LineBorder(new Color(230, 230, 230), 1),
        new EmptyBorder(15, 20, 15, 20)
    ));
    
    // Left Side - Title
    JLabel titleLabel = new JLabel("Supplier Management");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
    titleLabel.setForeground(TEXT_PRIMARY);
    
    // Right Side - Action Buttons
    JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    rightPanel.setOpaque(false);
    
    JButton addSupplierBtn = createModernButton("Add Supplier", SECONDARY_COLOR, 130, 35);
    JButton editSupplierBtn = createModernButton("Edit", WARNING_COLOR, 80, 35);
    JButton viewDetailsBtn = createModernButton("View Details", PRIMARY_COLOR, 120, 35);
    
    // Link actions (assuming these methods exist in MainFrame)
    addSupplierBtn.addActionListener(e -> addSupplier());
    editSupplierBtn.addActionListener(e -> editSupplier());
    viewDetailsBtn.addActionListener(e -> viewSupplierDetails());
    
    rightPanel.add(addSupplierBtn);
    rightPanel.add(editSupplierBtn);
    rightPanel.add(viewDetailsBtn);
    
    controlPanel.add(titleLabel, BorderLayout.WEST);
    controlPanel.add(rightPanel, BorderLayout.EAST);
    
    return controlPanel;
}
    // Replace the createSupplierInfoPanel() method in MainFrame.java with this improved version

    private JPanel createSupplierInfoPanel() {
    JPanel supplierPanel = new JPanel(new BorderLayout(10, 10));
    supplierPanel.setBackground(BACKGROUND_COLOR);
    
    if (suppliers.isEmpty()) {
        // Show empty state with better design
        JPanel emptyPanel = new JPanel(new GridBagLayout());
        emptyPanel.setBackground(CARD_COLOR);
        emptyPanel.setBorder(new LineBorder(new Color(230, 230, 230), 1));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 10, 20);
        
        JLabel emptyIcon = new JLabel("🏪");
        emptyIcon.setFont(new Font("Segoe UI", Font.PLAIN, 72));
        emptyPanel.add(emptyIcon, gbc);
        
        gbc.gridy = 1;
        JLabel emptyTitle = new JLabel("No Suppliers Registered");
        emptyTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        emptyTitle.setForeground(TEXT_PRIMARY);
        emptyPanel.add(emptyTitle, gbc);
        
        gbc.gridy = 2;
        JLabel emptyMessage = new JLabel("Add suppliers to start tracking performance and managing orders");
        emptyMessage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emptyMessage.setForeground(TEXT_SECONDARY);
        emptyPanel.add(emptyMessage, gbc);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(20, 20, 20, 20);
        JButton addFirstSupplierBtn = createModernButton("Add Your First Supplier", SECONDARY_COLOR, 200, 40);
        addFirstSupplierBtn.addActionListener(e -> addSupplier());
        emptyPanel.add(addFirstSupplierBtn, gbc);
        
        supplierPanel.add(emptyPanel, BorderLayout.CENTER);
        return supplierPanel;
    }
    
    // Split panel for supplier list and details
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    splitPane.setResizeWeight(0.5);
    splitPane.setDividerSize(10);
    splitPane.setBorder(null);
    
    // Top: Supplier Cards Grid
    JPanel supplierCardsPanel = createSupplierCardsPanel();
    
    // Bottom: Performance Metrics
    JPanel metricsPanel = createSupplierMetricsPanel();
    
    splitPane.setTopComponent(supplierCardsPanel);
    splitPane.setBottomComponent(metricsPanel);
    
    supplierPanel.add(splitPane, BorderLayout.CENTER);
    
    return supplierPanel;
}

private JPanel createSupplierCardsPanel() {
    JPanel cardsPanel = new JPanel(new BorderLayout());
    cardsPanel.setBackground(CARD_COLOR);
    cardsPanel.setBorder(new LineBorder(new Color(230, 230, 230), 1));
    
    JLabel title = new JLabel("Active Suppliers (" + suppliers.size() + ")");
    title.setFont(new Font("Segoe UI", Font.BOLD, 14));
    title.setBorder(new EmptyBorder(15, 20, 10, 20));
    title.setForeground(TEXT_PRIMARY);
    
    // Create scrollable panel for supplier cards
    JPanel cardsContainer = new JPanel();
    cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
    cardsContainer.setBackground(Color.WHITE);
    cardsContainer.setBorder(new EmptyBorder(10, 15, 10, 15));
    
    for (Supplier supplier : suppliers) {
        cardsContainer.add(createSupplierCard(supplier));
        cardsContainer.add(Box.createVerticalStrut(10));
    }
    
    JScrollPane scrollPane = new JScrollPane(cardsContainer);
    scrollPane.setBorder(null);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    
    cardsPanel.add(title, BorderLayout.NORTH);
    cardsPanel.add(scrollPane, BorderLayout.CENTER);
    
    return cardsPanel;
}

    private JPanel createSupplierCard(Supplier supplier) {
    JPanel card = new JPanel(new BorderLayout(10, 5));
    card.setBackground(Color.WHITE);
    card.setBorder(BorderFactory.createCompoundBorder(
        new LineBorder(new Color(220, 220, 220), 1, true),
        new EmptyBorder(12, 15, 12, 15)
    ));
    card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
    
    // Left: Supplier info
    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
    infoPanel.setOpaque(false);
    
    JLabel nameLabel = new JLabel(supplier.getName());
    nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    nameLabel.setForeground(TEXT_PRIMARY);
    
    JLabel contactLabel = new JLabel("📞 " + supplier.getPhone() + 
        (supplier.getEmail().isEmpty() ? "" : " | ✉️ " + supplier.getEmail()));
    contactLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    contactLabel.setForeground(TEXT_SECONDARY);
    
    JLabel addressLabel = new JLabel("📍 " + (supplier.getAddress().isEmpty() ? "No address" : supplier.getAddress()));
    addressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    addressLabel.setForeground(TEXT_SECONDARY);
    
    infoPanel.add(nameLabel);
    infoPanel.add(Box.createVerticalStrut(3));
    infoPanel.add(contactLabel);
    infoPanel.add(Box.createVerticalStrut(2));
    infoPanel.add(addressLabel);
    
    // Right: Performance badges
    JPanel statsPanel = new JPanel(new GridLayout(2, 2, 10, 5));
    statsPanel.setOpaque(false);
    
    // Rating badge
    JPanel ratingBadge = createStatBadge(
        String.format("%.1f", supplier.getReliabilityRating()),
        "Rating",
        getRatingColor(supplier.getReliabilityRating())
    );
    
    // Orders badge
    JPanel ordersBadge = createStatBadge(
        String.valueOf(supplier.getTotalOrdersPlaced()),
        "Orders",
        new Color(33, 150, 243)
    );
    
    // On-time badge
    JPanel onTimeBadge = createStatBadge(
        String.format("%.0f%%", supplier.getOnTimeDeliveryPercentage()),
        "On-Time",
        new Color(76, 175, 80)
    );
    
    // Status badge
    JPanel statusBadge = createStatBadge(
        supplier.isActive() ? "Active" : "Inactive",
        "Status",
        supplier.isActive() ? new Color(76, 175, 80) : new Color(158, 158, 158)
    );
    
    statsPanel.add(ratingBadge);
    statsPanel.add(ordersBadge);
    statsPanel.add(onTimeBadge);
    statsPanel.add(statusBadge);
    
    card.add(infoPanel, BorderLayout.WEST);
    card.add(statsPanel, BorderLayout.EAST);
    
    // Make card clickable
    card.setCursor(new Cursor(Cursor.HAND_CURSOR));
    card.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            card.setBackground(new Color(248, 249, 250));
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            card.setBackground(Color.WHITE);
        }
        
        @Override
        public void mouseClicked(MouseEvent e) {
            showSupplierDetails(supplier);
        }
    });
    
    return card;
}

    private JPanel createStatBadge(String value, String label, Color color) {
    JPanel badge = new JPanel(new BorderLayout());
    badge.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
    badge.setBorder(new EmptyBorder(8, 10, 8, 10));
    
    JLabel valueLabel = new JLabel(value);
    valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
    valueLabel.setForeground(color);
    valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
    
    JLabel textLabel = new JLabel(label);
    textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
    textLabel.setForeground(TEXT_SECONDARY);
    textLabel.setHorizontalAlignment(SwingConstants.CENTER);
    
    badge.add(valueLabel, BorderLayout.CENTER);
    badge.add(textLabel, BorderLayout.SOUTH);
    
    return badge;
}

    private Color getRatingColor(double rating) {
    if (rating >= 4.5) return new Color(76, 175, 80);
    if (rating >= 3.5) return new Color(33, 150, 243);
    if (rating >= 2.5) return new Color(255, 152, 0);
    return new Color(244, 67, 54);
}

    private JPanel createSupplierMetricsPanel() {
    JPanel metricsPanel = new JPanel(new BorderLayout());
    metricsPanel.setBackground(CARD_COLOR);
    metricsPanel.setBorder(new LineBorder(new Color(230, 230, 230), 1));
    
    JLabel metricsTitle = new JLabel("Supplier Performance Overview");
    metricsTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
    metricsTitle.setBorder(new EmptyBorder(15, 20, 10, 20));
    metricsTitle.setForeground(TEXT_PRIMARY);
    
    JTextArea metricsArea = new JTextArea();
    metricsArea.setEditable(false);
    metricsArea.setFont(new Font("Consolas", Font.PLAIN, 12));
    metricsArea.setBackground(Color.WHITE);
    metricsArea.setBorder(new EmptyBorder(10, 20, 20, 20));
    
    StringBuilder metrics = new StringBuilder();
    metrics.append("╔═══════════════════════════════════════════════════════════════════════╗\n");
    metrics.append("║                    SUPPLIER PERFORMANCE SUMMARY                       ║\n");
    metrics.append("╠═══════════════════════════════════════════════════════════════════════╣\n\n");
    
    // Overall statistics
    int totalSuppliers = suppliers.size();
    int activeSuppliers = (int) suppliers.stream().filter(Supplier::isActive).count();
    double avgRating = suppliers.stream().mapToDouble(Supplier::getReliabilityRating).average().orElse(0);
    int totalOrders = suppliers.stream().mapToInt(Supplier::getTotalOrdersPlaced).sum();
    double avgOnTime = suppliers.stream().mapToDouble(Supplier::getOnTimeDeliveryPercentage).average().orElse(0);
    
    metrics.append(String.format("📊 OVERVIEW\n"));
    metrics.append(String.format("   Total Suppliers: %d | Active: %d | Inactive: %d\n", 
        totalSuppliers, activeSuppliers, totalSuppliers - activeSuppliers));
    metrics.append(String.format("   Average Rating: %.2f/5.0 | Total Orders: %d | Avg On-Time: %.1f%%\n\n", 
        avgRating, totalOrders, avgOnTime));
    
    metrics.append("─".repeat(75)).append("\n\n");
    
    // Individual supplier details
    metrics.append("📋 DETAILED PERFORMANCE\n\n");
    
    for (Supplier supplier : suppliers) {
        String statusIcon = supplier.isActive() ? "✅" : "⚠️";
        String ratingStars = "⭐".repeat((int) Math.round(supplier.getReliabilityRating()));
        
        metrics.append(String.format("%s %s\n", statusIcon, supplier.getName()));
        metrics.append(String.format("   Rating: %s (%.1f/5.0) - %s\n", 
            ratingStars, supplier.getReliabilityRating(), supplier.getReliabilityDescription()));
        metrics.append(String.format("   Orders: %d total | %d on-time | %.1f%% delivery rate\n",
            supplier.getTotalOrdersPlaced(), 
            supplier.getOrdersDeliveredOnTime(),
            supplier.getOnTimeDeliveryPercentage()));
        metrics.append(String.format("   Contact: %s\n", supplier.getContactInfo()));
        
        if (!supplier.getAddress().isEmpty()) {
            metrics.append(String.format("   Address: %s\n", supplier.getAddress()));
        }
        
        if (!supplier.getSpecialties().isEmpty()) {
            metrics.append(String.format("   Specialties: %s\n", String.join(", ", supplier.getSpecialties())));
        }
        
        metrics.append("\n");
    }
    
    metricsArea.setText(metrics.toString());
    
    JScrollPane metricsScrollPane = new JScrollPane(metricsArea);
    metricsScrollPane.setBorder(null);
    
    metricsPanel.add(metricsTitle, BorderLayout.NORTH);
    metricsPanel.add(metricsScrollPane, BorderLayout.CENTER);
    
    return metricsPanel;
}

    private void showSupplierDetails(Supplier supplier) {
    JDialog dialog = new JDialog(this, "Supplier Details: " + supplier.getName(), true);
    dialog.setSize(600, 500);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout());
    
    // Header
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(PRIMARY_COLOR);
    headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
    
    JLabel titleLabel = new JLabel(supplier.getName());
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    titleLabel.setForeground(Color.WHITE);
    
    JLabel subtitleLabel = new JLabel(supplier.getReliabilityDescription() + " Supplier");
    subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    subtitleLabel.setForeground(new Color(200, 230, 255));
    
    JPanel titlePanel = new JPanel(new BorderLayout());
    titlePanel.setOpaque(false);
    titlePanel.add(titleLabel, BorderLayout.NORTH);
    titlePanel.add(subtitleLabel, BorderLayout.CENTER);
    
    headerPanel.add(titlePanel, BorderLayout.WEST);
    
    // Content
    JTextArea detailsArea = new JTextArea(supplier.getDetailedInfo());
    detailsArea.setEditable(false);
    detailsArea.setFont(new Font("Consolas", Font.PLAIN, 12));
    detailsArea.setBackground(Color.WHITE);
    detailsArea.setBorder(new EmptyBorder(20, 20, 20, 20));
    
    JScrollPane scrollPane = new JScrollPane(detailsArea);
    scrollPane.setBorder(null);
    
    // Footer with action buttons
    JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
    footerPanel.setBackground(CARD_COLOR);
    footerPanel.setBorder(new MatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));
    
    JButton editBtn = createModernButton("Edit", WARNING_COLOR, 80, 35);
    JButton closeBtn = createModernButton("Close", TEXT_SECONDARY, 80, 35);
    
    editBtn.addActionListener(e -> {
        dialog.dispose();
        editSupplierDirect(supplier);
    });
    
    closeBtn.addActionListener(e -> dialog.dispose());
    
    footerPanel.add(editBtn);
    footerPanel.add(closeBtn);
    
    dialog.add(headerPanel, BorderLayout.NORTH);
    dialog.add(scrollPane, BorderLayout.CENTER);
    dialog.add(footerPanel, BorderLayout.SOUTH);
    
    dialog.setVisible(true);
}
    
    private void editSupplierDirect(Supplier supplier) {
    // Create edit dialog with current values
    JTextField phoneField = new JTextField(supplier.getPhone(), 15);
    JTextField emailField = new JTextField(supplier.getEmail(), 15);
    JTextField addressField = new JTextField(supplier.getAddress(), 15);
    JCheckBox activeCheckbox = new JCheckBox("Active", supplier.isActive());
    
    JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
    panel.add(new JLabel("Phone:"));
    panel.add(phoneField);
    panel.add(new JLabel("Email:"));
    panel.add(emailField);
    panel.add(new JLabel("Address:"));
    panel.add(addressField);
    panel.add(new JLabel("Status:"));
    panel.add(activeCheckbox);
    
    int result = JOptionPane.showConfirmDialog(this, panel, 
        "Edit Supplier: " + supplier.getName(), JOptionPane.OK_CANCEL_OPTION);
    
    if (result == JOptionPane.OK_OPTION) {
        try {
            supplier.setPhone(phoneField.getText().trim());
            supplier.setEmail(emailField.getText().trim());
            supplier.setAddress(addressField.getText().trim());
            supplier.setActive(activeCheckbox.isSelected());
            
            // Update database
            SupplierDAO supplierDAO = new SupplierDAO();
            if (supplierDAO.updateSupplier(supplier)) {
                updateDisplay();
                refreshSupplierPanel();
                JOptionPane.showMessageDialog(this, "Supplier updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Error updating supplier in database!");
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), 
                "Update Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
    
    private JPanel createAnalyticsPanel() {
        JPanel analyticsPanel = new JPanel(new BorderLayout());
        analyticsPanel.setBackground(BACKGROUND_COLOR);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(156, 39, 176));
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        
        JLabel titleLabel = new JLabel("Business Analytics Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Analytics content
        JTextArea analyticsArea = new JTextArea();
        analyticsArea.setEditable(false);
        analyticsArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        analyticsArea.setBackground(Color.WHITE);
        analyticsArea.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        updateAnalyticsDisplay(analyticsArea);
        
        JScrollPane scrollPane = new JScrollPane(analyticsArea);
        scrollPane.setBorder(new LineBorder(new Color(230, 230, 230), 1));
        
        analyticsPanel.add(headerPanel, BorderLayout.NORTH);
        analyticsPanel.add(scrollPane, BorderLayout.CENTER);
        
        return analyticsPanel;
    }
    
    private void updateAnalyticsDisplay(JTextArea analyticsArea) {
        StringBuilder analytics = new StringBuilder();
        
        analytics.append("📊 COMPREHENSIVE BUSINESS ANALYTICS\n");
        analytics.append("═".repeat(80)).append("\n\n");
        
        // Inventory Analytics
        analytics.append("📦 INVENTORY METRICS\n");
        analytics.append("─".repeat(50)).append("\n");
        int totalProducts = products.size();
        int totalStock = products.stream().mapToInt(Product::getQuantity).sum();
        long lowStockCount = products.stream().filter(Product::isLowStock).count();
        double totalInventoryValue = products.stream().mapToDouble(p -> p.getQuantity() * p.getPrice()).sum();
        
        analytics.append(String.format("Total Products: %15d\n", totalProducts));
        analytics.append(String.format("Total Stock Units: %11d\n", totalStock));
        analytics.append(String.format("Low Stock Alerts: %12d\n", lowStockCount));
        analytics.append(String.format("Total Inventory Value: $%8.2f\n\n", totalInventoryValue));
        
        // Sales Analytics
        analytics.append("💰 SALES PERFORMANCE\n");
        analytics.append("─".repeat(50)).append("\n");
        int totalSold = products.stream().mapToInt(Product::getSoldCount).sum();
        double totalRevenue = products.stream().mapToDouble(Product::getTotalRevenue).sum();
        double avgRevenue = products.isEmpty() ? 0 : totalRevenue / totalProducts;
        
        analytics.append(String.format("Total Units Sold: %12d\n", totalSold));
        analytics.append(String.format("Total Revenue: $%15.2f\n", totalRevenue));
        analytics.append(String.format("Avg Revenue/Product: $%8.2f\n\n", avgRevenue));
        
        // Order Analytics
        analytics.append("🚛 ORDER MANAGEMENT\n");
        analytics.append("─".repeat(50)).append("\n");
        int totalOrders = orders.size();
        long pendingOrders = orders.stream().filter(o -> o.getStatus() == Order.OrderStatus.PENDING).count();
        long deliveredOrders = orders.stream().filter(o -> o.getStatus() == Order.OrderStatus.DELIVERED).count();
        double totalOrderValue = orders.stream().mapToDouble(Order::getTotalCost).sum();
        
        analytics.append(String.format("Total Orders: %16d\n", totalOrders));
        analytics.append(String.format("Pending Orders: %13d\n", pendingOrders));
        analytics.append(String.format("Delivered Orders: %11d\n", deliveredOrders));
        analytics.append(String.format("Total Order Value: $%9.2f\n\n", totalOrderValue));
        
        // Supplier Analytics
        analytics.append("🏪 SUPPLIER PERFORMANCE\n");
        analytics.append("─".repeat(50)).append("\n");
        int totalSuppliers = suppliers.size();
        int activeSuppliers = (int) suppliers.stream().filter(Supplier::isActive).count();
        double avgRating = suppliers.isEmpty() ? 0 : suppliers.stream().mapToDouble(Supplier::getReliabilityRating).average().orElse(0);
        
        analytics.append(String.format("Total Suppliers: %13d\n", totalSuppliers));
        analytics.append(String.format("Active Suppliers: %12d\n", activeSuppliers));
        analytics.append(String.format("Average Rating: %13.1f/5.0\n\n", avgRating));
        
        // Top performers
        if (!products.isEmpty()) {
            analytics.append("🏆 TOP PERFORMERS\n");
            analytics.append("─".repeat(50)).append("\n");
            
            products.sort((p1, p2) -> Double.compare(p2.getTotalRevenue(), p1.getTotalRevenue()));
            analytics.append("Top 3 Products by Revenue:\n");
            for (int i = 0; i < Math.min(3, products.size()); i++) {
                Product p = products.get(i);
                analytics.append(String.format("%d. %-20s $%.2f\n", i + 1, p.getName(), p.getTotalRevenue()));
            }
        }
        
        analyticsArea.setText(analytics.toString());
    }
    
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(40);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(232, 245, 255));
        table.setSelectionForeground(TEXT_PRIMARY);
        
        // Style header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        header.setPreferredSize(new Dimension(0, 45));
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(200); // Name
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // Category
        table.getColumnModel().getColumn(2).setPreferredWidth(80);  // Quantity
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Price
        table.getColumnModel().getColumn(4).setPreferredWidth(80);  // Sold
        table.getColumnModel().getColumn(5).setPreferredWidth(120); // Revenue
        table.getColumnModel().getColumn(6).setPreferredWidth(120); // Status
        
        // Custom cell renderer for status column
        table.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());
        
        // Center align numeric columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        
        // Right align price and revenue columns
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);
    }
    
    private void styleOrderTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.setRowHeight(35);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(232, 245, 255));
        table.setSelectionForeground(TEXT_PRIMARY);
        
        // Style header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        header.setPreferredSize(new Dimension(0, 40));
        
        // Custom cell renderer for status column
        table.getColumnModel().getColumn(4).setCellRenderer(new OrderStatusCellRenderer());
    }
    
    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        textField.setPreferredSize(new Dimension(200, 30));
    }
    
    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboBox.setPreferredSize(new Dimension(150, 30));
        comboBox.setBackground(Color.WHITE);
    }
    
    private JButton createModernButton(String text, Color bgColor, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorder(new EmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(width, height));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void filterTable() {
        String searchText = searchField.getText().toLowerCase();
        String selectedCategory = (String) categoryFilter.getSelectedItem();
        
        tableModel.setFilter(searchText, selectedCategory);
    }
    
    // Product Management Methods
    private void addProduct() {
        JTextField nameField = new JTextField(15);
        JTextField qtyField = new JTextField(15);
        JTextField thresholdField = new JTextField(15);
        JTextField priceField = new JTextField(15);
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"Electronics", "General", "Clothing", "Books", "Sports"});
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.add(new JLabel("Product Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryCombo);
        panel.add(new JLabel("Quantity:"));
        panel.add(qtyField);
        panel.add(new JLabel("Min Threshold:"));
        panel.add(thresholdField);
        panel.add(new JLabel("Price ($):"));
        panel.add(priceField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Product", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            
                String name = nameField.getText().trim();
                String category = (String) categoryCombo.getSelectedItem();
                int qty = Integer.parseInt(qtyField.getText().trim());
                int threshold = Integer.parseInt(thresholdField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());
                
                if (name.isEmpty() || qty < 0 || threshold < 0 || price < 0) {
                    throw new IllegalArgumentException("Invalid input values");
                }
                
                Product product = new Product(name, qty, threshold, price, category);
                ProductDAO productDAO = new ProductDAO();
                if (productDAO.addProduct(product)) {
                products.add(product);
                updateDisplay();
                        JOptionPane.showMessageDialog(this, "Product added successfully!");
                } else {
                        JOptionPane.showMessageDialog(this, "Error adding product to database!");
                }
        }
    }
    
     private void sellProduct() {
    if (products.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No products available!");
        return;
    }
    
    int selectedRow = productTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a product from the table!");
        return;
    }
    
    int modelRow = productTable.convertRowIndexToModel(selectedRow);
    Product product = tableModel.getProductAt(modelRow);
    
    if (product.getQuantity() == 0) {
        JOptionPane.showMessageDialog(this, product.getName() + " is out of stock!");
        return;
    }
    
    String input = JOptionPane.showInputDialog(this, 
        "Enter quantity to sell for " + product.getName() + 
        "\nCurrent stock: " + product.getQuantity());
    
    if (input != null && !input.trim().isEmpty()) {
        try {
            int sellQty = Integer.parseInt(input.trim());
            
            if (sellQty <= 0 || sellQty > product.getQuantity()) {
                throw new IllegalArgumentException("Invalid quantity");
            }
            
            product.sellProduct(sellQty);
            
            // FIX: Update database after selling
            ProductDAO productDAO = new ProductDAO();
            if (productDAO.updateProduct(product)) {
                updateDisplay();
                
                double revenue = sellQty * product.getPrice();
                JOptionPane.showMessageDialog(this, 
                    String.format("Sold %d units of %s\nRevenue: $%.2f", sellQty, product.getName(), revenue));
                
                if (product.isLowStock()) {
                    JOptionPane.showMessageDialog(this, 
                        "WARNING: " + product.getName() + " is now below minimum threshold!", 
                        "Low Stock Alert", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error updating database!");
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
    
    private void restockProduct() {
    if (products.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No products available!");
        return;
    }
    
    int selectedRow = productTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a product from the table!");
        return;
    }
    
    int modelRow = productTable.convertRowIndexToModel(selectedRow);
    Product product = tableModel.getProductAt(modelRow);
    
    String input = JOptionPane.showInputDialog(this, 
        "Enter quantity to restock for " + product.getName() + 
        "\nCurrent stock: " + product.getQuantity());
    
    if (input != null && !input.trim().isEmpty()) {
        try {
            int restockQty = Integer.parseInt(input.trim());
            
            if (restockQty <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            
            product.restockProduct(restockQty);
            
            // FIX: Update database after restocking
            ProductDAO productDAO = new ProductDAO();
            if (productDAO.updateProduct(product)) {
                updateDisplay();
                
                JOptionPane.showMessageDialog(this, 
                    String.format("Restocked %d units of %s\nNew stock: %d", 
                    restockQty, product.getName(), product.getQuantity()));
            } else {
                JOptionPane.showMessageDialog(this, "Error updating database!");
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}

    
    private void checkAlerts() {
        StringBuilder alerts = new StringBuilder("STOCK ALERTS:\n\n");
        int alertCount = 0;
        
        for (Product product : products) {
            if (product.getQuantity() == 0) {
                alerts.append("🔴 OUT OF STOCK: ").append(product.getName()).append("\n");
                alertCount++;
            } else if (product.isLowStock()) {
                alerts.append("🟡 LOW STOCK: ").append(product.getName())
                       .append(" (").append(product.getQuantity()).append(" units left)\n");
                alertCount++;
            }
        }
        
        if (alertCount == 0) {
            alerts.append("✅ All products are sufficiently stocked!");
        }
        
        JTextArea textArea = new JTextArea(alerts.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Stock Alerts", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void createOrder() {
    if (products.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No products available for ordering!");
        return;
    }
    
    if (suppliers.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No suppliers available! Please add suppliers first.");
        return;
    }
    
    JComboBox<String> productCombo = new JComboBox<>();
    for (Product product : products) {
        productCombo.addItem(product.getName());
    }
    
    JComboBox<String> supplierCombo = new JComboBox<>();
    for (Supplier supplier : suppliers) {
        supplierCombo.addItem(supplier.getName());
    }
    
    JTextField quantityField = new JTextField(15);
    
    JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
    panel.add(new JLabel("Product:"));
    panel.add(productCombo);
    panel.add(new JLabel("Supplier:"));
    panel.add(supplierCombo);
    panel.add(new JLabel("Quantity:"));
    panel.add(quantityField);
    
    int result = JOptionPane.showConfirmDialog(this, panel, "Create New Order", JOptionPane.OK_CANCEL_OPTION);
    
    if (result == JOptionPane.OK_OPTION) {
        try {
            String productName = (String) productCombo.getSelectedItem();
            String supplierName = (String) supplierCombo.getSelectedItem();
            int quantity = Integer.parseInt(quantityField.getText().trim());
            
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            
            Product selectedProduct = products.stream()
                .filter(p -> p.getName().equals(productName))
                .findFirst().orElse(null);
            
            Supplier selectedSupplier = suppliers.stream()
                .filter(s -> s.getName().equals(supplierName))
                .findFirst().orElse(null);
            
            if (selectedProduct != null && selectedSupplier != null) {
                Order order = new Order(selectedProduct, quantity, selectedSupplier);
                
                // FIX: Save order to database
                OrderDAO orderDAO = new OrderDAO();
                if (orderDAO.addOrder(order)) {
                    orders.add(order);
                    selectedSupplier.recordOrder();
                    
                    // FIX: Update supplier in database
                    SupplierDAO supplierDAO = new SupplierDAO();
                    supplierDAO.updateSupplier(selectedSupplier);
                    
                    updateDisplay();
                    refreshSupplierPanel();
                    
                    JOptionPane.showMessageDialog(this, 
                        String.format("Order created successfully!\nOrder ID: %d\nTotal Cost: $%.2f", 
                        order.getOrderId(), order.getTotalCost()));
                } else {
                    JOptionPane.showMessageDialog(this, "Error saving order to database!");
                }
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
    
    private void confirmOrder() {
    int selectedRow = orderTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select an order from the table!");
        return;
    }
    
    Order order = orders.get(selectedRow);
    if (order.getStatus() == Order.OrderStatus.PENDING) {
    order.confirmOrder();
    
    // ADD THIS:
    OrderDAO orderDAO = new OrderDAO();
    if (orderDAO.updateOrder(order)) {
        updateDisplay();
        JOptionPane.showMessageDialog(this, "Order #" + order.getOrderId() + " has been confirmed!");
    } else {
        JOptionPane.showMessageDialog(this, "Error updating database!");
    }
}
}
    
    private void cancelOrder() {
    int selectedRow = orderTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select an order from the table!");
        return;
    }
    
    Order order = orders.get(selectedRow);
    int result = JOptionPane.showConfirmDialog(this, 
        "Are you sure you want to cancel Order #" + order.getOrderId() + "?",
        "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
    
    if (result == JOptionPane.YES_OPTION) {
    order.cancelOrder();
    
    // ADD THIS:
    OrderDAO orderDAO = new OrderDAO();
    if (orderDAO.updateOrder(order)) {
        updateDisplay();
        JOptionPane.showMessageDialog(this, "Order #" + order.getOrderId() + " has been cancelled!");
    } else {
        JOptionPane.showMessageDialog(this, "Error updating database!");
    }
}
}
    
    private void deliverOrder() {
    int selectedRow = orderTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select an order from the table!");
        return;
    }
    
    Order order = orders.get(selectedRow);
    if (order.getStatus() == Order.OrderStatus.SHIPPED) {
        order.deliverOrder();
        order.getSupplier().recordOnTimeDelivery();
        
        // FIX: Update all three entities in database
        OrderDAO orderDAO = new OrderDAO();
        ProductDAO productDAO = new ProductDAO();
        SupplierDAO supplierDAO = new SupplierDAO();
        
        boolean orderUpdated = orderDAO.updateOrder(order);
        boolean productUpdated = productDAO.updateProduct(order.getProduct());
        boolean supplierUpdated = supplierDAO.updateSupplier(order.getSupplier());
        
        if (orderUpdated && productUpdated && supplierUpdated) {
            updateDisplay();
            refreshSupplierPanel();
            JOptionPane.showMessageDialog(this, 
                String.format("Order #%d delivered successfully!\n%s restocked with %d units.", 
                order.getOrderId(), order.getProduct().getName(), order.getQuantityOrdered()));
        } else {
            JOptionPane.showMessageDialog(this, "Error updating database!");
        }
    } else {
        JOptionPane.showMessageDialog(this, "Only shipped orders can be marked as delivered.");
    }
}
    
    private void addSupplier() {
    JTextField nameField = new JTextField(15);
    JTextField phoneField = new JTextField(15);
    JTextField emailField = new JTextField(15);
    JTextField addressField = new JTextField(15);
    
    JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
    panel.add(new JLabel("Supplier Name:"));
    panel.add(nameField);
    panel.add(new JLabel("Phone:"));
    panel.add(phoneField);
    panel.add(new JLabel("Email:"));
    panel.add(emailField);
    panel.add(new JLabel("Address:"));
    panel.add(addressField);
    
    int result = JOptionPane.showConfirmDialog(this, panel, "Add New Supplier", JOptionPane.OK_CANCEL_OPTION);
    
    if (result == JOptionPane.OK_OPTION) {
        try {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String address = addressField.getText().trim();
            
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Supplier name is required");
            }
            
            Supplier supplier = new Supplier(name, phone, email, address);
            
            // FIX: Save supplier to database
            SupplierDAO supplierDAO = new SupplierDAO();
            if (supplierDAO.addSupplier(supplier)) {
                suppliers.add(supplier);
                updateDisplay();
                refreshSupplierPanel(); // Refresh the supplier UI
                
                JOptionPane.showMessageDialog(this, "Supplier added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Error saving supplier to database!");
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
    
    private void editSupplier() {
    if (suppliers.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No suppliers available!");
        return;
    }
    
    // Select supplier to edit
    String[] supplierNames = suppliers.stream().map(Supplier::getName).toArray(String[]::new);
    String selectedName = (String) JOptionPane.showInputDialog(this,
        "Select supplier to edit:", "Edit Supplier",
        JOptionPane.QUESTION_MESSAGE, null, supplierNames, supplierNames[0]);
    
    if (selectedName != null) {
        Supplier supplier = suppliers.stream()
            .filter(s -> s.getName().equals(selectedName))
            .findFirst().orElse(null);
        
        if (supplier != null) {
            // Create edit dialog with current values
            JTextField phoneField = new JTextField(supplier.getPhone(), 15);
            JTextField emailField = new JTextField(supplier.getEmail(), 15);
            JTextField addressField = new JTextField(supplier.getAddress(), 15);
            JCheckBox activeCheckbox = new JCheckBox("Active", supplier.isActive());
            
            JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
            panel.add(new JLabel("Phone:"));
            panel.add(phoneField);
            panel.add(new JLabel("Email:"));
            panel.add(emailField);
            panel.add(new JLabel("Address:"));
            panel.add(addressField);
            panel.add(new JLabel("Status:"));
            panel.add(activeCheckbox);
            
            int result = JOptionPane.showConfirmDialog(this, panel, 
                "Edit Supplier: " + selectedName, JOptionPane.OK_CANCEL_OPTION);
            
            if (result == JOptionPane.OK_OPTION) {
                try {
                    supplier.setPhone(phoneField.getText().trim());
                    supplier.setEmail(emailField.getText().trim());
                    supplier.setAddress(addressField.getText().trim());
                    supplier.setActive(activeCheckbox.isSelected());
                    
                    // Update database
                    SupplierDAO supplierDAO = new SupplierDAO();
                    if (supplierDAO.updateSupplier(supplier)) {
                        updateDisplay();
                        refreshSupplierPanel();
                        JOptionPane.showMessageDialog(this, "Supplier updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Error updating supplier in database!");
                    }
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), 
                        "Update Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
    
    private void viewSupplierDetails() {
        if (suppliers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No suppliers available!");
            return;
        }
        
        String[] supplierNames = suppliers.stream().map(Supplier::getName).toArray(String[]::new);
        String selectedName = (String) JOptionPane.showInputDialog(this,
            "Select supplier to view details:", "Supplier Details",
            JOptionPane.QUESTION_MESSAGE, null, supplierNames, supplierNames[0]);
        
        if (selectedName != null) {
            Supplier supplier = suppliers.stream()
                .filter(s -> s.getName().equals(selectedName))
                .findFirst().orElse(null);
            
            if (supplier != null) {
                JTextArea detailsArea = new JTextArea(supplier.getDetailedInfo());
                detailsArea.setEditable(false);
                detailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                
                JScrollPane scrollPane = new JScrollPane(detailsArea);
                scrollPane.setPreferredSize(new Dimension(500, 400));
                
                JOptionPane.showMessageDialog(this, scrollPane, "Supplier Details", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void logout() {
        int result = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Confirm Logout", 
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            dispose();
            new LoginPage();
        }
    }
    
    private void addSampleData() {
        // Load data from database instead
        ProductDAO productDAO = new ProductDAO();
        SupplierDAO supplierDAO = new SupplierDAO();
        OrderDAO orderDAO = new OrderDAO();
    
        products = (ArrayList<Product>) productDAO.getAllProducts();
        suppliers = (ArrayList<Supplier>) supplierDAO.getAllSuppliers();
        orders = (ArrayList<Order>) orderDAO.getAllOrders();
    
        // If database is empty, add sample data
        if (products.isEmpty()) {
            addInitialSampleData();
        }
    }

    private void addInitialSampleData() {
        ProductDAO productDAO = new ProductDAO();
    
        // Add sample products
        Product p1 = new Product("MacBook Pro", 15, 5, 2499.99, "Electronics");
        Product p2 = new Product("Wireless Mouse", 50, 10, 79.99, "Electronics");
    
        productDAO.addProduct(p1);
        productDAO.addProduct(p2);
    
        products.add(p1);
        products.add(p2);
    
    // Similar for suppliers and orders...
    }
    
    private void updateDisplay() {
        tableModel.fireTableDataChanged();
        orderTableModel.fireTableDataChanged();
        updateStatusPanel();
        
        // Update analytics if it's the current tab
        if (tabbedPane.getSelectedIndex() == 3) {
            JPanel analyticsPanel = (JPanel) tabbedPane.getComponentAt(3);
            JScrollPane scrollPane = (JScrollPane) analyticsPanel.getComponent(1);
            JTextArea analyticsArea = (JTextArea) scrollPane.getViewport().getView();
            updateAnalyticsDisplay(analyticsArea);
        }
    }
    private void refreshSupplierPanel() {
    // Get the suppliers tab (index 2)
    if (tabbedPane.getComponentCount() > 2) {
        JPanel suppliersPanel = (JPanel) tabbedPane.getComponentAt(2);
        suppliersPanel.removeAll();
        suppliersPanel.setLayout(new BorderLayout(10, 10));
        suppliersPanel.setBackground(BACKGROUND_COLOR);
        suppliersPanel.add(createSupplierControlPanel(), BorderLayout.NORTH);
        suppliersPanel.add(createSupplierInfoPanel(), BorderLayout.CENTER);
        suppliersPanel.revalidate();
        suppliersPanel.repaint();
    }
}
    
    private void updateStatusPanel() {
        int totalProducts = products.size();
        int totalStock = products.stream().mapToInt(Product::getQuantity).sum();
        long lowStockCount = products.stream().filter(Product::isLowStock).count();
        double totalRevenue = products.stream().mapToDouble(Product::getTotalRevenue).sum();
        int pendingOrders = (int) orders.stream().filter(o -> o.getStatus() == Order.OrderStatus.PENDING).count();
        
        statsLabel.setText(String.format("Products: %d | Stock: %d | Revenue: $%.2f | Pending Orders: %d",
            totalProducts, totalStock, totalRevenue, pendingOrders));
        
        // Update alert label
        if (lowStockCount > 0) {
            alertLabel.setText(String.format("⚠️ %d Low Stock Alert%s",
                lowStockCount, lowStockCount > 1 ? "s" : ""));
            alertLabel.setForeground(WARNING_COLOR);
        } else {
            alertLabel.setText("✅ All products adequately stocked");
            alertLabel.setForeground(SECONDARY_COLOR);
        }
        
        // Update progress bar
        int maxStock = products.stream().mapToInt(p -> p.getQuantity() + p.getMinThreshold()).sum();
        int currentStock = products.stream().mapToInt(Product::getQuantity).sum();
        
        if (maxStock > 0) {
            int percentage = (int) ((double) currentStock / maxStock * 100);
            stockProgressBar.setValue(Math.min(percentage, 100));
            
            if (percentage < 30) {
                stockProgressBar.setForeground(DANGER_COLOR);
            } else if (percentage < 70) {
                stockProgressBar.setForeground(WARNING_COLOR);
            } else {
                stockProgressBar.setForeground(SECONDARY_COLOR);
            }
        }
    }
    
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(CARD_COLOR);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        // Left side - Statistics
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        statsPanel.setOpaque(false);
        
        statsLabel = new JLabel();
        statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statsLabel.setForeground(TEXT_SECONDARY);
        
        alertLabel = new JLabel();
        alertLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        statsPanel.add(statsLabel);
        statsPanel.add(alertLabel);
        
        // Right side - Progress indicator
        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        progressPanel.setOpaque(false);
        
        JLabel progressLabel = new JLabel("Stock Level:");
        progressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        progressLabel.setForeground(TEXT_SECONDARY);
        
        stockProgressBar = new JProgressBar(0, 100);
        stockProgressBar.setPreferredSize(new Dimension(200, 8));
        stockProgressBar.setStringPainted(false);
        stockProgressBar.setBackground(new Color(240, 240, 240));
        stockProgressBar.setForeground(SECONDARY_COLOR);
        
        progressPanel.add(progressLabel);
        progressPanel.add(Box.createHorizontalStrut(10));
        progressPanel.add(stockProgressBar);
        
        statusPanel.add(statsPanel, BorderLayout.WEST);
        statusPanel.add(progressPanel, BorderLayout.EAST);
        
        return statusPanel;
    }
    
    // Custom table models and renderers
    private class ProductTableModel extends AbstractTableModel {
        private final String[] columnNames = {
            "Product Name", "Category", "Quantity", "Price", "Sold", "Revenue", "Status"
        };
        private ArrayList<Product> filteredProducts = new ArrayList<>();
        
        public ProductTableModel() {
            filteredProducts.addAll(products);
        }
        
        @Override
        public int getRowCount() {
            return filteredProducts.size();
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Product product = filteredProducts.get(rowIndex);
            
            switch (columnIndex) {
                case 0: return product.getName();
                case 1: return product.getCategory();
                case 2: return product.getQuantity();
                case 3: return String.format("$%.2f", product.getPrice());
                case 4: return product.getSoldCount();
                case 5: return String.format("$%.2f", product.getTotalRevenue());
                case 6: return product.getStockStatus();
                default: return null;
            }
        }
        
        public Product getProductAt(int rowIndex) {
            return filteredProducts.get(rowIndex);
        }
        
        public void setFilter(String searchText, String category) {
            filteredProducts.clear();
            
            for (Product product : products) {
                boolean matchesSearch = searchText.isEmpty() ||
                    product.getName().toLowerCase().contains(searchText);
                boolean matchesCategory = "All Categories".equals(category) ||
                    product.getCategory().equals(category);
                
                if (matchesSearch && matchesCategory) {
                    filteredProducts.add(product);
                }
            }
            
            fireTableDataChanged();
        }
        
        @Override
        public void fireTableDataChanged() {
            if (filteredProducts.isEmpty()) {
                filteredProducts.addAll(products);
            }
            super.fireTableDataChanged();
        }
    }
    
    private class OrderTableModel extends AbstractTableModel {
        private final String[] columnNames = {
            "Order ID", "Product", "Quantity", "Supplier", "Status", "Order Date", "Expected Delivery", "Total Cost"
        };
        
        @Override
        public int getRowCount() {
            return orders.size();
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Order order = orders.get(rowIndex);
            
            switch (columnIndex) {
                case 0: return order.getOrderId();
                case 1: return order.getProduct().getName();
                case 2: return order.getQuantityOrdered();
                case 3: return order.getSupplier().getName();
                case 4: return order.getStatus().toString();
                case 5: return order.getFormattedOrderDate();
                case 6: return order.getFormattedDeliveryDate();
                case 7: return String.format("$%.2f", order.getTotalCost());
                default: return null;
            }
        }
    }
    
    // Custom cell renderer for status column
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            String status = (String) value;
            setHorizontalAlignment(CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            
            if (!isSelected) {
                switch (status) {
                    case "OUT OF STOCK":
                        setBackground(new Color(255, 245, 245));
                        setForeground(DANGER_COLOR);
                        break;
                    case "LOW STOCK":
                        setBackground(new Color(255, 248, 225));
                        setForeground(WARNING_COLOR);
                        break;
                    case "IN STOCK":
                        setBackground(new Color(245, 255, 245));
                        setForeground(SECONDARY_COLOR);
                        break;
                    default:
                        setBackground(Color.WHITE);
                        setForeground(TEXT_PRIMARY);
                        break;
                }
            }
            
            return this;
        }
    }
    
    private class OrderStatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            String status = (String) value;
            setHorizontalAlignment(CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 10));
            
            if (!isSelected) {
                switch (status) {
                    case "PENDING":
                        setBackground(new Color(255, 248, 225));
                        setForeground(WARNING_COLOR);
                        break;
                    case "CONFIRMED":
                        setBackground(new Color(232, 245, 255));
                        setForeground(PRIMARY_COLOR);
                        break;
                    case "SHIPPED":
                        setBackground(new Color(245, 255, 245));
                        setForeground(SECONDARY_COLOR);
                        break;
                    case "DELIVERED":
                        setBackground(new Color(240, 255, 240));
                        setForeground(new Color(46, 125, 50));
                        break;
                    case "CANCELLED":
                        setBackground(new Color(255, 245, 245));
                        setForeground(DANGER_COLOR);
                        break;
                    default:
                        setBackground(Color.WHITE);
                        setForeground(TEXT_PRIMARY);
                        break;
                }
            }
            
            return this;
        }
    }
}