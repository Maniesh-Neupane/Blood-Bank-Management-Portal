package Bank;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;
import java.security.MessageDigest;
import java.util.regex.Pattern;


 
public class RegisterPage extends JFrame {

    private static final Color PRIMARY_RED = new Color(220, 53, 69);
    private static final Color PRIMARY_DARK = new Color(185, 28, 44);
    private static final Color DARK_NAVY = new Color(15, 23, 42);
    private static final Color BG_LIGHT = new Color(248, 250, 252);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color TEXT_GRAY = new Color(100, 116, 139);
    private static final Color SUCCESS_GREEN = new Color(34, 197, 94);
    private static final Color WARNING_ORANGE = new Color(251, 146, 60);
    private static final Color DANGER_RED = new Color(239, 68, 68);

    private JTextField nameF, userF, emailF, phoneF, cityF;
    private JPasswordField passF;
    private JComboBox<String> bloodGrp;
    private JPanel leftHero, rightForm;
    
    private JLabel passStrengthLabel;
    private JProgressBar passStrengthBar;

    // Strict validation patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9][a-zA-Z0-9._-]*@[a-zA-Z0-9][a-zA-Z0-9.-]*\\.[a-zA-Z]{2,}$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,15}$");
    // Username must contain at least one letter (no numbers-only)
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])[a-zA-Z0-9_]{3,20}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]{2,50}$");

    public RegisterPage() {
        setTitle("LifeLine | Create Account");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1200, 850));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new GridLayout(1, 2));
        leftHero = buildHeroPanel();
        rightForm = buildFormPanel();

        root.add(leftHero);
        root.add(rightForm);
        add(root);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (getWidth() < 950) {
                    leftHero.setVisible(false);
                    root.setLayout(new GridLayout(1, 1));
                } else {
                    leftHero.setVisible(true);
                    root.setLayout(new GridLayout(1, 2));
                }
            }
        });
    }

    private JPanel buildHeroPanel() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_RED, getWidth(), getHeight(), PRIMARY_DARK);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillOval(-100, -100, 300, 300);
                g2.fillOval(getWidth() - 200, getHeight() - 200, 300, 300);
                g2.dispose();
            }
        };
        p.setLayout(new GridBagLayout());
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(0, 60, 0, 60));
        
        JLabel icon = new JLabel("❤️");
        icon.setFont(new Font("SansSerif", Font.BOLD, 80));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel title = new JLabel("Every Drop Counts");
        title.setFont(new Font("SansSerif", Font.BOLD, 38));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel("<html><div style='text-align: center; color: rgba(255,255,255,0.9);'>"
                + "Join thousands of donors and help save<br>lives every single day.</div></html>");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel features = new JPanel();
        features.setLayout(new BoxLayout(features, BoxLayout.Y_AXIS));
        features.setOpaque(false);
        features.setBorder(new EmptyBorder(30, 0, 0, 0));
        
        features.add(createFeatureItem("✓", "Quick and easy registration"));
        features.add(Box.createVerticalStrut(12));
        features.add(createFeatureItem("✓", "Track your donation history"));
        features.add(Box.createVerticalStrut(12));
        features.add(createFeatureItem("✓", "Earn recognition points"));
        features.add(Box.createVerticalStrut(12));
        features.add(createFeatureItem("✓", "Save lives in your community"));
        
        content.add(icon);
        content.add(Box.createVerticalStrut(25));
        content.add(title);
        content.add(Box.createVerticalStrut(15));
        content.add(subtitle);
        content.add(features);
        
        p.add(content);
        return p;
    }

    private JPanel createFeatureItem(String icon, String text) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        item.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 12));
        
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        textLabel.setForeground(new Color(255, 255, 255, 230));
        
        item.add(iconLabel);
        item.add(textLabel);
        return item;
    }

    private JPanel buildFormPanel() {
        JPanel mainRight = new JPanel(new BorderLayout());
        mainRight.setBackground(Color.WHITE);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(25, 30, 0, 0));
        
        JButton btnBack = new JButton("← Back to Home");
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnBack.setForeground(TEXT_GRAY);
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setFocusPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnBack.setForeground(PRIMARY_RED); }
            public void mouseExited(MouseEvent e) { btnBack.setForeground(TEXT_GRAY); }
        });
        btnBack.addActionListener(e -> { new UserHome().setVisible(true); dispose(); });
        navPanel.add(btnBack);
        mainRight.add(navPanel, BorderLayout.NORTH);

        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setOpaque(false);
        
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(20, 70, 50, 70));
        card.setPreferredSize(new Dimension(550, 800));

        JLabel title = new JLabel("Join LifeLine");
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setForeground(DARK_NAVY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Create your account to start saving lives");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_GRAY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        nameF = styledInput("Enter your full name");
        userF = styledInput("Choose a username");
        emailF = styledInput("your.email@example.com");
        phoneF = styledInput("Your phone number");
        cityF = styledInput("Your city (e.g., Kathmandu)");
        passF = (JPasswordField) styledInput("Create a strong password", true);
        
        passStrengthLabel = new JLabel(" ");
        passStrengthLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        passStrengthLabel.setForeground(TEXT_GRAY);
        passStrengthLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        passStrengthBar = new JProgressBar(0, 100);
        passStrengthBar.setPreferredSize(new Dimension(410, 5));
        passStrengthBar.setMaximumSize(new Dimension(410, 5));
        passStrengthBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        passStrengthBar.setStringPainted(false);
        passStrengthBar.setBorderPainted(false);
        
        addPasswordStrengthChecker(passF, passStrengthLabel, passStrengthBar);
        
        bloodGrp = new JComboBox<>(new String[]{
            "Select Blood Group", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"
        });
        styleCombo(bloodGrp);

        JButton regBtn = new JButton("Create Free Account");
        stylePrimaryButton(regBtn);
        regBtn.addActionListener(e -> handleRegister());

        JPanel loginLink = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        loginLink.setOpaque(false);
        loginLink.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel loginText = new JLabel("Already have an account?");
        loginText.setFont(new Font("SansSerif", Font.PLAIN, 13));
        loginText.setForeground(TEXT_GRAY);
        
        JLabel loginBtn = new JLabel("Sign In");
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        loginBtn.setForeground(PRIMARY_RED);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { loginBtn.setForeground(PRIMARY_DARK); }
            public void mouseExited(MouseEvent e) { loginBtn.setForeground(PRIMARY_RED); }
            public void mouseClicked(MouseEvent e) {
                new LoginPage().setVisible(true);
                dispose();
            }
        });
        
        loginLink.add(loginText);
        loginLink.add(loginBtn);

        card.add(title); 
        card.add(Box.createVerticalStrut(8));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(35));
        
        addInputGroup(card, "Full Name", nameF);
        addInputGroup(card, "Username", userF);
        addInputGroup(card, "Email Address", emailF);
        addInputGroup(card, "Phone Number", phoneF);
        addInputGroup(card, "City / Address", cityF);
        addInputGroup(card, "Blood Group", bloodGrp);
        addPasswordGroup(card, "Password", passF);
        
        card.add(Box.createVerticalStrut(30));
        card.add(regBtn);
        card.add(Box.createVerticalStrut(20));
        card.add(loginLink);

        formContainer.add(card);
        
        JScrollPane scrollPane = new JScrollPane(formContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        mainRight.add(scrollPane, BorderLayout.CENTER);
        return mainRight;
    }

    private void addPasswordStrengthChecker(JPasswordField passField, JLabel strengthLabel, JProgressBar strengthBar) {
        passField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { check(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { check(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { check(); }
            
            private void check() {
                String password = new String(passField.getPassword());
                if (password.isEmpty()) {
                    strengthLabel.setText(" ");
                    strengthBar.setValue(0);
                    return;
                }
                
                int score = 0;
                if (password.length() >= 8) score += 25;
                if (password.length() >= 12) score += 15;
                if (password.matches(".*[a-z].*")) score += 15;
                if (password.matches(".*[A-Z].*")) score += 15;
                if (password.matches(".*[0-9].*")) score += 15;
                if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) score += 15;
                
                String label;
                Color color;
                if (score < 40) {
                    label = "Weak password";
                    color = DANGER_RED;
                } else if (score < 65) {
                    label = "Fair password";
                    color = WARNING_ORANGE;
                } else if (score < 85) {
                    label = "Good password";
                    color = SUCCESS_GREEN;
                } else {
                    label = "Strong password";
                    color = new Color(5, 150, 105);
                }
                
                strengthLabel.setText(label);
                strengthBar.setValue(score);
                strengthBar.setForeground(color);
                strengthLabel.setForeground(color);
            }
        });
    }

    // STRICT VALIDATION - Production Ready
    private void handleRegister() {
        String name = cleanInput(nameF.getText());
        String user = cleanInput(userF.getText());
        String email = cleanInput(emailF.getText());
        String phone = cleanInput(phoneF.getText());
        String city = cleanInput(cityF.getText());
        String blood = bloodGrp.getSelectedItem().toString();
        String pass = new String(passF.getPassword());

        StringBuilder errors = new StringBuilder();
        
        // Name validation
        if (name.length() < 2) {
            errors.append("• Full name is required (minimum 2 characters)\n");
        } else if (name.length() > 50) {
            errors.append("• Name is too long (maximum 50 characters)\n");
        } else if (!NAME_PATTERN.matcher(name).matches()) {
            errors.append("• Name can only contain letters and spaces\n");
        }
        
        // STRICT Username validation - must contain at least one letter
        if (user.length() < 3) {
            errors.append("• Username is required (minimum 3 characters)\n");
        } else if (user.length() > 20) {
            errors.append("• Username is too long (maximum 20 characters)\n");
        } else if (!USERNAME_PATTERN.matcher(user).matches()) {
            // Check if it's numbers only
            if (user.matches("^[0-9_]+$")) {
                errors.append("• Username must contain at least one letter\n");
            } else {
                errors.append("• Username can only contain letters, numbers, and underscores\n");
            }
        }
        
        // STRICT Email validation - proper email format required
        if (email.isEmpty()) {
            errors.append("• Email address is required\n");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            errors.append("• Please enter a valid email address (e.g., name@example.com)\n");
        } else if (email.length() > 100) {
            errors.append("• Email is too long\n");
        } else {
            // Additional email validation
            String domain = email.substring(email.indexOf("@") + 1);
            if (!domain.contains(".")) {
                errors.append("• Email domain is invalid (must contain a dot)\n");
            }
        }
        
        // Phone validation (optional but strict if provided)
        if (!phone.isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
            errors.append("• Phone number must be 10-15 digits only\n");
        }
        
        // Password validation
        if (pass.length() < 8) {
            errors.append("• Password must be at least 8 characters\n");
        }
        
        // Blood group validation
        if (blood.equals("Select Blood Group")) {
            errors.append("• Please select your blood group\n");
        }
        
        // Show errors if any
        if (errors.length() > 0) {
            showError(errors.toString().trim());
            return;
        }

        // Database operations
        try (Connection con = DBConnection.connect()) {
            if (con == null) {
                showError("Unable to connect to database.\nPlease check your connection.");
                return;
            }
            
            // Check for duplicate username or email
            String checkSql = "SELECT username, email FROM users WHERE username = ? OR email = ?";
            try (PreparedStatement ps = con.prepareStatement(checkSql)) {
                ps.setString(1, user);
                ps.setString(2, email);
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    String existingUser = rs.getString("username");
                    String existingEmail = rs.getString("email");
                    
                    if (existingUser.equalsIgnoreCase(user)) {
                        showError("Username '" + user + "' is already taken.\nPlease choose a different username.");
                    } else if (existingEmail.equalsIgnoreCase(email)) {
                        showError("Email '" + email + "' is already registered.\nPlease use a different email or sign in.");
                    }
                    return;
                }
            }

            // Insert new user
            String insertSql = "INSERT INTO users (name, username, email, phone, city, blood_group, password, role, points, donation_count) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?, 'User', 0, 0)";
            
            try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                ps.setString(1, name);
                ps.setString(2, user);
                ps.setString(3, email);
                ps.setString(4, phone.isEmpty() ? null : phone);
                ps.setString(5, city.isEmpty() ? null : city);
                ps.setString(6, blood);
                ps.setString(7, hashPassword(pass));

                int rows = ps.executeUpdate();
                
                if (rows > 0) {
                    showSuccess("Account created successfully!\n\nWelcome to LifeLine, " + name + "!");
                    new LoginPage().setVisible(true);
                    dispose();
                } else {
                    showError("Registration failed.\nPlease try again.");
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database error occurred.\nPlease try again later.");
        } catch (Exception e) {
            e.printStackTrace();
            showError("An unexpected error occurred.\nPlease try again.");
        }
    }

    private String cleanInput(String input) {
        if (input == null) return "";
        input = input.trim();
        
        // Remove placeholder text
        if (input.equals("Enter your full name") || 
            input.equals("Choose a username") ||
            input.equals("your.email@example.com") ||
            input.equals("Your phone number") ||
            input.equals("Your city (e.g., Kathmandu)")) {
            return "";
        }
        
        return input;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return password;
        }
    }

    private void addInputGroup(JPanel p, String label, JComponent input) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        l.setForeground(DARK_NAVY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l); 
        p.add(Box.createVerticalStrut(8));
        p.add(input); 
        p.add(Box.createVerticalStrut(18));
    }

    private void addPasswordGroup(JPanel p, String label, JPasswordField input) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        l.setForeground(DARK_NAVY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        p.add(l);
        p.add(Box.createVerticalStrut(8));
        p.add(input);
        p.add(Box.createVerticalStrut(6));
        p.add(passStrengthBar);
        p.add(Box.createVerticalStrut(3));
        p.add(passStrengthLabel);
        p.add(Box.createVerticalStrut(12));
    }

    private JTextField styledInput(String placeholder) { 
        return styledInput(placeholder, false); 
    }
    
    private JTextField styledInput(String placeholder, boolean isPass) {
        JTextField f = isPass ? new JPasswordField() : new JTextField();
        f.setPreferredSize(new Dimension(410, 45));
        f.setMaximumSize(new Dimension(410, 45));
        f.setBackground(BG_LIGHT);
        f.setFont(new Font("SansSerif", Font.PLAIN, 14));
        f.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(0, 15, 0, 15)
        ));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        
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
        
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(new CompoundBorder(
                    new LineBorder(PRIMARY_RED, 2, true),
                    new EmptyBorder(0, 15, 0, 15)
                ));
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(new CompoundBorder(
                    new LineBorder(BORDER_COLOR, 1, true),
                    new EmptyBorder(0, 15, 0, 15)
                ));
            }
        });
        
        return f;
    }

    private void styleCombo(JComboBox b) {
        b.setPreferredSize(new Dimension(410, 45));
        b.setMaximumSize(new Dimension(410, 45));
        b.setBackground(BG_LIGHT);
        b.setFont(new Font("SansSerif", Font.PLAIN, 14));
        b.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(0, 10, 0, 10)
        ));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void stylePrimaryButton(JButton b) {
        b.setBackground(PRIMARY_RED);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 15));
        b.setPreferredSize(new Dimension(410, 50));
        b.setMaximumSize(new Dimension(410, 50));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(0, 0, 0, 0));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(PRIMARY_DARK); }
            public void mouseExited(MouseEvent e) { b.setBackground(PRIMARY_RED); }
        });
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Registration Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterPage().setVisible(true));
    }
}