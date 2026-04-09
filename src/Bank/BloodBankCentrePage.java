package Bank;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class BloodBankCentrePage extends JFrame {

    private static final Color PRIMARY_RED  = new Color(190, 18, 60);
    private static final Color BG_SOFT      = new Color(248, 250, 252);
    private static final Color CARD_BG      = Color.WHITE;
    private static final Color TEXT_MAIN    = new Color(15, 23, 42);
    private static final Color TEXT_SUB     = new Color(100, 116, 139);
    private static final Color ACCENT_BLUE  = new Color(241, 245, 249);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);

    
    private final JFrame parentDash;

    private JPanel       centerGrid;
    private List<JPanel> allCards = new ArrayList<>();
    private JLabel       noResultsLabel;

    
    public BloodBankCentrePage() {
        this(null);
    }

    //  Constructor 2: opened from UserDashboard — session preserved 
    public BloodBankCentrePage(JFrame parent) {
        this.parentDash = parent;
        buildUI();
    }

    private void buildUI() {
        setTitle("Authorized Facilities | LifeLine");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1100, 750));
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_SOFT);
        root.add(createModernHeader(), BorderLayout.NORTH);

        // Search + grid
        JPanel searchContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchContainer.setOpaque(false);
        searchContainer.setBorder(new EmptyBorder(20, 0, 10, 0));

        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(550, 55));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        searchField.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(0, 20, 0, 20)
        ));
        searchField.setText("Search by city or facility name...");
        searchField.setForeground(Color.GRAY);
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search by city or facility name...")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_MAIN);
                }
            }
            public void focusLost(FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText("Search by city or facility name...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });
        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                String q = searchField.getText().toLowerCase();
                filterCentres(q.equals("search by city or facility name...") ? "" : q);
            }
        });
        searchContainer.add(searchField);

        centerGrid = new JPanel(new GridLayout(0, 3, 25, 25));
        centerGrid.setOpaque(false);
        centerGrid.setBorder(new EmptyBorder(20, 60, 40, 60));

        noResultsLabel = new JLabel("No blood centres found matching your search.", SwingConstants.CENTER);
        noResultsLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
        noResultsLabel.setForeground(TEXT_SUB);
        noResultsLabel.setVisible(false);

        populateCentres();

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(searchContainer, BorderLayout.NORTH);
        contentPanel.add(centerGrid,      BorderLayout.CENTER);
        contentPanel.add(noResultsLabel,  BorderLayout.SOUTH);

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(25);
        root.add(scroll, BorderLayout.CENTER);

        root.add(createModernFooter(), BorderLayout.SOUTH);
        add(root);
    }

 
    private JPanel createModernHeader() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));

        // Slim top bar (crimson) with Back button 
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PRIMARY_RED);
        topBar.setPreferredSize(new Dimension(0, 52));
        topBar.setBorder(new EmptyBorder(0, 40, 0, 40));

        JLabel brand = new JLabel("\uD83E\uDE78  LifeLine Blood Bank");
        brand.setFont(new Font("SansSerif", Font.BOLD, 14));
        brand.setForeground(Color.WHITE);

        String backLabel = (parentDash != null) ? "\u2190  Back to Dashboard" : "\u2190  Back to Home";
        JButton backBtn = new JButton(backLabel);
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(new Color(44, 62, 80));
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.setBorder(new EmptyBorder(8, 18, 8, 18));
        backBtn.addActionListener(e -> goBack());

        topBar.add(brand,   BorderLayout.WEST);
        topBar.add(backBtn, BorderLayout.EAST);

        // White title section
        JPanel titleArea = new JPanel(new GridLayout(2, 1));
        titleArea.setBackground(Color.WHITE);
        titleArea.setBorder(new EmptyBorder(40, 0, 40, 0));

        JLabel title = new JLabel("Authorized Blood Facilities", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 42));
        title.setForeground(TEXT_MAIN);

        JLabel sub = new JLabel("Verified donation centers and hospital blood banks across Nepal", SwingConstants.CENTER);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 18));
        sub.setForeground(TEXT_SUB);

        titleArea.add(title);
        titleArea.add(sub);

        wrapper.add(topBar,     BorderLayout.NORTH);
        wrapper.add(titleArea,  BorderLayout.CENTER);
        return wrapper;
    }

    
    // FOOTER — Back button with session
    
    private JPanel createModernFooter() {
        JPanel f = new JPanel(new FlowLayout(FlowLayout.CENTER));
        f.setBackground(Color.WHITE);
        f.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, BORDER_COLOR),
            new EmptyBorder(24, 0, 40, 0)
        ));

        String backLabel = (parentDash != null) ? "BACK TO DASHBOARD" : "BACK TO HOME";
        JButton back = new JButton(backLabel);
        back.setPreferredSize(new Dimension(280, 55));
        back.setBackground(TEXT_MAIN);
        back.setForeground(Color.WHITE);
        back.setFont(new Font("SansSerif", Font.BOLD, 14));
        back.setFocusPainted(false);
        back.setBorderPainted(false);
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back.addActionListener(e -> goBack());

        // Emergency contact note
        JLabel emergency = new JLabel(
            "  \uD83D\uDCDE Emergency NRCS Hotline: 01-4288485  |  Ambulance: 102  ");
        emergency.setFont(new Font("SansSerif", Font.PLAIN, 12));
        emergency.setForeground(TEXT_SUB);

        f.add(back);
        f.add(Box.createHorizontalStrut(30));
        f.add(emergency);
        return f;
    }

    
    // DATA
    
    private void populateCentres() {
        addCentre("Central NRCS Bureau",   "Soalteemode, Kathmandu",   "01-4288485", "24/7", "National Centre");
        addCentre("Patan Hospital Bank",   "Lagankhel, Lalitpur",      "01-5522265", "24/7", "Public Hospital");
        addCentre("NRCS Regional Dharan",  "BPKIHS Campus, Dharan",    "025-525555", "24/7", "Regional Bureau");
        addCentre("Gandaki Regional Bank", "Pokhara, Kaski",           "061-520067", "24/7", "Regional Bureau");
        addCentre("Bir Hospital Bank",     "New Road Gate, KTM",       "01-4221119", "24/7", "Public Hospital");
        addCentre("NRCS Chitwan",          "Bharatpur, Chitwan",       "056-520880", "24/7", "District Branch");
        addCentre("Lumbini Provincial",    "Butwal, Rupandehi",        "071-540190", "24/7", "Provincial Hospital");
        addCentre("Bheri Hospital Bank",   "Nepalgunj, Banke",         "081-520120", "24/7", "Provincial Hospital");
        addCentre("Koshi Hospital Bank",   "Biratnagar, Morang",       "021-530103", "24/7", "Public Hospital");
    }

    private void addCentre(String name, String loc, String phone, String hours, String type) {
        JPanel card = createModernCard(name, loc, phone, hours, type);
        card.putClientProperty("filter", (name + loc + type).toLowerCase());
        allCards.add(card);
        centerGrid.add(card);
    }

    private void filterCentres(String query) {
        centerGrid.removeAll();
        int count = 0;
        for (JPanel card : allCards) {
            String filterText = (String) card.getClientProperty("filter");
            if (filterText.contains(query) || query.isEmpty()) {
                centerGrid.add(card);
                count++;
            }
        }
        noResultsLabel.setVisible(count == 0);
        centerGrid.revalidate();
        centerGrid.repaint();
    }

    private JPanel createModernCard(String name, String loc, String phone, String hours, String type) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel badge = new JLabel(type.toUpperCase());
        badge.setFont(new Font("SansSerif", Font.BOLD, 10));
        badge.setForeground(PRIMARY_RED);
        badge.setOpaque(true);
        badge.setBackground(new Color(255, 228, 230));
        badge.setBorder(new EmptyBorder(4, 8, 4, 8));

        JPanel badgeWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badgeWrapper.setOpaque(false);
        badgeWrapper.add(badge);

        JPanel info = new JPanel(new GridLayout(3, 1, 0, 5));
        info.setOpaque(false);

        JLabel n = new JLabel(name);
        n.setFont(new Font("SansSerif", Font.BOLD, 19));
        n.setForeground(TEXT_MAIN);

        JLabel l = new JLabel("\uD83D\uDCCD " + loc);
        l.setForeground(TEXT_SUB);

        JLabel t = new JLabel("\uD83D\uDCDE " + phone + "  \u2022  \uD83D\uDD52 " + hours);
        t.setFont(new Font("SansSerif", Font.BOLD, 13));
        t.setForeground(TEXT_MAIN);

        info.add(n); info.add(l); info.add(t);

        card.add(badgeWrapper, BorderLayout.NORTH);
        card.add(info,         BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBorder(new CompoundBorder(
                    new LineBorder(PRIMARY_RED, 1, true), new EmptyBorder(25, 25, 25, 25)));
                card.setBackground(ACCENT_BLUE);
            }
            public void mouseExited(MouseEvent e) {
                card.setBorder(new CompoundBorder(
                    new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(25, 25, 25, 25)));
                card.setBackground(CARD_BG);
            }
        });

        return card;
    }

    
    private void goBack() {
        dispose();
        if (parentDash != null) {
            
            parentDash.setVisible(true);
            parentDash.toFront();
        } else {
            new UserHome().setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BloodBankCentrePage().setVisible(true));
    }
}