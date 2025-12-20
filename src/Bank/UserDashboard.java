package Bank;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class UserDashboard extends JFrame {

    private static final Color PRIMARY_RED = new Color(220, 38, 38);
    private static final Color SUCCESS_GREEN = new Color(22, 163, 74);
    private static final Color WARNING_AMBER = new Color(217, 119, 6);
    private static final Color BG_NAVY = new Color(15, 23, 42);
    private static final Color TEXT_SLATE = new Color(71, 85, 105);
    private static final Color ACCENT_BLUE = new Color(37, 99, 235);

    private String currentUserName;
    private String currentUserBloodGroup;
    private JLabel lblTime;
    private JPanel contentWrapper; // Added to facilitate refreshing
    private int userPoints = 0;
    private int donationCount = 0;

    public UserDashboard(String userName, String bloodGroup) {
        this.currentUserName = userName;
        this.currentUserBloodGroup = bloodGroup;

        fetchUserData(); 

        setTitle("LifeLine Portal | " + userName);
        setSize(1300, 850);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(248, 250, 252));

        // Use a wrapper panel to allow removing/re-adding content on refresh
        contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);
        
        buildUI();
        add(contentWrapper);
        startClock(); 
    }

    /**
     * CRITICAL FIX: This is the method DonationForm calls to update the UI
     */
    public void refreshDashboard() {
        fetchUserData();    // Sync variables with DB
        buildUI();          // Rebuild the UI components
        this.revalidate();
        this.repaint();
    }

    private void buildUI() {
        contentWrapper.removeAll();
        
        JPanel header = createModernHeader();
        contentWrapper.add(header, BorderLayout.NORTH);
        
        JPanel content = createMainContent();
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        contentWrapper.add(scroll, BorderLayout.CENTER);
        
        contentWrapper.revalidate();
    }

    private void fetchUserData() {
        try (Connection con = DBConnection.connect()) {
            String sql = "SELECT points, donation_count FROM users WHERE name = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, currentUserName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                this.userPoints = rs.getInt("points");
                this.donationCount = rs.getInt("donation_count");
            }
        } catch (Exception e) {
            System.err.println("Dashboard Sync Error: " + e.getMessage());
        }
    }

    private JPanel createModernHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 80));
        header.setBorder(new MatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        left.setBorder(new EmptyBorder(15, 40, 15, 0));
        JLabel welcome = new JLabel("Welcome, " + currentUserName);
        welcome.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTime = new JLabel("Loading Time..."); // Placeholder for clock
        lblTime.setForeground(TEXT_SLATE);
        left.add(welcome);
        left.add(lblTime);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        right.setOpaque(false);
        right.setBorder(new EmptyBorder(0, 0, 0, 40));

        JButton btnDonate = new JButton("🩸 Donate Blood");
        
        if (isNotEligibleYet()) {
            styleButton(btnDonate, Color.LIGHT_GRAY);
            btnDonate.setEnabled(false);
            btnDonate.setToolTipText("Recovery period active.");
        } else {
            styleButton(btnDonate, PRIMARY_RED);
        }

        btnDonate.addActionListener(e -> {
            new DonationForm(this, currentUserName, currentUserBloodGroup).setVisible(true);
        });

        JButton btnLogout = new JButton("Logout");
        styleButton(btnLogout, BG_NAVY);
        btnLogout.addActionListener(e -> { 
            dispose(); 
            new UserHome().setVisible(true); //redirect after logout
        });

        right.add(btnDonate);
        right.add(btnLogout);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private boolean isNotEligibleYet() {
        try (Connection con = DBConnection.connect()) {
            String sql = "SELECT last_donation_date FROM users WHERE name = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, currentUserName);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getDate("last_donation_date") != null) {
                LocalDate last = rs.getDate("last_donation_date").toLocalDate();
                return ChronoUnit.MONTHS.between(last, LocalDate.now()) < 3;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    private JPanel createMainContent() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(30, 40, 30, 40));

        main.add(createEligibilityCard());
        main.add(Box.createVerticalStrut(30));

        JLabel stockLbl = new JLabel("⚠️ Critical Stock Alerts");
        stockLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        main.add(stockLbl);
        main.add(Box.createVerticalStrut(10));
        main.add(createStockAlertPanel());
        main.add(Box.createVerticalStrut(30));

        // Point-based Ranking System
        String rankTitle = userPoints >= 100 ? "Gold Hero" : (userPoints >= 50 ? "Silver Donor" : "Bronze Donor");

        JPanel stats = new JPanel(new GridLayout(1, 4, 20, 0));
        stats.setOpaque(false);
        stats.setMaximumSize(new Dimension(1600, 140));
        
        stats.add(new StatCard("Blood Group", currentUserBloodGroup, "Compatible: " + currentUserBloodGroup, ACCENT_BLUE));
        stats.add(new StatCard("Donations", donationCount + " Times", "Total Life Saved", SUCCESS_GREEN));
        stats.add(new StatCard("Your Points", userPoints + " pts", "+10 per donation", WARNING_AMBER));
        stats.add(new StatCard("Achievement", rankTitle, "Verified Level ✅", PRIMARY_RED));
        
        main.add(stats);
        main.add(Box.createVerticalStrut(40));

        JLabel historyLbl = new JLabel("📊 Recent Donation History");
        historyLbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        main.add(historyLbl);
        main.add(Box.createVerticalStrut(15));
        main.add(createHistoryTable());

        return main;
    }

    private JPanel createEligibilityCard() {
        JPanel card = new JPanel(new BorderLayout(20, 0));
        card.setBackground(new Color(240, 253, 244)); 
        card.setBorder(new LineBorder(SUCCESS_GREEN, 1, true));
        card.setMaximumSize(new Dimension(1600, 80));
        String text = "Checking eligibility...";
        try (Connection con = DBConnection.connect()) {
            String sql = "SELECT last_donation_date FROM users WHERE name = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, currentUserName);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getDate("last_donation_date") != null) {
                LocalDate last = rs.getDate("last_donation_date").toLocalDate();
                LocalDate next = last.plusMonths(3);
                long days = ChronoUnit.DAYS.between(LocalDate.now(), next);
                if (days > 0) {
                    card.setBackground(new Color(255, 251, 235)); 
                    card.setBorder(new LineBorder(WARNING_AMBER));
                    text = "<html><b>Eligibility:</b> Next donation in <b>" + days + " days</b> (" + next + ").</html>";
                } else {
                    text = "<html><b>Status: Fit to Donate!</b> You are healthy and ready to save lives.</html>";
                }
            } else { text = "<html><b>Status: Fit to Donate!</b> Ready to save a life today.</html>"; }
        } catch (Exception e) { text = "Status: Data temporarily unavailable."; }
        JLabel lbl = new JLabel(text);
        lbl.setBorder(new EmptyBorder(15, 20, 15, 20));
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 15));
        card.add(lbl, BorderLayout.CENTER);
        return card;
    }

    private JPanel createStockAlertPanel() {
        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        container.setOpaque(false);
        try (Connection con = DBConnection.connect()) {
            String sql = "SELECT blood_group, total_units FROM blood_stock_summary WHERE total_units < 10";
            ResultSet rs = con.createStatement().executeQuery(sql);
            boolean foundLowStock = false;
            while (rs.next()) {
                foundLowStock = true;
                String gp = rs.getString("blood_group");
                int unit = rs.getInt("total_units");
                JPanel alert = new JPanel();
                alert.setBackground(unit < 5 ? new Color(254, 242, 242) : Color.WHITE);
                alert.setBorder(new LineBorder(unit < 5 ? PRIMARY_RED : Color.LIGHT_GRAY, 1, true));
                alert.add(new JLabel("<html><b>" + gp + "</b>: " + unit + " units</html>"));
                container.add(alert);
            }
            if(!foundLowStock) container.add(new JLabel("All blood stock levels are currently stable."));
        } catch (Exception e) { container.add(new JLabel("Database error reading stock alerts.")); }
        return container;
    }

    private JScrollPane createHistoryTable() {
        String[] cols = {"Date", "Blood Group", "Phone", "City"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        try (Connection con = DBConnection.connect()) {
            String sql = "SELECT donation_date, blood_group, phone, city FROM donors WHERE name = ? ORDER BY donation_date DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, currentUserName);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                model.addRow(new Object[]{rs.getTimestamp(1), rs.getString(2), rs.getString(3), rs.getString(4)});
            }
        } catch (Exception e) { System.err.println("Table load error: " + e.getMessage()); }
        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        return new JScrollPane(table);
    }

    private void startClock() {
        Timer t = new Timer(1000, e -> {
            // Nepal Standard Time (UTC+5:45)
            ZonedDateTime nst = ZonedDateTime.now(ZoneId.of("Asia/Kathmandu"));
            if(lblTime != null) {
                lblTime.setText("NST: " + nst.format(DateTimeFormatter.ofPattern("yyyy-MM-dd | HH:mm:ss")));
            }
        });
        t.start();
    }

    private void styleButton(JButton b, Color c) {
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(8, 20, 8, 20));
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    class StatCard extends JPanel {
        public StatCard(String t, String v, String f, Color c) {
            setLayout(new GridLayout(3, 1));
            setBackground(Color.WHITE);
            setBorder(new CompoundBorder(new LineBorder(new Color(226,232,240)), new EmptyBorder(15,20,15,20)));
            JLabel title = new JLabel(t.toUpperCase()); 
            title.setFont(new Font("SansSerif", Font.BOLD, 11));
            title.setForeground(TEXT_SLATE);
            JLabel val = new JLabel(v); 
            val.setFont(new Font("SansSerif", Font.BOLD, 24)); 
            val.setForeground(c);
            JLabel footer = new JLabel(f); 
            footer.setForeground(TEXT_SLATE);
            footer.setFont(new Font("SansSerif", Font.PLAIN, 11));
            add(title); add(val); add(footer);
        }
    }
}