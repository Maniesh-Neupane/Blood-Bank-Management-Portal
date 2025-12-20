package Bank;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class BloodRequestPage extends JFrame {

    private static final Color PRIMARY_RED = new Color(220, 53, 69);
    private static final Color DARK_NAVY = new Color(15, 23, 42);
    private static final Color SLATE_TEXT = new Color(71, 85, 105);
    private static final Color BG_SOFT = new Color(248, 250, 252);
    
    private JTextField hospitalF, unitsF;
    private JComboBox<String> bloodGrp;
    private JPanel formCard;

    public BloodRequestPage() {
        setTitle("LifeLine | Blood Request Portal");
        setSize(1300, 950); 
        setMinimumSize(new Dimension(800, 850));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(Color.WHITE);

        // --- 1. HERO HEADER ---
        root.add(createHeroHeader());

        // --- 2. IMPACT THOUGHT SECTION (Replacing Table) ---
        root.add(createImpactThought());

        // --- 3. MODERN REQUEST FORM ---
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new EmptyBorder(40, 0, 40, 0));
        formCard = createModernFormCard();
        wrapper.add(formCard);
        root.add(wrapper);

        // --- 4. FOOTER ---
        root.add(createFooter());

        // Responsive Resizing Logic
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                formCard.setPreferredSize(new Dimension(w < 900 ? w - 150 : 650, 600));
                formCard.revalidate();
            }
        });

        JScrollPane scroll = new JScrollPane(root);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(25);
        add(scroll);
    }

    private JPanel createHeroHeader() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(80, 0, 20, 0));
        JLabel title = new JLabel("<html><center>Request Support.<br><font color='#dc3545'>Save a Life Today.</font></center></html>");
        title.setFont(new Font("SansSerif", Font.BOLD, 52));
        title.setForeground(DARK_NAVY);
        p.add(title);
        return p;
    }

    private JPanel createImpactThought() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_SOFT);
        p.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 1, 0, new Color(230, 235, 240)),
            new EmptyBorder(60, 100, 60, 100)
        ));
        p.setMaximumSize(new Dimension(5000, 220));

        JLabel thought = new JLabel("<html><center style='width: 900px;'>" +
            "<i style='font-size: 22pt; color: #1e293b; font-family: Serif;'>" +
            "\"Every blood donor is a hero. But every request is a call for that hero to act. " +
            "We are here to ensure that the bridge between a life in need and a life that gives is never broken.\"" +
            "</i></center></html>", SwingConstants.CENTER);
        p.add(thought, BorderLayout.CENTER);
        return p;
    }

    private JPanel createModernFormCard() {
        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBackground(Color.WHITE);
        c.setBorder(new CompoundBorder(
            new LineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(50, 70, 50, 70)
        ));

        JLabel cardTitle = new JLabel("Request Information");
        cardTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
        
        hospitalF = styledTextField("Hospital or Clinic Name");
        unitsF = styledTextField("Number of units required");
        
        bloodGrp = new JComboBox<>(new String[]{"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"});
        bloodGrp.setFont(new Font("SansSerif", Font.PLAIN, 15));
        bloodGrp.setMaximumSize(new Dimension(5000, 55));
        bloodGrp.setBackground(Color.WHITE);

        JButton submit = new JButton("Submit Urgent Request");
        submit.setBackground(PRIMARY_RED);
        submit.setForeground(Color.WHITE);
        submit.setFont(new Font("SansSerif", Font.BOLD, 16));
        submit.setMaximumSize(new Dimension(5000, 60));
        submit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submit.setFocusPainted(false);
        submit.setBorder(null);

        submit.addActionListener(e -> handleSubmission());

        c.add(cardTitle);
        c.add(Box.createVerticalStrut(35));
        c.add(createLabel("Medical Facility")); c.add(hospitalF);
        c.add(Box.createVerticalStrut(25));
        c.add(createLabel("Required Blood Group")); c.add(bloodGrp);
        c.add(Box.createVerticalStrut(25));
        c.add(createLabel("Quantity (Units)")); c.add(unitsF);
        c.add(Box.createVerticalStrut(45));
        c.add(submit);

        return c;
    }

    private JTextField styledTextField(String hint) {
        JTextField f = new JTextField();
        f.setFont(new Font("SansSerif", Font.PLAIN, 15));
        f.setMaximumSize(new Dimension(5000, 50));
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(210, 214, 218), 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        return f;
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 14));
        l.setForeground(SLATE_TEXT);
        l.setBorder(new EmptyBorder(0, 0, 5, 0));
        return l;
    }

    private JPanel createFooter() {
        JPanel f = new JPanel(new FlowLayout(FlowLayout.CENTER));
        f.setBackground(Color.WHITE);
        f.setBorder(new EmptyBorder(40, 0, 100, 0));

        JButton back = new JButton("← Back");
        back.setFont(new Font("SansSerif", Font.BOLD, 16));
        back.setPreferredSize(new Dimension(300, 65));
        back.setBackground(DARK_NAVY);
        back.setForeground(Color.WHITE);
        back.setFocusPainted(false);
        back.setBorder(null);
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));

        back.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { back.setBackground(new Color(30, 41, 59)); }
            public void mouseExited(MouseEvent e) { back.setBackground(DARK_NAVY); }
        });

        back.addActionListener(e -> { new UserHome().setVisible(true); dispose(); });
        f.add(back);
        return f;
    }

    private void handleSubmission() {
        String hName = hospitalF.getText().trim();
        String uText = unitsF.getText().trim();

        if (hName.isEmpty() || uText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int units = Integer.parseInt(uText);
            if (units <= 0 || units > 20) {
                JOptionPane.showMessageDialog(this, "Please enter units between 1 and 20.", "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Connection con = DBConnection.connect();
            String sql = "INSERT INTO blood_requests (hospital_name, blood_group, units_requested, status) VALUES (?, ?, ?, 'Pending')";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, hName);
            ps.setString(2, bloodGrp.getSelectedItem().toString());
            ps.setInt(3, units);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Request successfully submitted to the system.");
            new UserHome().setVisible(true);
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity must be a numeric value.", "Format Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }
}