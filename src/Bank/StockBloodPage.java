package Bank;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashMap;

public class StockBloodPage extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JLabel totalUnitsVal, criticalGroupsVal;

    
    private static final Color ACCENT_RED = new Color(231, 76, 60);
    private static final Color DARK_ONYX = new Color(52, 73, 94);
    private static final Color BG_LIGHT = new Color(245, 247, 250);

    public StockBloodPage() {
        setTitle("Blood Bank Management System - Stock Overview");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1200, 800));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_LIGHT);

        // HEADER METRICS
        JPanel metricsPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        metricsPanel.setBackground(DARK_ONYX);
        metricsPanel.setBorder(new EmptyBorder(40, 80, 40, 80));

        totalUnitsVal = new JLabel("0");
        criticalGroupsVal = new JLabel("0");

        metricsPanel.add(createMetricCard("TOTAL STOCK UNITS", totalUnitsVal, new Color(52, 152, 219)));
        metricsPanel.add(createMetricCard("URGENT SHORTAGES", criticalGroupsVal, ACCENT_RED));

        // SEARCH & ACTIONS BAR
        JPanel controlBar = new JPanel(new BorderLayout());
        controlBar.setOpaque(false);
        controlBar.setBorder(new EmptyBorder(20, 80, 10, 80));

        searchField = new JTextField(" Search Blood Group...");
        searchField.setPreferredSize(new Dimension(350, 45));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        searchField.setForeground(Color.GRAY);
        
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals(" Search Blood Group...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
        });

        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { loadStockData(searchField.getText().trim()); }
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttons.setOpaque(false);
        
        JButton refreshBtn = createStyledButton("Refresh", new Color(44, 62, 80));
        JButton backBtn = createStyledButton("Back to Home", ACCENT_RED);
        
        refreshBtn.addActionListener(e -> loadStockData(""));

        
        backBtn.addActionListener(e -> { 
            new UserHome().setVisible(true); 
            dispose(); 
        });

        buttons.add(refreshBtn);
        buttons.add(backBtn);

        controlBar.add(searchField, BorderLayout.WEST);
        controlBar.add(buttons, BorderLayout.EAST);

        // DATA TABLE 
        String[] cols = {"BLOOD GROUP", "UNITS REMAINING", "INVENTORY STATUS", "COMPATIBILITY"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        
        table = new JTable(model);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(10, 80, 50, 80));
        scrollPane.getViewport().setBackground(Color.WHITE);

        
        JPanel topArea = new JPanel();
        topArea.setLayout(new BoxLayout(topArea, BoxLayout.Y_AXIS));
        topArea.add(metricsPanel);
        topArea.add(controlBar);
        topArea.setOpaque(false);

        root.add(topArea, BorderLayout.NORTH);
        root.add(scrollPane, BorderLayout.CENTER);

        add(root);
        loadStockData(""); 
    }

    private JPanel createMetricCard(String title, JLabel val, Color lineCol) {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(false);
        
        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.BOLD, 12));
        t.setForeground(new Color(180, 190, 200));
        
        val.setFont(new Font("SansSerif", Font.BOLD, 40));
        val.setForeground(Color.WHITE);

        JPanel line = new JPanel();
        line.setPreferredSize(new Dimension(0, 4));
        line.setBackground(lineCol);

        card.add(t, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        card.add(line, BorderLayout.SOUTH);
        return card;
    }

    private void styleTable() {
        table.setRowHeight(65);
        table.setFont(new Font("SansSerif", Font.PLAIN, 15));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setPreferredSize(new Dimension(0, 50));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(240, 240, 240));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, center);

        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(JLabel.CENTER);
                String s = (String) value;
                if ("CRITICAL".equals(s)) l.setForeground(ACCENT_RED);
                else if ("LOW".equals(s)) l.setForeground(new Color(243, 156, 18));
                else l.setForeground(new Color(46, 204, 113));
                l.setFont(new Font("SansSerif", Font.BOLD, 14));
                return l;
            }
        });
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(140, 40));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void loadStockData(String filter) {
        model.setRowCount(0);
        int total = 0, critical = 0;

        try (Connection con = DBConnection.connect();
             Statement stmt = con.createStatement()) {
            
            String sql = "SELECT blood_group, total_units FROM blood_stock_summary";
            if (!filter.isEmpty() && !filter.contains("Search")) {
                sql += " WHERE blood_group LIKE '%" + filter + "%'";
            }

            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String bg = rs.getString("blood_group");
                int units = rs.getInt("total_units");
                total += units;

                String status = (units > 15) ? "OPTIMAL" : (units > 5 ? "LOW" : "CRITICAL");
                if (status.equals("CRITICAL")) critical++;

                model.addRow(new Object[]{bg, units + " Units", status, getCompatibility(bg)});
            }
            totalUnitsVal.setText(String.valueOf(total));
            criticalGroupsVal.setText(String.valueOf(critical));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Connection Error: " + e.getMessage());
        }
    }

    private String getCompatibility(String group) {
        HashMap<String, String> map = new HashMap<>();
        map.put("O-", "Universal Donor");
        map.put("AB+", "Universal Recipient");
        map.put("O+", "O+, A+, B+, AB+");
        map.put("A+", "A+, AB+");
        map.put("B+", "B+, AB+");
        return map.getOrDefault(group, "Restricted Use");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StockBloodPage().setVisible(true));
    }
}