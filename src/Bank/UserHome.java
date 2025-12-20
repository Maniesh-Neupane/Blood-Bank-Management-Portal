package Bank;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class UserHome extends JFrame {

    // ================= COLORS =================
    private static final Color PRIMARY = new Color(220, 53, 69);
    private static final Color DARK = new Color(33, 37, 41);
    private static final Color LIGHT_BG = new Color(245, 247, 250);
    private static final Color MUTED = new Color(108, 117, 125);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color FOOTER_BG = new Color(18, 18, 18);
    private static final Color ALERT_YELLOW = new Color(255, 243, 205);
    private static final Color ALERT_BORDER = new Color(255, 238, 186);

    public UserHome() {
        setTitle("  Blood Bank Management System ");
        setSize(1400, 900);
        setMinimumSize(new Dimension(1200, 800));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(LIGHT_BG);

        // Grouping Navbar and Alert System at the Top
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(createNavbar());
        
        JPanel alertBanner = createStockAlertBanner();
        if (alertBanner != null) {
            topPanel.add(alertBanner);
        }

        root.add(topPanel, BorderLayout.NORTH);
        root.add(createMainContent(), BorderLayout.CENTER);

        add(root);
    }

    // ================= NEW: TRIGGERED ALERT SYSTEM =================
    private JPanel createStockAlertBanner() {
        ArrayList<String> lowStock = new ArrayList<>();
        try (Connection con = DBConnection.connect();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT blood_group, total_units FROM blood_stock_summary WHERE total_units < 10")) {
            while (rs.next()) {
                lowStock.add(rs.getString("blood_group") + " (" + rs.getInt("total_units") + " units)");
            }
        } catch (Exception e) {
            return null; // Silent return if table doesn't exist or DB is off
        }

        if (lowStock.isEmpty()) return null;

        JPanel banner = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        banner.setBackground(ALERT_YELLOW);
        banner.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ALERT_BORDER));
        
        JLabel warnLabel = new JLabel("⚠️ LOW STOCK ALERT: ");
        warnLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        warnLabel.setForeground(new Color(133, 100, 4));
        banner.add(warnLabel);

        for (String msg : lowStock) {
            JLabel item = new JLabel(msg);
            item.setFont(new Font("SansSerif", Font.BOLD, 13));
            item.setForeground(PRIMARY);
            banner.add(item);
        }
        return banner;
    }

    // ================= DATABASE LOGIC =================
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

    // ================= NAVBAR (FIXED LINKS) =================
    private JPanel createNavbar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(Color.WHITE);
        nav.setBorder(new EmptyBorder(18, 70, 18, 70));

        JLabel logo = new JLabel("LifeLine Blood Bank");
        logo.setFont(new Font("SansSerif", Font.BOLD, 26));
        logo.setForeground(DARK);

        JPanel menu = new JPanel(new FlowLayout(FlowLayout.CENTER, 34, 0));
        menu.setOpaque(false);
        
        JLabel homeLink = navLink("Home");
        menu.add(homeLink);

        JLabel aboutLink = navLink("About Us");
        aboutLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new AboutUsPage().setVisible(true);
                dispose();
            }
        });
        menu.add(aboutLink);
        
        JLabel reqLink = navLink("Request Blood");
        reqLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new BloodRequestPage().setVisible(true);
                dispose();
            }
        });
        menu.add(reqLink);

        JLabel donorLink = navLink("Become a Donor");
        donorLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new RegisterPage().setVisible(true);
                dispose();
            }
        });
        menu.add(donorLink);
        
        JLabel contactLink = navLink("Contact Us");
        contactLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new ContactUsPage().setVisible(true);
                dispose();
            }
        });
        menu.add(contactLink);

        JPanel auth = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        auth.setOpaque(false);

        JButton login = new JButton("Login");
        styleOutlineButton(login);
        addHover(login);
        login.addActionListener(e -> {
            new LoginPage().setVisible(true);
            dispose();
        });

        JButton register = new JButton("Register");
        stylePrimaryButton(register);
        addHover(register);
        register.addActionListener(e -> {
            new RegisterPage().setVisible(true);
            dispose();
        });

        auth.add(login);
        auth.add(register);

        nav.add(logo, BorderLayout.WEST);
        nav.add(menu, BorderLayout.CENTER);
        nav.add(auth, BorderLayout.EAST);

        return nav;
    }

    private JScrollPane createMainContent() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(LIGHT_BG);

        content.add(createHero());
        content.add(createWhyUs());
        content.add(createWhatWeDo());
        content.add(createStats()); 
        content.add(createEmergencyBanner());
        content.add(createFooter()); 

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        return scroll;
    }

    private JPanel createHero() {
        JPanel hero = new JPanel(new GridBagLayout());
        hero.setBackground(LIGHT_BG);
        hero.setBorder(new EmptyBorder(90, 90, 70, 90));
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel title = new JLabel(
                "<html><span style='font-size:62px;'>Donate Blood,<br>"
                        + "<span style='color:#dc3545'>Save Lives ! </span></span></html>"
        );

        JLabel subtitle = new JLabel("A digital platform connecting donors, hospitals, and patients efficiently.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subtitle.setForeground(MUTED);

        JButton cta = new JButton("Request Blood");
        stylePrimaryButton(cta);
        addHover(cta);
        
        cta.addActionListener(e -> {
            new BloodRequestPage().setVisible(true);
            dispose();
        });

        left.add(title);
        left.add(Box.createVerticalStrut(20));
        left.add(subtitle);
        left.add(Box.createVerticalStrut(40));
        left.add(cta);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.WEST;
        hero.add(left, gbc);

        try {
            String projectPath = System.getProperty("user.dir");
            File imgFile = new File(projectPath + "/images/ind.png");
            if (!imgFile.exists()) imgFile = new File(projectPath + "/BBMS/images/ind.png");
            
            if (imgFile.exists()) {
                ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
                Image img = icon.getImage().getScaledInstance(500, 350, Image.SCALE_SMOOTH);
                JLabel imgLabel = new JLabel(new ImageIcon(img));
                gbc.gridx = 1; gbc.weightx = 0.5;
                gbc.insets = new Insets(0, 40, 0, 0);
                hero.add(imgLabel, gbc);
            }
        } catch (Exception e) { e.printStackTrace(); }

        return hero;
    }

    private JPanel createWhyUs() {
        JPanel section = new JPanel();
        section.setOpaque(false);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBorder(new EmptyBorder(30, 140, 50, 140));
        JLabel title = new JLabel("Why LifeLine?");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        JLabel desc = new JLabel("<html>LifeLine ensures quick blood availability, transparency, and coordination through a trusted nationwide donor network.</html>");
        desc.setFont(new Font("SansSerif", Font.PLAIN, 15));
        desc.setForeground(MUTED);
        section.add(title);
        section.add(Box.createVerticalStrut(14));
        section.add(desc);
        return section;
    }

    private JPanel createWhatWeDo() {
        JPanel section = new JPanel(new BorderLayout());
        section.setOpaque(false);
        section.setBorder(new EmptyBorder(20, 90, 60, 90));
        JLabel title = new JLabel("What We Do");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        section.add(title, BorderLayout.NORTH);
        return section;
    }

    private JPanel createStats() {
        JPanel stats = new JPanel(new GridLayout(1, 3, 40, 0));
        stats.setOpaque(false);
        stats.setBorder(new EmptyBorder(30, 140, 60, 140));

        int livesSaved = fetchCount("SELECT SUM(units_requested) FROM blood_requests WHERE status='Approved'");
        int activeDonors = fetchCount("SELECT COUNT(*) FROM users");
        int partnerHospitals = fetchCount("SELECT COUNT(DISTINCT hospital_name) FROM blood_requests");

        stats.add(statCard(livesSaved > 0 ? livesSaved : 120, "Units Fulfilled"));
        stats.add(statCard(activeDonors > 0 ? activeDonors : 50, "Active Donors"));
        stats.add(statCard(partnerHospitals > 0 ? partnerHospitals : 15, "Partner Hospitals"));

        return stats;
    }

    private JPanel statCard(int value, String label) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(new EmptyBorder(36, 30, 36, 30));
        JLabel v = new JLabel("0+", SwingConstants.CENTER);
        v.setFont(new Font("SansSerif", Font.BOLD, 36));
        v.setForeground(PRIMARY);
        animateCounter(v, value);
        JLabel l = new JLabel(label, SwingConstants.CENTER);
        l.setFont(new Font("SansSerif", Font.BOLD, 14));
        l.setForeground(MUTED);
        card.add(v, BorderLayout.CENTER);
        card.add(l, BorderLayout.SOUTH);
        addHover(card);
        return card;
    }

    private JPanel createEmergencyBanner() {
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(PRIMARY);
        banner.setBorder(new EmptyBorder(45, 90, 45, 90));

        JLabel text = new JLabel("<html><span style='font-size:26px;color:white;'>Emergency Blood Needed?</span></html>");
        banner.add(text, BorderLayout.WEST);

        JButton btn = new JButton("Request Now");
        styleOutlineButton(btn);
        btn.setForeground(Color.WHITE);
        addHover(btn);
        
        btn.addActionListener(e -> {
            new BloodRequestPage().setVisible(true);
            dispose();
        });
        
        banner.add(btn, BorderLayout.EAST);
        return banner;
    }

    // ================= FIXED FOOTER ACTION =================
    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(FOOTER_BG);
        footer.setBorder(new EmptyBorder(50, 90, 30, 90));

        JLabel copy = new JLabel("© 2025 LifeLine Blood Bank Management System", SwingConstants.LEFT);
        copy.setForeground(Color.GRAY);
        footer.add(copy, BorderLayout.WEST);

        JButton adminBtn = new JButton("Admin Portal");
        adminBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        adminBtn.setForeground(new Color(100, 100, 100)); // Visible color
        adminBtn.setContentAreaFilled(false);
        adminBtn.setBorderPainted(false);
        adminBtn.setFocusPainted(false);
        adminBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        adminBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { adminBtn.setForeground(Color.WHITE); }
            public void mouseExited(MouseEvent e) { adminBtn.setForeground(new Color(100, 100, 100)); }
        });

        // FIXED: Try-catch block to prevent entire program from stopping on error
        adminBtn.addActionListener(e -> {
            try {
                AdminLogin loginFrame = new AdminLogin();
                loginFrame.setVisible(true);
                dispose(); 
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error launching Admin Portal: " + ex.getMessage());
            }
        });

        footer.add(adminBtn, BorderLayout.EAST);
        return footer;
    }

    // ================= UTILITIES =================
    private JLabel navLink(String text) {
        JLabel link = new JLabel(text);
        link.setFont(new Font("SansSerif", Font.BOLD, 14));
        link.setForeground(new Color(73, 80, 87));
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { link.setForeground(PRIMARY); }
            public void mouseExited(MouseEvent e) { link.setForeground(new Color(73, 80, 87)); }
        });
        return link;
    }

    private void stylePrimaryButton(JButton btn) {
        btn.setBackground(PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(12, 30, 12, 30));
    }

    private void styleOutlineButton(JButton btn) {
        btn.setContentAreaFilled(false);
        btn.setForeground(PRIMARY);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY),
                new EmptyBorder(10, 24, 10, 24)
        ));
    }

    private void addHover(JComponent c) {
        c.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { c.setCursor(new Cursor(Cursor.HAND_CURSOR)); }
        });
    }

    private void animateCounter(JLabel label, int target) {
        Timer timer = new Timer(20, null);
        timer.addActionListener(new ActionListener() {
            int count = 0;
            public void actionPerformed(ActionEvent e) {
                if (target <= 0) { label.setText("0+"); timer.stop(); return; }
                count += Math.max(1, target / 50);
                label.setText(count + "+");
                if (count >= target) {
                    label.setText(target + "+");
                    timer.stop();
                }
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserHome().setVisible(true));
    }
}