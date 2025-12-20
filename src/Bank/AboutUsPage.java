package Bank;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class AboutUsPage extends JFrame {
    private static final Color PRIMARY_RED = new Color(220, 53, 69);
    private static final Color DARK_NAVY = new Color(15, 23, 42);
    private static final Color HOVER_NAVY = new Color(30, 41, 59);
    private static final Color SLATE_TEXT = new Color(71, 85, 105);
    private static final Color BG_LIGHT = new Color(250, 251, 253);

    public AboutUsPage() {
        setTitle("Our Mission | LifeLine");
        setSize(1350, 1000);
        setMinimumSize(new Dimension(1150, 900));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(Color.WHITE);

        // --- 1. MINIMALIST HERO ---
        JPanel hero = new JPanel(new FlowLayout(FlowLayout.LEFT));
        hero.setBackground(Color.WHITE);
        hero.setBorder(new EmptyBorder(90, 110, 10, 110));
        JLabel title = new JLabel("<html>Every drop tells a story of <font color='#dc3545'>Hope.</font></html>");
        title.setFont(new Font("SansSerif", Font.BOLD, 64));
        hero.add(title);
        root.add(hero);

        // --- 2. ABOUT US (The Story) ---
        root.add(createStorySection());

        // --- 3. THE CENTERPIECE THOUGHT (Re-designed) ---
        root.add(createElevatedThought());

        // --- 4. DATA INSIGHTS ---
        root.add(createStatsGrid());

        // --- 5. MAXIMUM BREADTH COMPATIBILITY TABLE ---
        root.add(createPremiumTableSection());

        // --- 6. INTERACTIVE FOOTER ---
        root.add(createFooter());

        JScrollPane scrollPane = new JScrollPane(root);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(28);
        add(scrollPane);
    }

    private JPanel createStorySection() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(0, 110, 60, 110));
        JLabel story = new JLabel("<html><body style='width: 800px; font-size: 16pt; color: #475569; line-height: 1.6;'>" +
            "LifeLine was born from a simple mission: to ensure no patient ever has to wait for a miracle. " +
            "We bridge the gap between compassionate donors and hospitals in real-time, making blood " +
            "donation accessible, transparent, and immediate.</body></html>");
        p.add(story);
        return p;
    }

    private JPanel createElevatedThought() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_LIGHT);
        p.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 1, 0, new Color(230, 235, 240)),
            new EmptyBorder(80, 110, 80, 110)
        ));
        p.setMaximumSize(new Dimension(5000, 280));

        JLabel thought = new JLabel("<html><center style='width: 1000px;'>" +
            "<span style='font-size: 40pt; color: #dc3545;'>“</span>" +
            "<span style='font-size: 26pt; color: #1e293b; font-family: Serif; font-style: italic;'>" +
            "The blood you donate gives someone another chance at life. " +
            "One day that someone might be a stranger, a friend, or even you." +
            "</span>" +
            "<span style='font-size: 40pt; color: #dc3545;'> ”</span>" +
            "</center></html>", SwingConstants.CENTER);
        p.add(thought, BorderLayout.CENTER);
        return p;
    }

    private JPanel createStatsGrid() {
        JPanel p = new JPanel(new GridLayout(1, 3, 80, 0));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(90, 110, 80, 110));
        p.setMaximumSize(new Dimension(5000, 200));

        p.add(createDataPoint("1 Donation", "Saves 3 Lives", "Whole blood is separated into three components."));
        p.add(createDataPoint("Every 2 Sec", "A Urgent Need", "Constant demand for surgeries and emergencies."));
        p.add(createDataPoint("100% Gift", "No Substitute", "Only human blood can save human lives."));
        return p;
    }

    private JPanel createDataPoint(String val, String head, String sub) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel v = new JLabel(val); v.setFont(new Font("SansSerif", Font.BOLD, 42)); v.setForeground(PRIMARY_RED);
        JLabel h = new JLabel(head); h.setFont(new Font("SansSerif", Font.BOLD, 22)); h.setForeground(DARK_NAVY);
        JLabel s = new JLabel("<html>" + sub + "</html>"); s.setFont(new Font("SansSerif", Font.PLAIN, 15)); s.setForeground(SLATE_TEXT);
        p.add(v); p.add(Box.createVerticalStrut(8)); p.add(h); p.add(Box.createVerticalStrut(5)); p.add(s);
        return p;
    }

    private JPanel createPremiumTableSection() {
        JPanel p = new JPanel(new BorderLayout(0, 40));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(50, 110, 50, 110));

        JLabel title = new JLabel("Compatibility & Matching Reference");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        p.add(title, BorderLayout.NORTH);

        String tableHtml = "<html><body style='width: 1100px; font-family: SansSerif;'>" +
            "<table width='100%' cellpadding='28' style='border: 1px solid #e2e8f0; border-collapse: collapse; background-color: white;'>" +
            "  <tr style='background-color: #0f172a; color: #ffffff; font-size: 17pt;'>" +
            "    <th align='left'>Blood Group</th><th align='left'>Can Give To</th><th align='left'>Can Receive From</th>" +
            "  </tr>" +
            "  <tr style='font-size: 15pt;'><td><b>O Negative</b></td><td color='#dc3545'><b>Universal Donor</b></td><td>O-</td></tr>" +
            "  <tr style='font-size: 15pt; background-color: #f8fafc;'><td><b>O Positive</b></td><td>O+, A+, B+, AB+</td><td>O+, O-</td></tr>" +
            "  <tr style='font-size: 15pt;'><td><b>AB Positive</b></td><td>AB+ Only</td><td color='#dc3545'><b>Universal Recipient</b></td></tr>" +
            "  <tr style='font-size: 15pt; background-color: #f8fafc;'><td><b>A Positive</b></td><td>A+, AB+</td><td>A+, A-, O+, O-</td></tr>" +
            "  <tr style='font-size: 15pt;'><td><b>B Positive</b></td><td>B+, AB+</td><td>B+, B-, O+, O-</td></tr>" +
            "</table></body></html>";

        p.add(new JLabel(tableHtml), BorderLayout.CENTER);
        return p;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(80, 0, 120, 0));

        JButton back = new JButton("Back");
        back.setFont(new Font("SansSerif", Font.BOLD, 16));
        back.setPreferredSize(new Dimension(300, 65));
        back.setBackground(DARK_NAVY);
        back.setForeground(Color.WHITE);
        back.setFocusPainted(false);
        back.setBorder(null);
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));

        back.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { back.setBackground(HOVER_NAVY); }
            public void mouseExited(MouseEvent e) { back.setBackground(DARK_NAVY); }
        });

        back.addActionListener(e -> { new UserHome().setVisible(true); dispose(); });
        footer.add(back);
        return footer;
    }
}