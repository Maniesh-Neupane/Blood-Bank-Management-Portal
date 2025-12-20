package Bank;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class ContactUsPage extends JFrame {
    private static final Color PRIMARY = new Color(220, 53, 69);
    private static final Color DARK_NAVY = new Color(15, 23, 42);
    private static final Color SLATE = new Color(71, 85, 105);
    private static final Color BG_LIGHT = new Color(248, 250, 252);

    private JPanel mainContent;
    private JScrollPane scrollPane;

    public ContactUsPage() {
        setTitle("Contact Us | LifeLine");
        setSize(1200, 900);
        setMinimumSize(new Dimension(500, 800));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Root setup
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(Color.WHITE);

        // --- 1. Header Section ---
        root.add(createHeader());

        // --- 2. Responsive Container ---
        mainContent = new JPanel(new GridBagLayout());
        mainContent.setBackground(Color.WHITE);
        root.add(mainContent);

        // --- 3. Footer (Back Button) ---
        root.add(createFooter());

        scrollPane = new JScrollPane(root);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        add(scrollPane);

        // Responsive Trigger
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                renderResponsiveLayout();
            }
        });
        
        renderResponsiveLayout();
    }

    private JPanel createHeader() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(60, 20, 20, 20));
        p.setMaximumSize(new Dimension(5000, 150));
        
        JLabel title = new JLabel("How can we help you?");
        title.setFont(new Font("SansSerif", Font.BOLD, 48));
        title.setForeground(DARK_NAVY);
        p.add(title);
        return p;
    }

    private void renderResponsiveLayout() {
        mainContent.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(20, 20, 20, 20);

        if (getWidth() < 950) {
            // Mobile/Stacked Layout
            gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;
            mainContent.add(createContactForm(), gbc);
            gbc.gridy = 1;
            mainContent.add(createSidebarInfo(), gbc);
        } else {
            // Wide Desktop Layout
            gbc.gridy = 0; gbc.gridx = 0; gbc.weightx = 0.6;
            mainContent.add(createContactForm(), gbc);
            gbc.gridx = 1; gbc.weightx = 0.4;
            mainContent.add(createSidebarInfo(), gbc);
        }
        mainContent.revalidate();
        mainContent.repaint();
    }

    private JPanel createContactForm() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new CompoundBorder(new LineBorder(new Color(241, 245, 249), 2), new EmptyBorder(40, 40, 40, 40)));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.weightx = 1.0;

        JLabel label = new JLabel("Send a Message");
        label.setFont(new Font("SansSerif", Font.BOLD, 24));
        c.gridy = 0; c.insets = new Insets(0,0,30,0);
        p.add(label, c);

        c.insets = new Insets(0,0,15,0);
        c.gridy = 1; p.add(createInputWrapper("Full Name", "Enter your name"), c);
        c.gridy = 2; p.add(createInputWrapper("Email", "Enter your email"), c);
        
        c.gridy = 3; 
        JLabel msgLabel = new JLabel("Message");
        msgLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        p.add(msgLabel, c);
        
        JTextArea area = new JTextArea(5, 20);
        area.setBackground(BG_LIGHT);
        area.setBorder(new EmptyBorder(10,10,10,10));
        area.setLineWrap(true);
        c.gridy = 4; p.add(new JScrollPane(area), c);

        JButton send = new JButton("Send Message");
        send.setBackground(PRIMARY);
        send.setForeground(Color.WHITE);
        send.setFont(new Font("SansSerif", Font.BOLD, 15));
        send.setPreferredSize(new Dimension(0, 50));
        send.setCursor(new Cursor(Cursor.HAND_CURSOR));
        c.gridy = 5; c.insets = new Insets(20,0,0,0);
        p.add(send, c);

        return p;
    }

    private JPanel createSidebarInfo() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(DARK_NAVY);
        p.setBorder(new EmptyBorder(50, 40, 50, 40));

        JLabel t = new JLabel("Connect With Us");
        t.setFont(new Font("SansSerif", Font.BOLD, 22));
        t.setForeground(Color.WHITE);
        p.add(t);
        p.add(Box.createVerticalStrut(30));

        p.add(createDetailItem("📍 Location", "Butwal, Nepal"));
        p.add(Box.createVerticalStrut(25));
        p.add(createDetailItem("📞 Support Line", "9867142822"));
        p.add(Box.createVerticalStrut(25));
        p.add(createDetailItem("✉️ Email", "manieshneupane@gmail.com"));

        return p;
    }

    private JPanel createDetailItem(String title, String info) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setForeground(PRIMARY);
        t.setFont(new Font("SansSerif", Font.BOLD, 13));
        JLabel i = new JLabel(info);
        i.setForeground(new Color(200, 200, 200));
        i.setFont(new Font("SansSerif", Font.PLAIN, 15));
        p.add(t, BorderLayout.NORTH);
        p.add(i, BorderLayout.CENTER);
        return p;
    }

    private JPanel createInputWrapper(String label, String placeholder) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.BOLD, 14));
        JTextField f = new JTextField();
        f.setBackground(BG_LIGHT);
        f.setPreferredSize(new Dimension(0, 45));
        f.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(230, 230, 230)), new EmptyBorder(0, 10, 0, 10)));
        p.add(l, BorderLayout.NORTH);
        p.add(f, BorderLayout.CENTER);
        return p;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(40, 0, 80, 0));
        
        JButton back = new JButton("← Back to Home");
        back.setFont(new Font("SansSerif", Font.BOLD, 16));
        back.setPreferredSize(new Dimension(280, 60));
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
        footer.add(back);
        return footer;
    }
}