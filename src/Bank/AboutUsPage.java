package Bank;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;


public class AboutUsPage extends JFrame {
    private static final Color PRIMARY_RED = new Color(225, 29, 72);  // Modern Crimson
    private static final Color TEXT_DARK = new Color(15, 23, 42);     // Deep Slate
    private static final Color TEXT_SLATE = new Color(71, 85, 105);   // Muted Slate
    private static final Color BG_LIGHT = new Color(250, 251, 253);   // Soft Background
    private static final Color BORDER_COLOR = new Color(226, 232, 240);

    public AboutUsPage() {
        setTitle("Our Mission | LifeLine Blood Bank");
        
        //   FULL SCREEN 
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1100, 800));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(Color.WHITE);

        // HERO SECTION
        root.add(createModernHero());

        // THE STORY 
        root.add(createStorySection());

        // IMPACT STATS 
        root.add(createImpactStats());

        // COMPATIBILITY CHART
        root.add(createCompatibilitySection());

        // NAVIGATION
        root.add(createNavigationSection());

        JScrollPane scrollPane = new JScrollPane(root);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(25);
        add(scrollPane);
    }

    private JPanel createModernHero() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_LIGHT);
        p.setBorder(new EmptyBorder(80, 100, 60, 100));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        JLabel title = new JLabel("<html>Every drop tells a story of <font color='#e11d48'>Hope.</font></html>");
        title.setFont(new Font("SansSerif", Font.BOLD, 62));
        title.setForeground(TEXT_DARK);
        
        JLabel sub = new JLabel("We are building the bridge between compassion and crisis.");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 20));
        sub.setForeground(TEXT_SLATE);
        sub.setBorder(new EmptyBorder(20, 0, 0, 0));

        p.add(title, BorderLayout.NORTH);
        p.add(sub, BorderLayout.CENTER);
        return p;
    }

    private JPanel createStorySection() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(80, 100, 80, 100));

        JLabel text = new JLabel("<html><div style='width: 900px; line-height: 1.8;'>" +
            "LifeLine was established to modernize the blood donation process in Nepal. " +
            "By integrating real-time technology with healthcare, we ensure that blood units " +
            "reach those in need without administrative delays. Our platform connects thousands " +
            "of voluntary donors with hospitals and patients instantly.</div></html>");
        text.setFont(new Font("SansSerif", Font.PLAIN, 18));
        text.setForeground(TEXT_SLATE);
        
        p.add(text, BorderLayout.CENTER);
        return p;
    }

    private JPanel createImpactStats() {
        JPanel p = new JPanel(new GridLayout(1, 3, 40, 0));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(40, 100, 60, 100));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        p.add(createStatCard("1 Donation", "Saves 3 Lives", "Components: Red cells, Plasma, Platelets."));
        p.add(createStatCard("Every 2 Sec", "A New Patient", "The demand for blood is constant and urgent."));
        p.add(createStatCard("100% Gift", "Human Only", "Blood cannot be manufactured; only donated."));

        return p;
    }

    private JPanel createStatCard(String val, String head, String sub) {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(BG_LIGHT);
        p.setBorder(new CompoundBorder(new LineBorder(BORDER_COLOR), new EmptyBorder(30, 30, 30, 30)));
        
        JLabel v = new JLabel(val); v.setFont(new Font("SansSerif", Font.BOLD, 38)); v.setForeground(PRIMARY_RED);
        JLabel h = new JLabel(head); h.setFont(new Font("SansSerif", Font.BOLD, 18)); h.setForeground(TEXT_DARK);
        JLabel s = new JLabel("<html>" + sub + "</html>"); s.setFont(new Font("SansSerif", Font.PLAIN, 14)); s.setForeground(TEXT_SLATE);
        
        p.add(v, BorderLayout.NORTH);
        p.add(h, BorderLayout.CENTER);
        p.add(s, BorderLayout.SOUTH);
        return p;
    }

    private JPanel createCompatibilitySection() {
        JPanel p = new JPanel(new BorderLayout(0, 30));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(60, 100, 60, 100));

        JLabel title = new JLabel("Blood Matching Reference");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(TEXT_DARK);
        p.add(title, BorderLayout.NORTH);

        String tableHtml = "<html><body style='width: 1000px;'>" +
            "<table width='100%' cellpadding='20' style='border-collapse: collapse; border: 1px solid #e2e8f0;'>" +
            "  <tr style='background-color: #f1f5f9; color: #0f172a; font-weight: bold;'>" +
            "    <th align='left'>Group</th><th align='left'>Give To (Donation)</th><th align='left'>Receive From</th>" +
            "  </tr>" +
            "  <tr><td><b>O-</b></td><td><b>Universal Donor</b> (All Groups)</td><td>O-</td></tr>" +
            "  <tr style='background-color: #f8fafc;'><td><b>O+</b></td><td>O+, A+, B+, AB+</td><td>O+, O-</td></tr>" +
            "  <tr><td><b>AB+</b></td><td>AB+ Only</td><td><b>Universal Recipient</b> (All)</td></tr>" +
            "  <tr style='background-color: #f8fafc;'><td><b>A+</b></td><td>A+, AB+</td><td>A+, A-, O+, O-</td></tr>" +
            "  <tr><td><b>B+</b></td><td>B+, AB+</td><td>B+, B-, O+, O-</td></tr>" +
            "</table></body></html>";

        JLabel table = new JLabel(tableHtml);
        p.add(table, BorderLayout.CENTER);
        return p;
    }

    private JPanel createNavigationSection() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setBackground(BG_LIGHT);
        p.setBorder(new EmptyBorder(60, 0, 100, 0));

        JButton btnBack = new JButton("RETURN TO HOME");
        btnBack.setPreferredSize(new Dimension(280, 60));
        btnBack.setBackground(TEXT_DARK);
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnBack.setFocusPainted(false);
        btnBack.setBorderPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnBack.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnBack.setBackground(new Color(30, 41, 59)); }
            public void mouseExited(MouseEvent e) { btnBack.setBackground(TEXT_DARK); }
        });

        btnBack.addActionListener(e -> {
            new UserHome().setVisible(true);
            dispose();
        });

        p.add(btnBack);
        return p;
    }
}