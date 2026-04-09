package Bank;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.security.MessageDigest;

public class LoginPage extends JFrame {

    private static final Color PRIMARY_RED = new Color(220, 53, 69);
    private static final Color PRIMARY_DARK = new Color(185, 28, 44);
    private static final Color DARK_NAVY = new Color(15, 23, 42);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color SLATE_TEXT = new Color(71, 85, 105);
    private static final Color INPUT_BG = new Color(248, 250, 252);
    private static final Color TEXT_GRAY = new Color(100, 116, 139);

    private JTextField userField;
    private JPasswordField passField;
    private JPanel leftHero, rightForm, mainContent;

    public LoginPage() {
        setTitle("LifeLine | Donor Login");
        
        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1150, 750));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        root.add(createTopNav(), BorderLayout.NORTH);

        mainContent = new JPanel(new GridLayout(1, 2));
        leftHero = buildHero();
        rightForm = buildForm();
        mainContent.add(leftHero);
        mainContent.add(rightForm);

        root.add(mainContent, BorderLayout.CENTER);
        add(root);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (getWidth() < 950) {
                    leftHero.setVisible(false);
                    ((GridLayout)mainContent.getLayout()).setColumns(1);
                } else {
                    leftHero.setVisible(true);
                    ((GridLayout)mainContent.getLayout()).setColumns(2);
                }
                mainContent.revalidate();
            }
        });
    }

    private JPanel createTopNav() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(Color.WHITE);
        nav.setPreferredSize(new Dimension(0, 70));
        nav.setBorder(new EmptyBorder(0, 70, 0, 70));

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 20));
        logoPanel.setOpaque(false);
        
        JLabel logoIcon = new JLabel("❤");
        logoIcon.setFont(new Font("SansSerif", Font.BOLD, 26));
        logoIcon.setForeground(PRIMARY_RED);
        
        JLabel logo = new JLabel("LifeLine Blood Bank");
        logo.setFont(new Font("SansSerif", Font.BOLD, 22));
        logo.setForeground(DARK_NAVY);
        
        logoPanel.add(logoIcon);
        logoPanel.add(logo);
        nav.add(logoPanel, BorderLayout.WEST);

        JButton backBtn = new JButton("← Back to Home");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        backBtn.setForeground(TEXT_GRAY);
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { backBtn.setForeground(PRIMARY_RED); }
            public void mouseExited(MouseEvent e) { backBtn.setForeground(TEXT_GRAY); }
        });
        backBtn.addActionListener(e -> {
             new UserHome().setVisible(true); 
             dispose(); 
        });

        JPanel rightSide = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 20));
        rightSide.setOpaque(false);
        rightSide.add(backBtn);
        nav.add(rightSide, BorderLayout.EAST);

        // Bottom border
        JPanel navWrapper = new JPanel(new BorderLayout());
        navWrapper.add(nav, BorderLayout.CENTER);
        navWrapper.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));

        return navWrapper;
    }

    private JPanel buildHero() {
        JPanel p = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Modern gradient background
                GradientPaint gp = new GradientPaint(
                    0, 0, PRIMARY_RED, 
                    0, getHeight(), PRIMARY_DARK
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Decorative circles
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillOval(-100, -100, 300, 300);
                g2d.fillOval(getWidth() - 200, getHeight() - 200, 300, 300);
            }
        };

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(0, 60, 0, 60));
        
        // Icon
        JLabel icon = new JLabel("🩸");
        icon.setFont(new Font("SansSerif", Font.BOLD, 80));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Title
        JLabel title = new JLabel("Welcome Back!");
        title.setFont(new Font("SansSerif", Font.BOLD, 42));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle
        JLabel subtitle = new JLabel("<html><div style='text-align: center; color: rgba(255,255,255,0.9);'>"
                + "Your donation saves lives.<br>Log in to manage your impact.</div></html>");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        content.add(icon);
        content.add(Box.createVerticalStrut(25));
        content.add(title);
        content.add(Box.createVerticalStrut(15));
        content.add(subtitle);
        
        p.add(content);
        return p;
    }

    private JPanel buildForm() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 60, 10, 60);

        JLabel title = new JLabel("Donor Login");
        title.setFont(new Font("SansSerif", Font.BOLD, 38));
        title.setForeground(DARK_NAVY);

        JLabel subtitle = new JLabel("Enter your credentials to access your dashboard");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_GRAY);

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        userLabel.setForeground(DARK_NAVY);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        passLabel.setForeground(DARK_NAVY);

        userField = createModernInput("Enter your username", false);
        passField = (JPasswordField) createModernInput("Enter your password", true);

        JButton loginBtn = new JButton("Login to Dashboard");
        stylePrimaryButton(loginBtn);
        loginBtn.addActionListener(e -> handleLogin());

        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        registerPanel.setOpaque(false);
        
        JLabel regText = new JLabel("New donor?");
        regText.setFont(new Font("SansSerif", Font.PLAIN, 13));
        regText.setForeground(TEXT_GRAY);
        
        JLabel regLink = new JLabel("Create an account");
        regLink.setFont(new Font("SansSerif", Font.BOLD, 13));
        regLink.setForeground(PRIMARY_RED);
        regLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        regLink.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { regLink.setForeground(PRIMARY_DARK); }
            public void mouseExited(MouseEvent e) { regLink.setForeground(PRIMARY_RED); }
            public void mouseClicked(MouseEvent e) { 
                new RegisterPage().setVisible(true); 
                dispose(); 
            }
        });
        
        registerPanel.add(regText);
        registerPanel.add(regLink);

        gbc.gridy = 0; gbc.insets = new Insets(0, 60, 10, 60); p.add(title, gbc);
        gbc.gridy = 1; gbc.insets = new Insets(0, 60, 40, 60); p.add(subtitle, gbc);
        gbc.gridy = 2; gbc.insets = new Insets(10, 60, 8, 60); p.add(userLabel, gbc);
        gbc.gridy = 3; p.add(userField, gbc);
        gbc.gridy = 4; gbc.insets = new Insets(20, 60, 8, 60); p.add(passLabel, gbc);
        gbc.gridy = 5; p.add(passField, gbc);
        gbc.gridy = 6; gbc.insets = new Insets(35, 60, 20, 60); p.add(loginBtn, gbc);
        gbc.gridy = 7; gbc.insets = new Insets(0, 60, 10, 60); p.add(registerPanel, gbc);

        return p;
    }

    private JTextField createModernInput(String placeholder, boolean isPass) {
        JTextField f = isPass ? new JPasswordField() : new JTextField();
        f.setPreferredSize(new Dimension(380, 50));
        f.setFont(new Font("SansSerif", Font.PLAIN, 15));
        f.setBackground(INPUT_BG);
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(0, 15, 0, 15)
        ));
        
        // Placeholder effect
        if (!isPass) {
            f.setForeground(TEXT_GRAY);
            f.setText(placeholder);
            f.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    if (f.getText().equals(placeholder)) {
                        f.setText("");
                        f.setForeground(DARK_NAVY);
                    }
                }
                public void focusLost(FocusEvent e) {
                    if (f.getText().isEmpty()) {
                        f.setForeground(TEXT_GRAY);
                        f.setText(placeholder);
                    }
                }
            });
        }
        
        // Focus border effect
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(PRIMARY_RED, 2, true),
                    new EmptyBorder(0, 15, 0, 15)
                ));
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER_COLOR, 1, true),
                    new EmptyBorder(0, 15, 0, 15)
                ));
            }
        });
        
        return f;
    }

    private void stylePrimaryButton(JButton b) {
        b.setBackground(PRIMARY_RED);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 16));
        b.setPreferredSize(new Dimension(380, 52));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBackground(PRIMARY_DARK);
            }
            public void mouseExited(MouseEvent e) {
                b.setBackground(PRIMARY_RED);
            }
        });
    }

    // --- LOGIN HANDLER ---
    private void handleLogin() {
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword());

        // Clear placeholder check
        if (user.equals("Enter your username")) user = "";

        if (user.isEmpty() || pass.isEmpty()) {
            showErrorDialog("Please fill in all fields.");
            return;
        }

        // Change cursor to indicate loading
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try (Connection con = DBConnection.connect()) {
            String sql = "SELECT name, blood_group FROM users WHERE username=? AND password=? AND role='User'";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, hashPassword(pass));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String fullName = rs.getString("name");
                String bGroup = rs.getString("blood_group");
                
                // Load dashboard on Event Dispatch Thread
                SwingUtilities.invokeLater(() -> {
                    try {
                        UserDashboard dashboard = new UserDashboard(fullName, bGroup);
                        dashboard.setVisible(true);
                        this.dispose(); // Close login window after success
                    } catch (Exception dashEx) {
                        dashEx.printStackTrace();
                        setCursor(Cursor.getDefaultCursor());
                        showErrorDialog("Dashboard Error: " + dashEx.getMessage());
                        this.setVisible(true); // Keep login visible on failure
                    }
                });
            } else {
                setCursor(Cursor.getDefaultCursor());
                showErrorDialog("Invalid username or password.");
            }
        } catch (SQLException ex) {
            setCursor(Cursor.getDefaultCursor());
            showErrorDialog("Database Error: " + ex.getMessage());
            ex.printStackTrace();
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

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Login Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}