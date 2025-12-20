package Bank;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.security.MessageDigest;
import java.util.regex.Pattern;

public class RegisterPage extends JFrame {

    // --- Your Original Palette ---
    private static final Color PRIMARY_RED = new Color(220, 53, 69);
    private static final Color DARK_NAVY = new Color(15, 23, 42);
    private static final Color BG_LIGHT = new Color(248, 250, 252);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color TEXT_GRAY = new Color(100, 116, 139);

    private JTextField nameF, userF, emailF, phoneF;
    private JPasswordField passF;
    private JComboBox<String> bloodGrp;
    private JPanel leftHero, rightForm;

    public RegisterPage() {
        setTitle("LifeLine | Create Account");
        setSize(1200, 850);
        setMinimumSize(new Dimension(900, 700));
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

    // --- UI Methods (Exact original design) ---
    private JPanel buildHeroPanel() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_RED, getWidth(), getHeight(), new Color(150, 20, 40));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        p.setLayout(new GridBagLayout());
        JLabel msg = new JLabel("<html><div style='text-align: center; color: white;'>"
                + "<h1 style='font-size: 32px;'>Every Drop Counts.</h1>"
                + "<p style='font-size: 14px;'>Join thousands of donors and help<br>save lives every single day.</p>"
                + "</div></html>");
        p.add(msg);
        return p;
    }

    private JPanel buildFormPanel() {
        JPanel mainRight = new JPanel(new BorderLayout());
        mainRight.setBackground(Color.WHITE);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(20, 20, 0, 0));
        
        JButton btnBack = new JButton(" ←  Back to Home");
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnBack.setForeground(TEXT_GRAY);
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> { new UserHome().setVisible(true); dispose(); });
        navPanel.add(btnBack);
        mainRight.add(navPanel, BorderLayout.NORTH);

        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(10, 60, 40, 60));
        card.setPreferredSize(new Dimension(500, 750));

        JLabel title = new JLabel("Join LifeLine");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(DARK_NAVY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        nameF = styledInput();
        userF = styledInput();
        emailF = styledInput();
        phoneF = styledInput();
        passF = (JPasswordField) styledInput(true);
        bloodGrp = new JComboBox<>(new String[]{"Select Blood Group", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"});
        styleCombo(bloodGrp);

        JButton regBtn = new JButton("Create Free Account");
        stylePrimaryButton(regBtn);
        regBtn.addActionListener(e -> handleRegister());

        card.add(title); card.add(Box.createVerticalStrut(25));
        addInputGroup(card, "Full Name", nameF);
        addInputGroup(card, "Username", userF);
        addInputGroup(card, "Email Address", emailF);
        addInputGroup(card, "Phone", phoneF);
        addInputGroup(card, "Blood Group", bloodGrp);
        addInputGroup(card, "Create Password", passF);
        
        card.add(Box.createVerticalStrut(20));
        card.add(regBtn);

        p.add(card);
        mainRight.add(p, BorderLayout.CENTER);
        return mainRight;
    }

    // --- SECURITY & VALIDATION LOGIC ---
    private void handleRegister() {
        String name = nameF.getText().trim();
        String user = userF.getText().trim();
        String email = emailF.getText().trim();
        String phone = phoneF.getText().trim();
        String blood = bloodGrp.getSelectedItem().toString();
        String pass = new String(passF.getPassword());

        // 1. Basic Field Validation
        if(name.isEmpty() || user.isEmpty() || email.isEmpty() || pass.length() < 6 || blood.equals("Select Blood Group")) {
            JOptionPane.showMessageDialog(this, "Please fill all fields. Password must be 6+ chars.");
            return;
        }

        // 2. Email Format Validation
        if (!Pattern.compile("^(.+)@(.+)$").matcher(email).matches()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.");
            return;
        }

        try (Connection con = DBConnection.connect()) {
            // 3. Prevent Duplicates (Check if User/Email exists)
            String checkSql = "SELECT id FROM users WHERE username = ? OR email = ?";
            PreparedStatement checkPs = con.prepareStatement(checkSql);
            checkPs.setString(1, user);
            checkPs.setString(2, email);
            if (checkPs.executeQuery().next()) {
                JOptionPane.showMessageDialog(this, "Username or Email already taken.");
                return;
            }

            // 4. Secure Insertion (Strictly forced 'User' role)
            String sql = "INSERT INTO users (name, username, email, phone, blood_group, password, role, points, donation_count) VALUES (?,?,?,?,?,?,'User', 0, 0)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, user);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setString(5, blood);
            ps.setString(6, hashPassword(pass));

            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Welcome to the LifeLine family!");
                new LoginPage().setVisible(true);
                dispose();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Connection Error: " + ex.getMessage());
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

    // --- Helper Styling Methods (Original) ---
    private void addInputGroup(JPanel p, String label, JComponent input) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(DARK_NAVY);
        p.add(l); p.add(Box.createVerticalStrut(5));
        p.add(input); p.add(Box.createVerticalStrut(12));
    }

    private JTextField styledInput() { return styledInput(false); }
    private JTextField styledInput(boolean isPass) {
        JTextField f = isPass ? new JPasswordField() : new JTextField();
        f.setPreferredSize(new Dimension(380, 40));
        f.setMaximumSize(new Dimension(380, 40));
        f.setBackground(BG_LIGHT);
        f.setBorder(new CompoundBorder(new LineBorder(BORDER_COLOR, 1), new EmptyBorder(0, 10, 0, 10)));
        return f;
    }

    private void styleCombo(JComboBox b) {
        b.setPreferredSize(new Dimension(380, 40));
        b.setMaximumSize(new Dimension(380, 40));
        b.setBackground(BG_LIGHT);
    }

    private void stylePrimaryButton(JButton b) {
        b.setBackground(PRIMARY_RED);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 15));
        b.setPreferredSize(new Dimension(380, 50));
        b.setMaximumSize(new Dimension(380, 50));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterPage().setVisible(true));
    }
}