package Bank;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class ContactUsPage extends JFrame {
    // Modern "CSS-like" Color Palette (Light Theme)
    private static final Color PRIMARY_RED = new Color(225, 29, 72);   // Modern Medical Red
    private static final Color BG_LIGHT = new Color(249, 250, 251);    // Soft Gray/White background
    private static final Color TEXT_DARK = new Color(17, 24, 39);      // Near black for readability
    private static final Color TEXT_MUTED = new Color(107, 114, 128);  // Gray for subtitles
    private static final Color BORDER_COLOR = new Color(229, 231, 235); // Light borders

    private JPanel mainContent;

    public ContactUsPage() {
        setTitle("Contact Us | LifeLine Blood Bank");
        
        //  Full Screen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 700));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Root Panel
        JPanel root = new JPanel();
        root.setLayout(new BorderLayout());
        root.setBackground(BG_LIGHT);

        // TOP HEADER
        root.add(createModernHeader(), BorderLayout.NORTH);

        // CENTER CONTENT 
        mainContent = new JPanel(new GridBagLayout());
        mainContent.setOpaque(false);
        
        // Create a wrapper to center the form card
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        renderLayout(wrapper);
        
        root.add(new JScrollPane(wrapper), BorderLayout.CENTER);

        // BOTTOM NAVIGATION
        root.add(createNavigationFooter(), BorderLayout.SOUTH);

        add(root);

        // Responsive handling
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                renderLayout(wrapper);
            }
        });
    }

    private JPanel createModernHeader() {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 5));
        p.setBackground(Color.WHITE);
        p.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_COLOR),
            new EmptyBorder(40, 20, 40, 20)
        ));

        JLabel title = new JLabel("How can we help you?", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setForeground(TEXT_DARK);

        JLabel sub = new JLabel("Our medical team usually responds within 2 hours.", SwingConstants.CENTER);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 16));
        sub.setForeground(TEXT_MUTED);

        p.add(title);
        p.add(sub);
        return p;
    }

    private void renderLayout(JPanel container) {
        container.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(40, 20, 40, 20);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        if (getWidth() < 900) {
            gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;
            container.add(createFormCard(), gbc);
            gbc.gridy = 1;
            container.add(createInfoCard(), gbc);
        } else {
            gbc.gridy = 0; gbc.gridx = 0; gbc.weightx = 0.6;
            container.add(createFormCard(), gbc);
            gbc.gridx = 1; gbc.weightx = 0.4;
            container.add(createInfoCard(), gbc);
        }
        container.revalidate();
        container.repaint();
    }

    private JPanel createFormCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 30, 10, 30);
        c.gridx = 0; c.weightx = 1.0;

        // Label
        JLabel head = new JLabel("Send Message");
        head.setFont(new Font("SansSerif", Font.BOLD, 22));
        c.gridy = 0; c.insets = new Insets(30, 30, 20, 30);
        card.add(head, c);

        // Inputs
        c.insets = new Insets(10, 30, 10, 30);
        c.gridy = 1; card.add(createField("Full Name", "Enter your name..."), c);
        c.gridy = 2; card.add(createField("Email Address", "Enter your email..."), c);
        
        c.gridy = 3; card.add(new JLabel("Message"), c);
        JTextArea msg = new JTextArea(5, 20);
        msg.setBorder(BorderFactory.createCompoundBorder(new LineBorder(BORDER_COLOR), new EmptyBorder(10,10,10,10)));
        msg.setBackground(BG_LIGHT);
        c.gridy = 4; card.add(new JScrollPane(msg), c);

        // SEND BUTTON
        JButton btnSend = new JButton("SEND MESSAGE");
        btnSend.setFocusPainted(false);
        btnSend.setBackground(PRIMARY_RED);
        btnSend.setForeground(Color.WHITE);
        btnSend.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnSend.setPreferredSize(new Dimension(0, 50));
        btnSend.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        c.gridy = 5; c.insets = new Insets(20, 30, 40, 30);
        card.add(btnSend, c);

        return card;
    }

    private JPanel createInfoCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(243, 244, 246));
        card.setBorder(new EmptyBorder(40, 40, 40, 40));

        String[][] details = {
            {"📍 LOCATION", "Butwal, Rupandehi, Nepal"},
            {"📞 SUPPORT", "+977 9867142822"},
            {"✉️ EMAIL", "help@lifeline.com"}
        };

        for (String[] d : details) {
            JLabel t = new JLabel(d[0]);
            t.setForeground(PRIMARY_RED);
            t.setFont(new Font("SansSerif", Font.BOLD, 12));
            JLabel v = new JLabel(d[1]);
            v.setFont(new Font("SansSerif", Font.PLAIN, 16));
            v.setForeground(TEXT_DARK);
            card.add(t);
            card.add(Box.createVerticalStrut(5));
            card.add(v);
            card.add(Box.createVerticalStrut(25));
        }

        return card;
    }

    private JPanel createField(String label, String hint) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        JTextField f = new JTextField();
        f.setPreferredSize(new Dimension(0, 40));
        f.setBorder(BorderFactory.createCompoundBorder(new LineBorder(BORDER_COLOR), new EmptyBorder(0,10,0,10)));
        p.add(l, BorderLayout.NORTH);
        p.add(f, BorderLayout.CENTER);
        return p;
    }

    private JPanel createNavigationFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(20, 20, 40, 20));

        // BACK BUTTON
        JButton btnBack = new JButton("← BACK TO HOME");
        btnBack.setPreferredSize(new Dimension(250, 55));
        btnBack.setBackground(TEXT_DARK);
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnBack.setFocusPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnBack.addActionListener(e -> {
            new UserHome().setVisible(true);
            this.dispose();
        });

        footer.add(btnBack);
        return footer;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ContactUsPage().setVisible(true));
    }
}