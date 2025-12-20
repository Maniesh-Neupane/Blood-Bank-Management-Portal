package Bank;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.security.MessageDigest;

public class LoginPage extends JFrame {

    private static final Color PRIMARY_RED = new Color(220, 53, 69);
    private static final Color DARK_NAVY = new Color(15, 23, 42);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color SLATE_TEXT = new Color(71, 85, 105);
    private static final Color INPUT_BG = new Color(248, 250, 252);

    private JTextField userField;
    private JPasswordField passField;
    private JPanel leftHero, rightForm, mainContent;

    public LoginPage() {
        setTitle("LifeLine | Donor Login");
        setSize(1150, 750);
        setMinimumSize(new Dimension(850, 650));
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
        nav.setPreferredSize(new Dimension(0, 75));
        nav.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));

        JLabel logo = new JLabel("  🩸 LifeLine Blood Bank");
        logo.setFont(new Font("SansSerif", Font.BOLD, 22));
        logo.setForeground(PRIMARY_RED);
        nav.add(logo, BorderLayout.WEST);

        JButton backBtn = new JButton("← Back to Home");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        backBtn.setForeground(SLATE_TEXT);
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
             new UserHome().setVisible(true); 
             dispose(); 
        });

        JPanel rightSide = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 20));
        rightSide.setOpaque(false);
        rightSide.add(backBtn);
        nav.add(rightSide, BorderLayout.EAST);

        return nav;
    }

    private JPanel buildHero() {
        JPanel p = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_RED, 0, getHeight(), new Color(150, 30, 45));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(new Color(255, 255, 255, 20));
                for (int i = 0; i < getWidth() + getHeight(); i += 20) {
                    g2d.drawLine(i, 0, i - getHeight(), getHeight());
                }
            }
        };

        JLabel text = new JLabel("<html><div style='text-align:center; color:white;'>"
                + "<h1 style='font-size:38px;'>Welcome Back!</h1><br>"
                + "<p style='font-size:16px; font-weight:lighter;'>Your donation saves lives.<br>Log in to manage your impact.</p></div></html>");
        p.add(text);
        return p;
    }

    private JPanel buildForm() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 60, 10, 60);

        JLabel title = new JLabel("Donor Login");
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setForeground(DARK_NAVY);

        userField = createModernInput(false);
        passField = (JPasswordField) createModernInput(true);

        JButton loginBtn = new JButton("Login to Dashboard");
        stylePrimaryButton(loginBtn);
        loginBtn.addActionListener(e -> handleLogin());

        JButton regLink = new JButton("New donor? Create an account");
        regLink.setFont(new Font("SansSerif", Font.BOLD, 13));
        regLink.setForeground(PRIMARY_RED);
        regLink.setContentAreaFilled(false);
        regLink.setBorderPainted(false);
        regLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        regLink.addActionListener(e -> { new RegisterPage().setVisible(true); dispose(); });

        gbc.gridy = 0; gbc.insets = new Insets(0, 60, 40, 60); p.add(title, gbc);
        gbc.gridy = 1; gbc.insets = new Insets(10, 60, 5, 60); p.add(new JLabel("<html><b>Username</b></html>"), gbc);
        gbc.gridy = 2; p.add(userField, gbc);
        gbc.gridy = 3; gbc.insets = new Insets(20, 60, 5, 60); p.add(new JLabel("<html><b>Password</b></html>"), gbc);
        gbc.gridy = 4; p.add(passField, gbc);
        gbc.gridy = 5; gbc.insets = new Insets(45, 60, 15, 60); p.add(loginBtn, gbc);
        gbc.gridy = 6; p.add(regLink, gbc);

        return p;
    }

    private JTextField createModernInput(boolean isPass) {
        JTextField f = isPass ? new JPasswordField() : new JTextField();
        f.setPreferredSize(new Dimension(350, 45));
        f.setFont(new Font("SansSerif", Font.PLAIN, 15));
        f.setBackground(INPUT_BG);
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(0, 15, 0, 15)
        ));
        return f;
    }

    private void stylePrimaryButton(JButton b) {
        b.setBackground(PRIMARY_RED);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 16));
        b.setPreferredSize(new Dimension(350, 50));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // --- REPAIRED LOGIN HANDLER ---
    private void handleLogin() {
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
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
                
                // CRITICAL FIX: Don't hide the login page until the dashboard is fully loaded
                SwingUtilities.invokeLater(() -> {
                    try {
                        UserDashboard dashboard = new UserDashboard(fullName, bGroup);
                        dashboard.setVisible(true);
                        this.dispose(); // Only close this window after success
                    } catch (Exception dashEx) {
                        dashEx.printStackTrace();
                        setCursor(Cursor.getDefaultCursor());
                        JOptionPane.showMessageDialog(this, "Dashboard Error: " + dashEx.getMessage());
                        this.setVisible(true); // Ensure login remains visible on failure
                    }
                });
            } else {
                setCursor(Cursor.getDefaultCursor());
                JOptionPane.showMessageDialog(this, "Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
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
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}