package Bank;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.security.MessageDigest;

public class AdminLogin extends JFrame {

    // --- Modern Design Palette ---
    private final Color DARK_NAVY = new Color(15, 23, 42); 
    private final Color ACCENT_RED = new Color(220, 53, 69);
    private final Color BG_LIGHT = new Color(241, 245, 249); 
    private final Color BORDER_GRAY = new Color(226, 232, 240);
    private final Color TEXT_GRAY = new Color(100, 116, 139);

    private JTextField userField;
    private JPasswordField passField;

    public AdminLogin() {
        setTitle("LifeLine | Secure Admin Access");
        setSize(1100, 800); 
        setMinimumSize(new Dimension(500, 600));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel backgroundPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, BG_LIGHT, 0, getHeight(), new Color(226, 232, 240));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // --- The Login Card ---
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(420, 580)); // Height increased to accommodate back button
        card.setMaximumSize(new Dimension(420, 580));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER_GRAY, 1, true),
            new EmptyBorder(45, 45, 45, 45)
        ));

        // Header Section
        JLabel icon = new JLabel("🛡️");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 48));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Admin Login");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(DARK_NAVY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subTitle = new JLabel("Secure gateway for system management");
        subTitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subTitle.setForeground(TEXT_GRAY);
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Input Fields
        userField = styledField();
        passField = (JPasswordField) styledField(true);

        // Login Button
        JButton loginBtn = new JButton("AUTHENTICATE");
        styleButton(loginBtn);
        loginBtn.addActionListener(e -> handleLogin());

        // --- ADDED: Back Button ---
        JButton backBtn = new JButton("← Back to Portal");
        styleSecondaryButton(backBtn);
        backBtn.addActionListener(e -> {
            // Replace 'MainPortal' with the class name of your main landing page/login
            new LoginPage().setVisible(true); 
            this.dispose();
        });

        // Assembly
        card.add(icon); card.add(Box.createVerticalStrut(15));
        card.add(title); card.add(Box.createVerticalStrut(8));
        card.add(subTitle); card.add(Box.createVerticalStrut(40));
        
        card.add(createInputWrapper("USERNAME", userField));
        card.add(Box.createVerticalStrut(20));
        card.add(createInputWrapper("PASSWORD", passField));
        card.add(Box.createVerticalStrut(40));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(15)); // Spacer
        card.add(backBtn); // Added back button to layout

        backgroundPanel.add(card);
        add(backgroundPanel);
        
        getRootPane().setDefaultButton(loginBtn);
    }

    // --- Helper to style the Back Button ---
    private void styleSecondaryButton(JButton btn) {
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(TEXT_GRAY);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(DARK_NAVY); }
            public void mouseExited(MouseEvent e) { btn.setForeground(TEXT_GRAY); }
        });
    }

    private JPanel createInputWrapper(String labelStr, JComponent field) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(330, 75));
        
        JLabel label = new JLabel(labelStr);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(DARK_NAVY);
        
        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(field, BorderLayout.CENTER);
        return wrapper;
    }

    private JTextField styledField() { return styledField(false); }
    private JTextField styledField(boolean isPass) {
        JTextField f = isPass ? new JPasswordField() : new JTextField();
        f.setPreferredSize(new Dimension(330, 45));
        f.setFont(new Font("SansSerif", Font.PLAIN, 14));
        f.setBorder(new CompoundBorder(
            new LineBorder(BORDER_GRAY, 1),
            new EmptyBorder(0, 15, 0, 15)
        ));
        
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { f.setBorder(new CompoundBorder(new LineBorder(ACCENT_RED, 1), new EmptyBorder(0, 15, 0, 15))); }
            public void focusLost(FocusEvent e) { f.setBorder(new CompoundBorder(new LineBorder(BORDER_GRAY, 1), new EmptyBorder(0, 15, 0, 15))); }
        });
        return f;
    }

    private void styleButton(JButton btn) {
        btn.setMaximumSize(new Dimension(330, 50));
        btn.setPreferredSize(new Dimension(330, 50));
        btn.setBackground(DARK_NAVY);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(ACCENT_RED); }
            public void mouseExited(MouseEvent e) { btn.setBackground(DARK_NAVY); }
        });
    }

    private void handleLogin() {
        String uid = userField.getText().trim();
        String pwd = new String(passField.getPassword());

        if(uid.isEmpty() || pwd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Security credentials required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.connect()) {
            String sql = "SELECT * FROM users WHERE username=? AND password=? AND LOWER(role)='admin'";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, uid);
            pst.setString(2, hashPassword(pwd));

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                new AdminDash().setVisible(true); 
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Access Denied: Invalid Admin Credentials", "Security Alert", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database Connectivity Error: " + ex.getMessage());
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { return password; }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new AdminLogin().setVisible(true));
    }
}