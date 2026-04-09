package Bank;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class UserHome extends JFrame {

    //  MODERN COLOR PALETTE
    private static final Color PRIMARY = new Color(220, 53, 69);
    private static final Color PRIMARY_DARK = new Color(185, 28, 44);
    private static final Color SECONDARY = new Color(59, 130, 246);
    private static final Color DARK = new Color(15, 23, 42);
    private static final Color LIGHT_BG = new Color(248, 250, 252);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color FOOTER_BG = new Color(15, 23, 42);
    private static final Color ALERT_YELLOW = new Color(254, 243, 199);
    private static final Color ALERT_BORDER = new Color(252, 211, 77);
    private static final Color SUCCESS = new Color(34, 197, 94);
    private static final Color WARNING = new Color(251, 146, 60);
    private static final Color BORDER_LIGHT = new Color(226, 232, 240);

    public UserHome() {
        setTitle("Blood Bank Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        setMinimumSize(new Dimension(1200, 800));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(LIGHT_BG);

        // Top panel with navbar and alerts
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(createModernNavbar());
        
        JPanel alertBanner = createStockAlertBanner();
        if (alertBanner != null) {
            topPanel.add(alertBanner);
        }

        root.add(topPanel, BorderLayout.NORTH);
        root.add(createMainContent(), BorderLayout.CENTER);

        add(root);
    }

    // MODERN NAVBAR 
    private JPanel createModernNavbar() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(Color.WHITE);
        nav.setBorder(new EmptyBorder(18, 70, 18, 70));

        // Logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoPanel.setOpaque(false);
        
        JLabel logoIcon = new JLabel("❤");
        logoIcon.setFont(new Font("SansSerif", Font.BOLD, 26));
        logoIcon.setForeground(PRIMARY);
        
        JLabel logo = new JLabel("Blood Bank");
        logo.setFont(new Font("SansSerif", Font.BOLD, 24));
        logo.setForeground(DARK);
        
        logoPanel.add(logoIcon);
        logoPanel.add(logo);

        // Navigation menu
        JPanel menu = new JPanel(new FlowLayout(FlowLayout.CENTER, 32, 0));
        menu.setOpaque(false);
        
        menu.add(createNavLink("Home", true, null));
        menu.add(createNavLink("Stock Blood", false, "StockBloodPage"));
        menu.add(createNavLink("About Us", false, "AboutUsPage"));
        menu.add(createNavLink("Request Blood", false, "BloodRequestPage"));
        menu.add(createNavLink("Become a Donor", false, "RegisterPage"));
        menu.add(createNavLink("Contact Us", false, "ContactUsPage"));
        menu.add(createNavLink("Blood Centres", false, "BloodBankCentrePage"));


        // Auth buttons
        JPanel auth = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        auth.setOpaque(false);

        JButton login = createModernButton("Login", false);
        login.addActionListener(e -> {
            new LoginPage().setVisible(true);
            dispose();
        });

        JButton register = createModernButton("Register", true);
        register.addActionListener(e -> {
            new RegisterPage().setVisible(true);
            dispose();
        });

        auth.add(login);
        auth.add(register);

        nav.add(logoPanel, BorderLayout.WEST);
        nav.add(menu, BorderLayout.CENTER);
        nav.add(auth, BorderLayout.EAST);
        
        wrapper.add(nav, BorderLayout.CENTER);
        wrapper.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_LIGHT));
        
        return wrapper;
    }

    private JLabel createNavLink(String text, boolean isActive, String targetPage) {
        JLabel link = new JLabel(text);
        link.setFont(new Font("SansSerif", Font.BOLD, 14));
        link.setForeground(isActive ? PRIMARY : MUTED);
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (!isActive && targetPage != null) {
            link.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { link.setForeground(PRIMARY); }
                public void mouseExited(MouseEvent e) { link.setForeground(MUTED); }
                public void mouseClicked(MouseEvent e) {
                    try {
                        Class<?> pageClass = Class.forName("Bank." + targetPage);
                        JFrame frame = (JFrame) pageClass.getDeclaredConstructor().newInstance();
                        frame.setVisible(true);
                        dispose();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
        return link;
    }

    // ENHANCED ALERT BANNER 
    private JPanel createStockAlertBanner() {
        ArrayList<String> lowStock = new ArrayList<>();
        try (Connection con = DBConnection.connect();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT blood_group, total_units FROM blood_stock_summary WHERE total_units < 10")) {
            while (rs.next()) {
                lowStock.add(rs.getString("blood_group") + " (" + rs.getInt("total_units") + " units)");
            }
        } catch (Exception e) {
            return null; 
        }

        if (lowStock.isEmpty()) return null;

        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(ALERT_YELLOW);
        banner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ALERT_BORDER),
            new EmptyBorder(12, 70, 12, 70)
        ));
        
        JPanel content = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        content.setOpaque(false);
        
        JLabel warnIcon = new JLabel("⚠️");
        warnIcon.setFont(new Font("SansSerif", Font.PLAIN, 16));
        
        JLabel warnLabel = new JLabel("LOW STOCK ALERT:");
        warnLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        warnLabel.setForeground(new Color(146, 64, 14));
        
        content.add(warnIcon);
        content.add(warnLabel);

        for (int i = 0; i < lowStock.size(); i++) {
            JLabel item = new JLabel(lowStock.get(i));
            item.setFont(new Font("SansSerif", Font.BOLD, 13));
            item.setForeground(PRIMARY);
            content.add(item);
            
            if (i < lowStock.size() - 1) {
                JLabel separator = new JLabel("•");
                separator.setForeground(MUTED);
                content.add(separator);
            }
        }
        
        banner.add(content, BorderLayout.CENTER);
        
        return banner;
    }

    //  DATABASE LOGIC 
    private int fetchCount(String query) {
        int count = 0;
        try (Connection con = DBConnection.connect();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    private JScrollPane createMainContent() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(LIGHT_BG);

        content.add(createModernHero());
        content.add(createBloodGroupAvailability());
        content.add(createImpactStats());
        content.add(createEmergencyBanner());
        content.add(createFooter()); 

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.getHorizontalScrollBar().setUnitIncrement(20);
        return scroll;
    }

    // MODERN HERO SECTION 
    private JPanel createModernHero() {
        JPanel hero = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Subtle gradient background
                GradientPaint gp = new GradientPaint(
                    0, 0, Color.WHITE,
                    getWidth(), getHeight(), new Color(254, 242, 242)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        hero.setBorder(new EmptyBorder(100, 90, 80, 90));
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel title = new JLabel(
            "<html><span style='font-size:58px; font-weight:900; line-height:1.2;'>" +
            "Donate Blood,<br>" +
            "<span style='color:#dc3545'>Save Lives</span></span></html>"
        );
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(
            "<html><span style='font-size:17px; line-height:1.6;'>" +
            "A digital platform connecting donors, hospitals, and patients efficiently.</span></html>"
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 17));
        subtitle.setForeground(MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel ctaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        ctaPanel.setOpaque(false);
        ctaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton primaryCta = createModernButton("Request Blood", true);
        primaryCta.addActionListener(e -> {
            new BloodRequestPage().setVisible(true);
            dispose();
        });
        
        JButton secondaryCta = createModernButton("Become a Donor", false);
        secondaryCta.addActionListener(e -> {
            new RegisterPage().setVisible(true);
            dispose();
        });

        ctaPanel.add(primaryCta);
        ctaPanel.add(secondaryCta);

        left.add(title);
        left.add(Box.createVerticalStrut(20));
        left.add(subtitle);
        left.add(Box.createVerticalStrut(35));
        left.add(ctaPanel);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.WEST;
        hero.add(left, gbc);

        // Hero image
        try {
            String projectPath = System.getProperty("user.dir");
            File imgFile = new File(projectPath + "/images/ind.png");
            if (!imgFile.exists()) imgFile = new File(projectPath + "/BBMS/images/ind.png");
            
            if (imgFile.exists()) {
                ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
                Image img = icon.getImage().getScaledInstance(500, 350, Image.SCALE_SMOOTH);
                JLabel imgLabel = new JLabel(new ImageIcon(img));
                gbc.gridx = 1; gbc.weightx = 0.5;
                gbc.insets = new Insets(0, 50, 0, 0);
                hero.add(imgLabel, gbc);
            }
        } catch (Exception e) { 
            e.printStackTrace();
        }

        return hero;
    }

    //  BLOOD GROUP AVAILABILITY
    private JPanel createBloodGroupAvailability() {
        JPanel section = new JPanel(new BorderLayout());
        section.setOpaque(false);
        section.setBorder(new EmptyBorder(50, 90, 60, 90));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        
        JLabel title = new JLabel("Blood Stock Availability");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel("Real-time inventory across all blood groups");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitle.setForeground(MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        header.add(title);
        header.add(Box.createVerticalStrut(8));
        header.add(subtitle);

        JPanel grid = new JPanel(new GridLayout(2, 4, 20, 20));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(30, 0, 0, 0));

        String[] bloodGroups = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
        
        for (String group : bloodGroups) {
            int units = fetchBloodGroupStock(group);
            grid.add(createBloodGroupCard(group, units));
        }

        section.add(header, BorderLayout.NORTH);
        section.add(grid, BorderLayout.CENTER);
        return section;
    }

    private int fetchBloodGroupStock(String bloodGroup) {
        try (Connection con = DBConnection.connect();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT total_units FROM blood_stock_summary WHERE blood_group = ?")) {
            ps.setString(1, bloodGroup);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total_units");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private JPanel createBloodGroupCard(String group, int units) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
        };
        card.setLayout(new BorderLayout(0, 12));
        card.setBackground(CARD_BG);
        
        Color statusColor = units < 5 ? PRIMARY : (units < 15 ? WARNING : SUCCESS);
        String status = units < 5 ? "Critical" : (units < 15 ? "Low Stock" : "Available");
        
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(statusColor, 2),
            new EmptyBorder(22, 18, 22, 18)
        ));

        JLabel groupLabel = new JLabel(group);
        groupLabel.setFont(new Font("SansSerif", Font.BOLD, 38));
        groupLabel.setForeground(DARK);
        groupLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setOpaque(false);

        JLabel unitsLabel = new JLabel(units + " Units");
        unitsLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        unitsLabel.setForeground(statusColor);
        unitsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel statusLabel = new JLabel(status);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setForeground(MUTED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        bottom.add(unitsLabel);
        bottom.add(Box.createVerticalStrut(4));
        bottom.add(statusLabel);

        card.add(groupLabel, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }

    //  IMPACT STATISTICS
    private JPanel createImpactStats() {
        JPanel section = new JPanel(new BorderLayout());
        section.setOpaque(false);
        section.setBorder(new EmptyBorder(40, 90, 60, 90));

        JPanel stats = new JPanel(new GridLayout(1, 3, 35, 0));
        stats.setOpaque(false);

        int unitsFulfilled = fetchCount("SELECT COALESCE(SUM(units_requested), 0) FROM blood_requests WHERE status='Approved'");
        int activeDonors = fetchCount("SELECT COUNT(*) FROM users WHERE role='User'");
        int partnerHospitals = fetchCount("SELECT COUNT(DISTINCT hospital_name) FROM blood_requests");

        stats.add(createStatCard(unitsFulfilled > 0 ? unitsFulfilled : 120, "Units Fulfilled", PRIMARY));
        stats.add(createStatCard(activeDonors > 0 ? activeDonors : 50, "Active Donors", SUCCESS));
        stats.add(createStatCard(partnerHospitals > 0 ? partnerHospitals : 15, "Partner Hospitals", SECONDARY));

        section.add(stats, BorderLayout.CENTER);
        return section;
    }

    private JPanel createStatCard(int value, String label, Color color) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(new EmptyBorder(35, 28, 35, 28));

        JLabel v = new JLabel("0", SwingConstants.CENTER);
        v.setFont(new Font("SansSerif", Font.BOLD, 40));
        v.setForeground(color);
        v.setAlignmentX(Component.CENTER_ALIGNMENT);
        animateCounter(v, value, "+");

        JLabel l = new JLabel(label, SwingConstants.CENTER);
        l.setFont(new Font("SansSerif", Font.BOLD, 14));
        l.setForeground(MUTED);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(v);
        card.add(Box.createVerticalStrut(10));
        card.add(l);

        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(249, 250, 251));
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(CARD_BG);
            }
        });

        return card;
    }

    // EMERGENCY BANNER 
    private JPanel createEmergencyBanner() {
        JPanel banner = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                    0, 0, PRIMARY,
                    getWidth(), 0, PRIMARY_DARK
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        banner.setBorder(new EmptyBorder(50, 90, 50, 90));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel title = new JLabel("Emergency Blood Needed?");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("We're here to help. Request blood instantly.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitle.setForeground(new Color(255, 255, 255, 200));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        left.add(title);
        left.add(Box.createVerticalStrut(8));
        left.add(subtitle);

        JButton btn = new JButton("Request Now");
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setBackground(Color.WHITE);
        btn.setForeground(PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(13, 32, 13, 32));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(248, 250, 252));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
            }
        });
        
        btn.addActionListener(e -> {
            new BloodRequestPage().setVisible(true);
            dispose();
        });

        banner.add(left, BorderLayout.WEST);
        banner.add(btn, BorderLayout.EAST);
        return banner;
    }

    

    // MODERN FOOTER
private JPanel createFooter() {
    JPanel footer = new JPanel(new BorderLayout());
    footer.setBackground(FOOTER_BG);
    footer.setBorder(new EmptyBorder(20, 40, 20, 40)); // slimmer, modern padding

    JLabel copy = new JLabel("© 2025 LifeLine Blood Bank Management System");
    copy.setFont(new Font("SansSerif", Font.PLAIN, 13));
    copy.setForeground(new Color(148, 163, 184));

    JButton adminBtn = new JButton("Admin Portal");
    adminBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
    adminBtn.setForeground(new Color(148, 163, 184));
    adminBtn.setContentAreaFilled(false);
    adminBtn.setBorderPainted(false);
    adminBtn.setFocusPainted(false);
    adminBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

    // Hover effect
    adminBtn.addMouseListener(new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
            adminBtn.setForeground(Color.WHITE);
        }
        public void mouseExited(MouseEvent e) {
            adminBtn.setForeground(new Color(148, 163, 184));
        }
    });

    // Open AdminLogin full screen
    adminBtn.addActionListener(e -> {
        try {
            AdminLogin loginFrame = new AdminLogin();
            loginFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // full screen
            loginFrame.setLocationRelativeTo(null);              // center if not maximized
            loginFrame.setVisible(true);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error launching Admin Portal: " + ex.getMessage());
        }
    });

    footer.add(copy, BorderLayout.WEST);
    footer.add(adminBtn, BorderLayout.EAST);
    return footer;
}

// UTILITIES
private JButton createModernButton(String text, boolean isPrimary) {
    JButton btn = new JButton(text);
    btn.setFont(new Font("SansSerif", Font.BOLD, 14));
    btn.setFocusPainted(false);
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

    if (isPrimary) {
        btn.setBackground(PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setBorder(new EmptyBorder(12, 28, 12, 28));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(PRIMARY_DARK);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(PRIMARY);
            }
        });
    } else {
        btn.setContentAreaFilled(false);
        btn.setForeground(PRIMARY);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY, 2),
            new EmptyBorder(10, 26, 10, 26)
        ));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(254, 226, 226));
                btn.setOpaque(true);
            }
            public void mouseExited(MouseEvent e) {
                btn.setContentAreaFilled(false);
                btn.setOpaque(false);
            }
        });
    }

    return btn;
}

// COUNTER ANIMATION
private void animateCounter(JLabel label, int target, String suffix) {
    Timer timer = new Timer(15, null);
    timer.addActionListener(new ActionListener() {
        int count = 0;
        public void actionPerformed(ActionEvent e) {
            if (target <= 0) {
                label.setText("0" + suffix);
                timer.stop();
                return;
            }
            count += Math.max(1, target / 60);
            if (count >= target) {
                label.setText(target + suffix);
                timer.stop();
            } else {
                label.setText(count + suffix);
            }
        }
    });
    timer.start();
}

// MAIN ENTRY
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        UserHome home = new UserHome();
        home.setExtendedState(JFrame.MAXIMIZED_BOTH); // ensure full screen 
        home.setLocationRelativeTo(null);
        home.setVisible(true);
    });
}
}