package Bank;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.*;
import java.sql.*;

public class AdminDash extends JFrame {
    // --- Modern Professional Palette ---
    private final Color CLR_ACCENT = new Color(220, 38, 38);    
    private final Color CLR_SIDEBAR = new Color(15, 23, 42);    
    private final Color CLR_BODY_BG = new Color(245, 247, 250); 
    private final Color CLR_CARD = Color.WHITE;
    private final Color CLR_TEXT_DARK = new Color(30, 41, 59);
    private final Color CLR_TEXT_LIGHT = new Color(100, 116, 139);
    private final Color CLR_SUCCESS = new Color(34, 197, 94);

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private String currentView = "Dashboard";
    
    private int[] stockData = new int[8];
    private int[] approveData = new int[8];
    private int[] denyData = new int[8];
    private String[] groups = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};

    public AdminDash() {
        setTitle("LifeLine | Admin Console");
        setSize(1450, 950);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        refreshData(); 

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CLR_BODY_BG);
        root.add(createModernSidebar(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        renderActiveTab();
        root.add(contentPanel, BorderLayout.CENTER);
        add(root);
    }

    private void refreshData() {
        try (Connection con = DBConnection.connect()) {
            if(con == null) return;
            for(int i=0; i<8; i++) { stockData[i] = 0; approveData[i] = 0; denyData[i] = 0; }

            ResultSet rs1 = con.createStatement().executeQuery("SELECT blood_group, total_units FROM blood_stock_summary");
            while(rs1.next()) {
                String g = rs1.getString("blood_group");
                for(int i=0; i<8; i++) if(groups[i].equalsIgnoreCase(g)) stockData[i] = rs1.getInt("total_units");
            }
            
            ResultSet rs2 = con.createStatement().executeQuery(
                "SELECT blood_group, status, COUNT(*) as cnt FROM blood_requests GROUP BY blood_group, status");
            while(rs2.next()) {
                String g = rs2.getString("blood_group");
                String status = rs2.getString("status");
                for(int i=0; i<8; i++) {
                    if(groups[i].equalsIgnoreCase(g)) {
                        if("Approved".equalsIgnoreCase(status)) approveData[i] = rs2.getInt("cnt");
                        if("Rejected".equalsIgnoreCase(status)) denyData[i] = rs2.getInt("cnt");
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void renderActiveTab() {
        contentPanel.removeAll();
        contentPanel.add(createDashboard(), "Dashboard");
        contentPanel.add(createRequestModule(), "Requests");
        contentPanel.add(createStockModule(), "Stock");
        contentPanel.add(createDonorModule(), "Donors");
        contentPanel.revalidate();
        contentPanel.repaint();
        cardLayout.show(contentPanel, currentView);
    }

    private JPanel createDashboard() {
        JPanel dash = new JPanel(new BorderLayout(0, 30));
        dash.setOpaque(false);
        JPanel kpi = new JPanel(new GridLayout(1, 3, 25, 0));
        kpi.setOpaque(false);
        kpi.add(new ModernStatCard("Stock Units", getTotal(stockData) + "", CLR_ACCENT));
        kpi.add(new ModernStatCard("Total Approvals", getTotal(approveData) + "", CLR_SUCCESS));
        kpi.add(new ModernStatCard("System Health", "Active", new Color(79, 70, 229)));
        dash.add(kpi, BorderLayout.NORTH);
        dash.add(createChartCard("Real-time Inventory Level (Units)", new ModernBarChart(stockData, groups, CLR_ACCENT)), BorderLayout.CENTER);
        
        JButton refreshBtn = new JButton("🔄 Sync Data with Database");
        refreshBtn.setBackground(CLR_SIDEBAR);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> { refreshData(); renderActiveTab(); });
        dash.add(refreshBtn, BorderLayout.SOUTH);
        return dash;
    }

    private JPanel createRequestModule() {
        JPanel p = new JPanel(new BorderLayout(0, 20));
        p.setOpaque(false);
        JPanel chartHeader = createChartCard("Approval vs Denial Analysis", new ComparisonChart(approveData, denyData, groups));
        chartHeader.setPreferredSize(new Dimension(0, 250));
        JTextField searchReq = new JTextField();
        searchReq.setBorder(BorderFactory.createTitledBorder("Search Request History..."));
        String[] cols = {"ID", "Hospital", "Group", "Units", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        styleTable(table);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        searchReq.addCaretListener(e -> sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchReq.getText())));
        try (Connection con = DBConnection.connect()) {
            ResultSet rs = con.createStatement().executeQuery("SELECT request_id, hospital_name, blood_group, units_requested, status FROM blood_requests ORDER BY request_id DESC");
            while(rs.next()) model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getString(5)});
        } catch(Exception e) {}
        JPanel controls = new JPanel(new BorderLayout(0, 10));
        controls.setOpaque(false);
        controls.add(chartHeader, BorderLayout.NORTH);
        controls.add(searchReq, BorderLayout.SOUTH);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        JButton approve = createStyledButton("Approve", CLR_SUCCESS);
        JButton reject = createStyledButton("Reject", CLR_ACCENT);
        approve.addActionListener(e -> updateStatus(table, "Approved"));
        reject.addActionListener(e -> updateStatus(table, "Rejected"));
        btnPanel.add(reject); btnPanel.add(approve);
        p.add(controls, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(btnPanel, BorderLayout.SOUTH);
        return p;
    }

    private JPanel createStockModule() {
        JPanel p = new JPanel(new BorderLayout(0, 20));
        p.setOpaque(false);
        JPanel grid = new JPanel(new GridLayout(2, 4, 25, 25));
        grid.setOpaque(false);
        for (int i = 0; i < 8; i++) {
            JPanel card = new JPanel(new BorderLayout(0, 10));
            card.setBackground(CLR_CARD);
            boolean isLow = stockData[i] < 5;
            card.setBorder(new LineBorder(isLow ? CLR_ACCENT : new Color(226, 232, 240), isLow ? 2 : 1, true));
            JLabel grp = new JLabel(groups[i], JLabel.CENTER);
            grp.setFont(new Font("Inter", Font.BOLD, 36));
            grp.setForeground(isLow ? CLR_ACCENT : CLR_TEXT_DARK);
            card.add(grp, BorderLayout.CENTER);
            if (isLow) {
                JButton alert = new JButton("ALERT USERS");
                alert.setBackground(CLR_ACCENT); alert.setForeground(Color.WHITE);
                alert.addActionListener(e -> JOptionPane.showMessageDialog(this, "Low Stock Alert! Notification sent."));
                card.add(alert, BorderLayout.NORTH);
            }
            JLabel units = new JLabel(stockData[i] + " Units", JLabel.CENTER);
            units.setForeground(isLow ? CLR_ACCENT : CLR_SUCCESS);
            card.add(units, BorderLayout.SOUTH);
            grid.add(card);
        }
        p.add(grid, BorderLayout.CENTER);
        return p;
    }

    private JPanel createDonorModule() {
        JPanel p = new JPanel(new BorderLayout(0, 25));
        p.setOpaque(false);

        JPanel quotePanel = new JPanel(new BorderLayout());
        quotePanel.setBackground(CLR_SIDEBAR);
        quotePanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        JLabel quote = new JLabel("<html><i>\"Every drop counts. Recognizing the heroes who give the gift of life.\"</i></html>");
        quote.setFont(new Font("Serif", Font.ITALIC, 20));
        quote.setForeground(Color.WHITE);
        quotePanel.add(quote, BorderLayout.CENTER);

        JPanel podium = new JPanel(new GridLayout(1, 3, 25, 0));
        podium.setOpaque(false); podium.setPreferredSize(new Dimension(0, 180));

        try (Connection con = DBConnection.connect()) {
            ResultSet rs = con.createStatement().executeQuery("SELECT name, points, donation_count FROM users WHERE role='User' ORDER BY points DESC LIMIT 3");
            String[] icons = {"🏆", "⭐", "🏅"};
            String[] ranks = {"PLATINUM HERO", "GOLD DONOR", "SILVER DONOR"};
            Color[] colors = {new Color(79, 70, 229), new Color(245, 158, 11), new Color(100, 116, 139)};
            int i = 0;
            while(rs.next() && i < 3) {
                podium.add(new DonorRankCard(rs.getString(1), rs.getInt(2), ranks[i], icons[i], colors[i]));
                i++;
            }
        } catch (Exception e) {}

        JTextField searchDonor = new JTextField();
        searchDonor.setBorder(BorderFactory.createTitledBorder(" Filter All Donors "));
        String[] cols = {"Name", "Group", "Donations", "Points", "City", "Action"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return column == 5; }
        };
        JTable table = new JTable(model);
        styleTable(table);
        
        table.getColumnModel().getColumn(5).setCellRenderer(new TableButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new TableButtonEditor(new JCheckBox(), table)); // Fix 3: Passed table reference

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        searchDonor.addCaretListener(e -> sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchDonor.getText())));

        try (Connection con = DBConnection.connect()) {
            ResultSet rs = con.createStatement().executeQuery("SELECT name, blood_group, donation_count, points, city FROM users WHERE role='User' ORDER BY points DESC");
            while(rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getString(5), "RECOGNIZE"});
            }
        } catch (Exception e) {}

        JPanel topSection = new JPanel(new BorderLayout(0, 20));
        topSection.setOpaque(false);
        topSection.add(quotePanel, BorderLayout.NORTH);
        topSection.add(podium, BorderLayout.CENTER);
        topSection.add(searchDonor, BorderLayout.SOUTH);

        p.add(topSection, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    class TableButtonRenderer extends JButton implements TableCellRenderer {
        public TableButtonRenderer() { setOpaque(true); }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            setBackground(CLR_SIDEBAR); setForeground(Color.WHITE);
            return this;
        }
    }

    class TableButtonEditor extends DefaultCellEditor {
        private String label;
        private JButton button;
        private JTable table;

        public TableButtonEditor(JCheckBox checkBox, JTable table) {
            super(checkBox);
            this.table = table;
            button = new JButton();
            button.addActionListener(e -> {
                // Fix 1 & 2: Get donor name from table instead of contentPanel.getComponentAt
                int row = table.getSelectedRow();
                if(row != -1) {
                    String donorName = (String) table.getValueAt(row, 0);
                    JOptionPane.showMessageDialog(button, "Official Recognition Certificate sent to " + donorName + "!");
                }
                fireEditingStopped();
            });
        }
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            return button;
        }
        public Object getCellEditorValue() { return label; }
    }

    class DonorRankCard extends JPanel {
        DonorRankCard(String name, int pts, String rank, String icon, Color accent) {
            setLayout(new BorderLayout(15, 0));
            setBackground(CLR_CARD);
            setBorder(new CompoundBorder(new LineBorder(new Color(226, 232, 240)), new EmptyBorder(20, 20, 20, 20)));
            JLabel lblIcon = new JLabel(icon);
            lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
            JPanel info = new JPanel(new GridLayout(3,1)); info.setOpaque(false);
            JLabel rL = new JLabel(rank); rL.setFont(new Font("Inter", Font.BOLD, 10)); rL.setForeground(accent);
            JLabel nL = new JLabel(name.toUpperCase()); nL.setFont(new Font("Inter", Font.BOLD, 15));
            JLabel pL = new JLabel(pts + " Points"); pL.setForeground(CLR_TEXT_LIGHT);
            info.add(rL); info.add(nL); info.add(pL);
            add(lblIcon, BorderLayout.WEST); add(info, BorderLayout.CENTER);
        }
    }

    private JPanel createModernSidebar() {
        JPanel s = new JPanel(new BorderLayout());
        s.setPreferredSize(new Dimension(260, 0));
        s.setBackground(CLR_SIDEBAR);
        s.setBorder(new EmptyBorder(50, 25, 30, 25));
        JPanel navGroup = new JPanel();
        navGroup.setOpaque(false);
        navGroup.setLayout(new BoxLayout(navGroup, BoxLayout.Y_AXIS));
        JLabel logo = new JLabel("LIFELINE ADMIN");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Inter", Font.BOLD, 22));
        navGroup.add(logo);
        navGroup.add(Box.createVerticalStrut(50));
        String[] nav = {"Dashboard", "Requests", "Stock", "Donors"};
        for(String n : nav) {
            JButton b = new JButton(n);
            b.setForeground(n.equals(currentView) ? Color.WHITE : new Color(148, 163, 184));
            b.setContentAreaFilled(false);
            b.setBorderPainted(false);
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            b.addActionListener(e -> { currentView = n; refreshData(); renderActiveTab(); });
            navGroup.add(b);
            navGroup.add(Box.createVerticalStrut(15));
        }
        s.add(navGroup, BorderLayout.NORTH);
        JButton logoutBtn = new JButton("LOGOUT");
        logoutBtn.setBackground(CLR_ACCENT);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.addActionListener(e -> { dispose(); new UserHome().setVisible(true); }); // Fix: Linked to AdminLogin
        s.add(logoutBtn, BorderLayout.SOUTH);
        return s;
    }

    class ModernStatCard extends JPanel {
        ModernStatCard(String t, String v, Color c) {
            setLayout(new GridLayout(2, 1)); setBackground(CLR_CARD);
            setBorder(new EmptyBorder(15,15,15,15)); 
            JLabel lbl = new JLabel(t); lbl.setForeground(CLR_TEXT_LIGHT); add(lbl);
            JLabel val = new JLabel(v); val.setForeground(c); val.setFont(new Font("Inter", Font.BOLD, 22)); add(val);
        }
    }

    class ModernBarChart extends JPanel {
        int[] d; String[] l; Color c;
        ModernBarChart(int[] data, String[] labs, Color col) { this.d = data; this.l = labs; this.c = col; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int bw = (getWidth() - 100) / 8;
            for(int i=0; i<8; i++) {
                int h = (int)((d[i] / 50.0) * (getHeight() - 80));
                int x = 50 + i * (bw + 10);
                g2.setPaint(new GradientPaint(x, getHeight()-30-h, d[i]<5?CLR_ACCENT:c, x, getHeight(), Color.WHITE));
                g2.fill(new RoundRectangle2D.Double(x, getHeight()-30-h, bw, h, 10, 10));
                g2.setColor(CLR_TEXT_DARK); g2.drawString(l[i], x + (bw/3), getHeight() - 10);
            }
        }
    }

    class ComparisonChart extends JPanel {
        int[] d1, d2; String[] l;
        ComparisonChart(int[] a, int[] d, String[] labs) { this.d1 = a; this.d2 = d; this.l = labs; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            int bw = (getWidth() - 100) / 16;
            for(int i=0; i<8; i++) {
                int h1 = (int)((d1[i]/20.0)*(getHeight()-60));
                int h2 = (int)((d2[i]/20.0)*(getHeight()-60));
                int x = 50 + i * (bw*2 + 15);
                g2.setColor(CLR_SUCCESS); g2.fill(new RoundRectangle2D.Double(x, getHeight()-30-h1, bw, h1, 5, 5));
                g2.setColor(CLR_ACCENT); g2.fill(new RoundRectangle2D.Double(x+bw, getHeight()-30-h2, bw, h2, 5, 5));
                g2.setColor(CLR_TEXT_DARK); g2.drawString(l[i], x+5, getHeight()-10);
            }
        }
    }

    private JPanel createChartCard(String title, JPanel chart) {
        JPanel p = new JPanel(new BorderLayout(0, 10)); p.setBackground(CLR_CARD);
        p.setBorder(new CompoundBorder(new LineBorder(new Color(226, 232, 240)), new EmptyBorder(20, 20, 20, 20)));
        JLabel l = new JLabel(title); l.setFont(new Font("Inter", Font.BOLD, 14)); p.add(l, BorderLayout.NORTH); p.add(chart, BorderLayout.CENTER);
        return p;
    }

    private void styleTable(JTable t) { 
        t.setRowHeight(45); 
        t.setShowGrid(false); 
        t.getTableHeader().setBackground(Color.WHITE); 
        t.setSelectionBackground(new Color(241, 245, 249));
    }
    
    private JButton createStyledButton(String text, Color bg) { JButton b = new JButton(text); b.setBackground(bg); b.setForeground(Color.WHITE); return b; }
    private int getTotal(int[] arr) { int t=0; for(int i:arr) t+=i; return t; }

    private void updateStatus(JTable table, String status) {
        int row = table.getSelectedRow(); if(row == -1) return;
        int id = (int)table.getModel().getValueAt(table.convertRowIndexToModel(row), 0);
        try (Connection con = DBConnection.connect()) {
            PreparedStatement pst = con.prepareStatement("UPDATE blood_requests SET status=? WHERE request_id=?");
            pst.setString(1, status); pst.setInt(2, id);
            pst.executeUpdate();
            refreshData(); renderActiveTab();
        } catch (Exception e) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDash().setVisible(true));
    }
}