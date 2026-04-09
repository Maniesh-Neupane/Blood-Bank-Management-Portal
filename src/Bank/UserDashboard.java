package Bank;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.File;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class UserDashboard extends JFrame {

    
    private final Color CLR_PRIMARY      = new Color(185, 28,  28);   
    private final Color CLR_PRIMARY_DARK = new Color(153, 20,  20);
    private final Color CLR_SUCCESS      = new Color(4,   120, 87);   
    private final Color CLR_WARNING      = new Color(161, 75,  8);    
    private final Color CLR_INFO         = new Color(67,  56,  202);  
    private final Color CLR_NAVY         = new Color(15,  23,  42);
    private final Color CLR_BG           = new Color(245, 246, 250);  
    private final Color CLR_BORDER       = new Color(226, 228, 233);  
    private final Color CLR_TEXT_MAIN    = new Color(15,  23,  42);   
    private final Color CLR_TEXT_SUB     = new Color(71,  85,  105);  
    private final Color CLR_TEXT_MUTED   = new Color(148, 163, 184);  
    private final Color CLR_CARD         = Color.WHITE;

    // SESSION STATE
    private String    currentUserName;
    private String    currentUserBloodGroup;
    private JLabel    lblTime;
    private JPanel    contentWrapper;
    private int       userPoints    = 0;
    private int       donationCount = 0;
    private int       userId        = 0;
    private String    userCity      = "";
    private LocalDate lastDonationDate = null;

    
    public UserDashboard(String userName, String bloodGroup) {
        this.currentUserName       = userName;
        this.currentUserBloodGroup = bloodGroup;
        fetchUserData();
        setTitle("LifeLine Portal | " + userName);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1280, 850));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(CLR_BG);
        buildUI();
        add(contentWrapper);
        startClock();
    }

    // DATA 
    private void fetchUserData() {
        try (Connection con = DBConnection.connect()) {
            PreparedStatement ps = con.prepareStatement(
                "SELECT id, points, donation_count, last_donation_date, city FROM users WHERE name = ?");
            ps.setString(1, currentUserName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                userId        = rs.getInt("id");
                userPoints    = rs.getInt("points");
                donationCount = rs.getInt("donation_count");
                Date d = rs.getDate("last_donation_date");
                if (d != null) lastDonationDate = d.toLocalDate();
                userCity = rs.getString("city");
                if (userCity == null) userCity = "Unknown";
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    
    public void refreshDashboard() {
        fetchUserData();
        buildUI();
        revalidate();
        repaint();
    }

    // BUILD UI 
    private void buildUI() {
        contentWrapper.removeAll();
        contentWrapper.add(buildHeader(), BorderLayout.NORTH);

        JPanel main = new JPanel(new BorderLayout(25, 0));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(25, 35, 25, 35));

        //Left column 
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.add(buildEligibilityBanner());
        left.add(Box.createVerticalStrut(20));
        left.add(buildStatsGrid());
        left.add(Box.createVerticalStrut(20));
        left.add(buildQuickActions());
        left.add(Box.createVerticalStrut(20));
        left.add(buildCompatibility());
        left.add(Box.createVerticalStrut(20));
        left.add(buildHistoryTable());

        // Right sidebar (wider: 400px for notifications)
        JPanel right = new JPanel();
        right.setPreferredSize(new Dimension(400, 0));
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);
        right.add(buildProgressCard());
        right.add(Box.createVerticalStrut(18));
        right.add(buildHealthTip());
        right.add(Box.createVerticalStrut(18));
        right.add(buildBloodGroupInfo());
        right.add(Box.createVerticalStrut(18));
        right.add(buildNotifications());

        main.add(left,  BorderLayout.CENTER);
        main.add(right, BorderLayout.EAST);

        JScrollPane scroll = new JScrollPane(main);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        contentWrapper.add(scroll, BorderLayout.CENTER);
        contentWrapper.revalidate();
        contentWrapper.repaint();
    }

    
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(Color.WHITE);
        h.setPreferredSize(new Dimension(0, 85));
        h.setBorder(new MatteBorder(0, 0, 1, 0, CLR_BORDER));

        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        left.setBorder(new EmptyBorder(18, 35, 18, 20));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel welcome = new JLabel("Welcome back, " + currentUserName + " \uD83D\uDC4B");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 24));
        welcome.setForeground(CLR_TEXT_MAIN);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Time label — Nepal Standard Time (NST = UTC+5:45)
        lblTime = new JLabel("Loading...");
        lblTime.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblTime.setForeground(CLR_TEXT_SUB);
        lblTime.setAlignmentX(Component.LEFT_ALIGNMENT);

        info.add(welcome);
        info.add(Box.createVerticalStrut(4));
        info.add(lblTime);
        left.add(info, BorderLayout.CENTER);

        JPanel acts = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 18));
        acts.setOpaque(false);
        acts.setBorder(new EmptyBorder(0, 0, 0, 35));

        JButton btnDonate = solidBtn("\uD83E\uDE78 Quick Donate", CLR_PRIMARY);
        btnDonate.addActionListener(e -> handleDonate());

        JButton btnLogout = solidBtn("Sign Out", CLR_NAVY);
        btnLogout.addActionListener(e -> { dispose(); new UserHome().setVisible(true); });

        acts.add(btnDonate);
        acts.add(btnLogout);
        h.add(left, BorderLayout.WEST);
        h.add(acts, BorderLayout.EAST);
        return h;
    }

    
    private JPanel buildStatsGrid() {
        JPanel g = new JPanel(new GridLayout(1, 4, 18, 0));
        g.setOpaque(false);
        g.setMaximumSize(new Dimension(9999, 140));

        String badge    = userPoints >= 1000 ? "\uD83D\uDC8E Diamond"
                        : userPoints >= 500  ? "\uD83E\uDD47 Gold"
                        : userPoints >= 100  ? "\uD83E\uDD48 Silver" : "\uD83E\uDE78 Donor";
        int livesSaved  = donationCount * 3;

        g.add(statCard("Blood Type",    currentUserBloodGroup, "Your blood group",     CLR_INFO,                 "\uD83E\uDE78"));
        g.add(statCard("Lives Saved",   livesSaved + "",       donationCount + " donations", new Color(4,120,87),"\u2764\uFE0F"));
        g.add(statCard("Impact Points", userPoints + "",       "Total earned",         new Color(161, 75, 8),    "\u2B50"));
        g.add(statCard("Rank",          badge,                 tierName(),             CLR_PRIMARY,              "\uD83C\uDFC6"));
        return g;
    }

    private String tierName() {
        if (userPoints >= 1000) return "Diamond Tier";
        if (userPoints >= 500)  return "Gold Tier";
        if (userPoints >= 100)  return "Silver Tier";
        return "Rising Hero";
    }

    private JPanel statCard(String title, String value, String sub, Color accent, String icon) {
        JPanel c = roundedPanel(CLR_CARD);
        c.setLayout(new BorderLayout(0, 8));
        c.setBorder(new CompoundBorder(new MatteBorder(0, 4, 0, 0, accent),
                                       new EmptyBorder(18, 18, 18, 18)));

        JPanel top = new JPanel(new BorderLayout()); top.setOpaque(false);
        JLabel tl = lbl(title.toUpperCase(), 10, Font.BOLD, CLR_TEXT_MUTED);
        JLabel il = lbl(icon, 22, Font.PLAIN, CLR_TEXT_MUTED);
        top.add(tl, BorderLayout.WEST); top.add(il, BorderLayout.EAST);

        JLabel vl = lbl(value, 30, Font.BOLD, accent);
        JLabel sl = lbl(sub,   11, Font.PLAIN, CLR_TEXT_MUTED);

        c.add(top, BorderLayout.NORTH);
        c.add(vl,  BorderLayout.CENTER);
        c.add(sl,  BorderLayout.SOUTH);
        return c;
    }

    
    private JPanel buildQuickActions() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel title = lbl("\u26A1 Quick Actions", 17, Font.BOLD, CLR_TEXT_MAIN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel grid = new JPanel(new GridLayout(1, 4, 15, 0));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(9999, 90));
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);

        grid.add(actionCard("\uD83D\uDCC5 Donate Blood", "Schedule donation",
            e -> handleDonate()));

        // Request Blood — opens BloodRequestPage with session; returns here on back/submit
        grid.add(actionCard("\uD83C\uDD98 Request Blood", "Emergency request",
            e -> openBloodRequest()));

        // Find Blood Banks — passes 'this' so Back button returns here, not login screen
        grid.add(actionCard("\uD83C\uDFE5 Find Blood Banks", "Locate nearby centers",
            e -> {
                try {
                    new BloodBankCentrePage(this).setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Cannot open blood bank locator:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }));

        // Certificate — real designed dialog with print/save
        grid.add(actionCard("\uD83D\uDCDC Certificate", "Download proof",
            e -> {
                if (donationCount > 0) showCertificate();
                else JOptionPane.showMessageDialog(this,
                    "You need at least one donation to receive a certificate.",
                    "No Donations Yet", JOptionPane.WARNING_MESSAGE);
            }));

        panel.add(title);
        panel.add(Box.createVerticalStrut(12));
        panel.add(grid);
        return panel;
    }

   
    // Open BloodRequestPage — passes 'this' so session is fully preserved
    
    private void openBloodRequest() {
        try {
            // Three-arg constructor: userName, bloodGroup, parent (this UserDashboard)
            // BloodRequestPage will return here on Back or after Submit
            BloodRequestPage page = new BloodRequestPage(
                currentUserName, currentUserBloodGroup, this);
            page.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            showInlineRequestDialog();
        }
    }

  
    private void showInlineRequestDialog() {
        JDialog dlg = new JDialog(this, "\uD83C\uDD98 Blood Request", true);
        dlg.setSize(520, 490);
        dlg.setLocationRelativeTo(this);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(248, 250, 252));
        p.setBorder(new EmptyBorder(28, 32, 28, 32));

        p.add(lbl("Submit Blood Request", 20, Font.BOLD, CLR_PRIMARY));
        p.add(Box.createVerticalStrut(4));
        p.add(lbl("Logged in as: " + currentUserName + " (" + currentUserBloodGroup + ")", 12, Font.PLAIN, CLR_TEXT_MUTED));
        p.add(Box.createVerticalStrut(20));

        JTextField tfPatient = inputField();
        JTextField tfPhone   = inputField();
        JTextField tfUnits   = inputField();
        String[] groups = {"A+","A-","B+","B-","O+","O-","AB+","AB-"};
        JComboBox<String> cbGroup = new JComboBox<>(groups);
        cbGroup.setSelectedItem(currentUserBloodGroup);
        cbGroup.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cbGroup.setBackground(Color.WHITE);
        cbGroup.setMaximumSize(new Dimension(9999, 40));
        cbGroup.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextArea taNote = new JTextArea(3, 1);
        taNote.setFont(new Font("SansSerif", Font.PLAIN, 13));
        taNote.setLineWrap(true); taNote.setWrapStyleWord(true);
        taNote.setBorder(new CompoundBorder(new LineBorder(CLR_BORDER, 1, true), new EmptyBorder(8, 10, 8, 10)));
        JScrollPane noteScr = new JScrollPane(taNote);
        noteScr.setMaximumSize(new Dimension(9999, 80));
        noteScr.setAlignmentX(Component.LEFT_ALIGNMENT);
        noteScr.setBorder(null);

        p.add(fieldLbl("Patient / Hospital *")); p.add(Box.createVerticalStrut(5)); p.add(tfPatient);
        p.add(Box.createVerticalStrut(12));
        p.add(fieldLbl("Phone *"));              p.add(Box.createVerticalStrut(5)); p.add(tfPhone);
        p.add(Box.createVerticalStrut(12));
        p.add(fieldLbl("Blood Group *"));        p.add(Box.createVerticalStrut(5)); p.add(cbGroup);
        p.add(Box.createVerticalStrut(12));
        p.add(fieldLbl("Units Needed *"));       p.add(Box.createVerticalStrut(5)); p.add(tfUnits);
        p.add(Box.createVerticalStrut(12));
        p.add(fieldLbl("Urgency Note"));         p.add(Box.createVerticalStrut(5)); p.add(noteScr);
        p.add(Box.createVerticalStrut(22));

        JButton submit = solidBtn("Submit Request", CLR_PRIMARY);
        submit.setAlignmentX(Component.LEFT_ALIGNMENT);
        submit.addActionListener(ev -> {
            String pat = tfPatient.getText().trim(), ph = tfPhone.getText().trim();
            String bg  = (String) cbGroup.getSelectedItem(), uStr = tfUnits.getText().trim();
            String nt  = taNote.getText().trim();
            if (pat.isEmpty() || ph.isEmpty() || uStr.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Please fill all required fields.", "Missing", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int units; try { units = Integer.parseInt(uStr); if (units <= 0) throw new NumberFormatException(); }
            catch (NumberFormatException ex) { JOptionPane.showMessageDialog(dlg, "Units must be a positive number.", "Invalid", JOptionPane.WARNING_MESSAGE); return; }
            try (Connection con = DBConnection.connect()) {
                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO blood_requests(patient_name,phone,blood_group,units_requested,note,status) VALUES(?,?,?,?,?,'Pending')");
                ps.setString(1, pat); ps.setString(2, ph); ps.setString(3, bg);
                ps.setInt(4, units); ps.setString(5, nt.isEmpty() ? null : nt);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(dlg, "\u2705 Request submitted! Our team will review it shortly.", "Submitted", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        p.add(submit);

        dlg.setContentPane(new JScrollPane(p));
        dlg.setVisible(true);
    }

   
    private void showCertificate() {
        JDialog dlg = new JDialog(this, "\uD83C\uDFC6 Certificate of Appreciation — " + currentUserName, true);
        dlg.setSize(730, 620);
        dlg.setLocationRelativeTo(this);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.setResizable(false);

        final int CW = 690, CH = 510;  

        String tier  = userPoints >= 1000 ? "\uD83D\uDC8E Diamond Donor"
                     : userPoints >= 500  ? "\uD83E\uDD47 Gold Donor"
                     : userPoints >= 100  ? "\uD83E\uDD48 Silver Donor" : "\uD83E\uDE78 Donor";
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
        int    lives = donationCount * 3;

        // ── Certificate canvas — fully custom-painted 
        JPanel canvas = new JPanel() {
            @Override
            public Dimension getPreferredSize() { return new Dimension(CW, CH); }

            @Override
            protected void paintComponent(Graphics g) {
                renderCert((Graphics2D) g, getWidth(), getHeight(), tier, today, lives);
            }
        };
        canvas.setOpaque(true);

        
        JButton btnSave = solidBtn("\uD83D\uDCBE  Download", new Color(4, 120, 87));
        btnSave.addActionListener(e -> doSavePng(canvas, CW, CH, tier, today, lives));

        JButton btnClose = new JButton("Close");
        btnClose.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btnClose.setBackground(new Color(226, 232, 240));
        btnClose.setForeground(CLR_TEXT_MAIN);
        btnClose.setFocusPainted(false);
        btnClose.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dlg.dispose());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 14));
        btnRow.setBackground(Color.WHITE);
        btnRow.setBorder(new MatteBorder(1, 0, 0, 0, CLR_BORDER));
    
        btnRow.add(btnSave);
        btnRow.add(btnClose);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(new Color(240, 240, 240));
        wrap.setBorder(new EmptyBorder(10, 10, 0, 10));
        wrap.add(canvas, BorderLayout.CENTER);

        dlg.setLayout(new BorderLayout());
        dlg.add(wrap,   BorderLayout.CENTER);
        dlg.add(btnRow, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    /** All certificate drawing in one place — called by both paintComponent and doSavePng */
    private void renderCert(Graphics2D g2, int W, int H, String tier, String today, int lives) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Cream parchment background
        g2.setColor(new Color(255, 253, 244));
        g2.fillRect(0, 0, W, H);

        // Outer thick crimson border
        g2.setColor(new Color(185, 28, 28));
        g2.setStroke(new BasicStroke(8f));
        g2.drawRect(12, 12, W - 24, H - 24);

        // Inner thin gold border
        g2.setColor(new Color(234, 179, 8));
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawRect(22, 22, W - 44, H - 44);

        // Corner gold ornaments
        int[][] corners = {{22,22},{W-22,22},{22,H-22},{W-22,H-22}};
        for (int[] c : corners) {
            int sx = c[0] == 22 ? 1 : -1, sy = c[1] == 22 ? 1 : -1;
            g2.setColor(new Color(234, 179, 8));
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(c[0], c[1] + sy*16, c[0], c[1] + sy*38);
            g2.drawLine(c[0] + sx*16, c[1], c[0] + sx*38, c[1]);
            g2.fillOval(c[0]-4, c[1]-4, 9, 9);
        }

        // Red header band
        g2.setColor(new Color(185, 28, 28));
        g2.fillRect(30, 30, W - 60, 82);

        // Header text
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Serif", Font.BOLD, 13));
        drawC(g2, "LIFELINE BLOOD BANK  \u2014  Butwal, NEPAL", W, 58);
        g2.setFont(new Font("Serif", Font.BOLD, 22));
        drawC(g2, "CERTIFICATE OF APPRECIATION", W, 98);

        // Gold divider
        g2.setColor(new Color(234, 179, 8));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(70, 126, W - 70, 126);

        // "certifies that"
        g2.setColor(new Color(71, 85, 105));
        g2.setFont(new Font("Serif", Font.ITALIC, 15));
        drawC(g2, "This is to proudly certify that", W, 158);

        // Donor name — large crimson
        g2.setColor(new Color(185, 28, 28));
        g2.setFont(new Font("Serif", Font.BOLD, 36));
        drawC(g2, currentUserName, W, 208);

        // Blood-group pill badge
        int bw = 160, bh = 34, bx = (W - bw) / 2, by = 220;
        g2.setColor(new Color(254, 242, 242));
        g2.fillRoundRect(bx, by, bw, bh, 17, 17);
        g2.setColor(new Color(185, 28, 28));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(bx, by, bw, bh, 17, 17);
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        drawC(g2, "Blood Type:  " + currentUserBloodGroup, W, by + 23);

        // Donation body text
        g2.setColor(new Color(30, 41, 59));
        g2.setFont(new Font("Serif", Font.PLAIN, 15));
        drawC(g2, "has completed " + donationCount + " life-saving donation"
            + (donationCount != 1 ? "s," : ","), W, 280);
        drawC(g2, "contributing to an estimated " + lives + " lives saved.", W, 302);

        // Tier badge pill
        int tw = 240, th = 44, tx = (W - tw)/2, ty = 322;
        g2.setColor(new Color(255, 249, 231));
        g2.fillRoundRect(tx, ty, tw, th, 22, 22);
        g2.setColor(new Color(234, 179, 8));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(tx, ty, tw, th, 22, 22);
        g2.setColor(new Color(120, 53, 15));
        g2.setFont(new Font("SansSerif", Font.BOLD, 17));
        drawC(g2, tier, W, ty + 29);

        // Points line
        g2.setColor(new Color(71, 85, 105));
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        drawC(g2, "Total Impact Points: " + userPoints, W, 386);

        // Signature line
        g2.setColor(new Color(185, 28, 28));
        g2.setStroke(new BasicStroke(1f));
        g2.drawLine(W/2 - 115, 438, W/2 + 115, 438);
        g2.setColor(new Color(100, 116, 139));
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        drawC(g2, "LifeLine Blood Bank Authority", W, 453);

        // Footer: issued date left, cert-ID right
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2.setColor(new Color(148, 163, 184));
        g2.drawString("Issued: " + today, 44, H - 32);
        String cid = "Cert #LBB-" + userId + "-" + donationCount;
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(cid, W - 44 - fm.stringWidth(cid), H - 32);
    }

    /** Centre-align a string horizontally */
    private void drawC(Graphics2D g2, String s, int W, int y) {
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(s, (W - fm.stringWidth(s)) / 2, y);
    }

    private void doPrint(JPanel canvas) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("LifeLine Certificate \u2014 " + currentUserName);
        job.setPrintable((g, pf, idx) -> {
            if (idx > 0) return Printable.NO_SUCH_PAGE;
            Graphics2D g2 = (Graphics2D) g;
            g2.translate(pf.getImageableX(), pf.getImageableY());
            double scale = Math.min(pf.getImageableWidth()  / canvas.getWidth(),
                                    pf.getImageableHeight() / canvas.getHeight());
            g2.scale(scale, scale);
            canvas.print(g2);
            return Printable.PAGE_EXISTS;
        });
        if (job.printDialog()) {
            try { job.print(); }
            catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Print error: " + ex.getMessage(), "Print", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Save to PNG — draws into a fresh BufferedImage of known size, avoids 0×0 problem */
    private void doSavePng(JPanel canvas, int CW, int CH, String tier, String today, int lives) {
        BufferedImage img = new BufferedImage(CW, CH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        renderCert(g2, CW, CH, tier, today, lives);
        g2.dispose();

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save Certificate as PNG");
        fc.setSelectedFile(new File("LifeLine_Certificate_" + currentUserName.replaceAll("\\s+","_") + ".png"));
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG Image","png"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File out = fc.getSelectedFile();
            if (!out.getName().toLowerCase().endsWith(".png")) out = new File(out + ".png");
            try {
                javax.imageio.ImageIO.write(img, "png", out);
                JOptionPane.showMessageDialog(this,
                    "\u2705 Certificate saved!\n" + out.getAbsolutePath(), "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    
    // NOTIFICATIONS
   
    
    private JPanel buildNotifications() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setOpaque(false);

        // Header
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setOpaque(false);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        titleRow.setOpaque(false);
        JLabel titleLbl = lbl("\uD83D\uDD14  Notifications", 17, Font.BOLD, CLR_TEXT_MAIN);
        titleRow.add(titleLbl);

        int unread = countUnread();
        if (unread > 0) {
            JLabel badge = new JLabel("  " + unread + " new  ");
            badge.setFont(new Font("SansSerif", Font.BOLD, 10));
            badge.setForeground(Color.WHITE);
            badge.setBackground(CLR_PRIMARY);
            badge.setOpaque(true);
            badge.setBorder(new EmptyBorder(3, 6, 3, 6));
            titleRow.add(badge);
        }

        JButton markAll = new JButton("Mark all read");
        markAll.setFont(new Font("SansSerif", Font.PLAIN, 11));
        markAll.setForeground(CLR_INFO);
        markAll.setContentAreaFilled(false);
        markAll.setBorderPainted(false);
        markAll.setFocusPainted(false);
        markAll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        markAll.addActionListener(e -> { markAllRead(); refreshDashboard(); });

        hdr.add(titleRow, BorderLayout.WEST);
        hdr.add(markAll,  BorderLayout.EAST);

        // Cards list 
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(CLR_CARD);
        list.setBorder(new EmptyBorder(10, 10, 10, 10));

        boolean any = false;
        try (Connection con = DBConnection.connect()) {
            PreparedStatement ps = con.prepareStatement(
                "SELECT message, created_at, is_read FROM notifications " +
                "WHERE user_id = ? ORDER BY created_at DESC LIMIT 12");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                any = true;
                list.add(notifCard(rs.getString("message"), rs.getTimestamp("created_at"), rs.getInt("is_read") == 0));
                list.add(Box.createVerticalStrut(10));
            }
        } catch (Exception e) { e.printStackTrace(); }

        if (!any) {
            JPanel empty = new JPanel();
            empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
            empty.setOpaque(false);
            empty.setBorder(new EmptyBorder(55, 20, 55, 20));
            JLabel ei = lbl("\uD83D\uDCED", 48, Font.PLAIN, CLR_TEXT_MUTED); ei.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel et = lbl("All caught up!",     15, Font.BOLD,  CLR_TEXT_MAIN); et.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel es = lbl("No new notifications",12, Font.PLAIN, CLR_TEXT_MUTED); es.setAlignmentX(Component.CENTER_ALIGNMENT);
            empty.add(ei); empty.add(Box.createVerticalStrut(12));
            empty.add(et); empty.add(Box.createVerticalStrut(4)); empty.add(es);
            list.add(empty);
        }

        // Taller scroll pane (600 px) — enough to show 4-5 cards without cramping
        JScrollPane sp = new JScrollPane(list);
        sp.setBorder(new LineBorder(CLR_BORDER, 1, true));
        sp.setPreferredSize(new Dimension(400, 600));
        sp.getVerticalScrollBar().setUnitIncrement(18);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        wrapper.add(hdr, BorderLayout.NORTH);
        wrapper.add(sp,  BorderLayout.CENTER);
        return wrapper;
    }

    /** Single notification card — larger font, clear colour-coding, proper text wrap */
    private JPanel notifCard(String message, Timestamp time, boolean isUnread) {
        boolean urgent  = message.contains("URGENT") || message.contains("CRITICAL") || message.contains("emergency");
        boolean warning = message.contains("LOW")    || message.contains("ALERT")    || message.contains("shortage");
        boolean success = message.contains("CERTIFICATE") || message.contains("award")
                       || message.contains("Recognition") || message.contains("Congratulations")
                       || message.contains("earned");

        Color bg  = urgent  ? new Color(254, 242, 242)
                  : warning ? new Color(255, 251, 235)
                  : success ? new Color(236, 253, 245)
                  :           new Color(248, 250, 252);
        Color bdr = urgent  ? new Color(252, 165, 165)
                  : warning ? new Color(252, 211,  77)
                  : success ? new Color(110, 231, 183)
                  :           CLR_BORDER;
        String icon    = urgent ? "\uD83D\uDEA8" : warning ? "\u26A0\uFE0F" : success ? "\uD83C\uDFC6" : "\uD83D\uDD14";
        String typeTag = urgent ? "URGENT ALERT"  : warning ? "STOCK ALERT"  : success ? "ACHIEVEMENT"  : "NOTIFICATION";
        Color  typClr  = urgent ? CLR_PRIMARY : warning ? new Color(146,64,14) : success ? new Color(4,120,87) : CLR_INFO;

        JPanel card = new JPanel(new BorderLayout(14, 0));
        card.setBackground(bg);
        card.setBorder(new CompoundBorder(
            new LineBorder(bdr, isUnread ? 2 : 1, true),
            new EmptyBorder(14, 14, 14, 14)));
        card.setMaximumSize(new Dimension(380, 9999));

        // Icon column
        JLabel iconLbl = lbl(icon, 24, Font.PLAIN, CLR_TEXT_MAIN);
        iconLbl.setVerticalAlignment(SwingConstants.TOP);
        iconLbl.setPreferredSize(new Dimension(34, 34));

        // Content column
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);

        // Type tag row + unread dot
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        top.setOpaque(false);
        top.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel typeLbl = lbl(typeTag, 9, Font.BOLD, typClr);
        top.add(typeLbl);
        if (isUnread) {
            JLabel dot = lbl("  \u25CF", 10, Font.BOLD, CLR_PRIMARY);
            top.add(dot);
        }

        JLabel sender = lbl("LifeLine System", 13, Font.BOLD, CLR_TEXT_MAIN);
        sender.setAlignmentX(Component.LEFT_ALIGNMENT);

        // JTextArea for wrapping — font 13, columns 25 keeps text in 380 px sidebar
        JTextArea msg = new JTextArea(message);
        msg.setFont(new Font("SansSerif", Font.PLAIN, 13));
        msg.setForeground(CLR_TEXT_SUB);
        msg.setLineWrap(true); msg.setWrapStyleWord(true);
        msg.setEditable(false); msg.setOpaque(false); msg.setBorder(null);
        msg.setAlignmentX(Component.LEFT_ALIGNMENT);
        msg.setColumns(25);

        JLabel ts = lbl(fmtTime(time), 11, Font.PLAIN, CLR_TEXT_MUTED);
        ts.setAlignmentX(Component.LEFT_ALIGNMENT);

        body.add(top);
        body.add(Box.createVerticalStrut(3));
        body.add(sender);
        body.add(Box.createVerticalStrut(6));
        body.add(msg);
        body.add(Box.createVerticalStrut(8));
        body.add(ts);

        card.add(iconLbl, BorderLayout.WEST);
        card.add(body,    BorderLayout.CENTER);
        return card;
    }

    private int countUnread() {
        try (Connection con = DBConnection.connect()) {
            PreparedStatement ps = con.prepareStatement(
                "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = 0");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    private void markAllRead() {
        try (Connection con = DBConnection.connect()) {
            PreparedStatement ps = con.prepareStatement(
                "UPDATE notifications SET is_read = 1 WHERE user_id = ?");
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private String fmtTime(Timestamp ts) {
        if (ts == null) return "Unknown";
        LocalDateTime ldt = ts.toLocalDateTime(), now = LocalDateTime.now();
        long hrs = ChronoUnit.HOURS.between(ldt, now);
        if (hrs < 1)  return "Just now";
        if (hrs < 24) return hrs + "h ago";
        long days = ChronoUnit.DAYS.between(ldt, now);
        if (days < 7) return days + "d ago";
        return ldt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }

    
    // DONATE HANDLER
    
    private void handleDonate() {
        if (isNotEligible()) {
            JOptionPane.showMessageDialog(this,
                "You must wait " + getDaysLeft() + " more day(s) before donating again.\n"
                + "Next donation available: " + nextDate(),
                "Not Eligible", JOptionPane.WARNING_MESSAGE);
        } else {
            new DonationForm(this, currentUserName, currentUserBloodGroup).setVisible(true);
        }
    }

    
    // ELIGIBILITY
    
    private boolean isNotEligible() {
        return lastDonationDate != null &&
               ChronoUnit.DAYS.between(lastDonationDate, LocalDate.now()) < 90;
    }
    private long getDaysLeft() {
        if (lastDonationDate == null) return 0;
        return Math.max(0, ChronoUnit.DAYS.between(LocalDate.now(), lastDonationDate.plusDays(90)));
    }
    private String nextDate() {
        if (lastDonationDate == null) return "now";
        return lastDonationDate.plusDays(90).format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }

    private JPanel buildEligibilityBanner() {
        boolean ok = !isNotEligible();
        JPanel b = roundedPanel(ok ? new Color(236,253,245) : new Color(254,242,242));
        b.setLayout(new BorderLayout());
        b.setBorder(new CompoundBorder(
            new LineBorder(ok ? new Color(110,231,183) : new Color(252,165,165), 2, true),
            new EmptyBorder(14, 18, 14, 18)));
        b.setMaximumSize(new Dimension(9999, 65));

        String txt = ok
            ? "\u2705  Medical Status: You're eligible to donate today — save lives!"
            : "\u23F3  Recovery Period: Next donation on " + nextDate() + "  (" + getDaysLeft() + " days remaining)";
        JLabel msg = lbl(txt, 13, Font.BOLD, ok ? new Color(4,120,87) : CLR_PRIMARY);
        b.add(msg);
        return b;
    }

    
    // PROGRESS CARD
    
    private JPanel buildProgressCard() {
        JPanel c = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(67,56,202), getWidth(), getHeight(), new Color(37,99,235)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }
        };
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBorder(new EmptyBorder(18, 18, 18, 18));
        c.setMaximumSize(new Dimension(400, 170));

        int next  = userPoints < 100 ? 100 : userPoints < 500 ? 500 : userPoints < 1000 ? 1000 : 2000;
        int pct   = Math.min(100, (int)((userPoints / (double) next) * 100));
        String nr = next == 100 ? "Silver" : next == 500 ? "Gold" : "Diamond";

        JLabel t = lbl("\uD83D\uDCC8 Your Progress", 15, Font.BOLD, Color.WHITE); t.setAlignmentX(0);
        JLabel v = lbl(userPoints + " / " + next + " points", 19, Font.BOLD, Color.WHITE); v.setAlignmentX(0);

        JProgressBar pb = new JProgressBar(0, 100);
        pb.setValue(pct); pb.setStringPainted(true); pb.setString(pct + "%");
        pb.setForeground(new Color(34,197,94)); pb.setBackground(new Color(255,255,255,80));
        pb.setMaximumSize(new Dimension(364, 28)); pb.setAlignmentX(0);
        pb.setFont(new Font("SansSerif", Font.BOLD, 11));

        JLabel nl = lbl((next - userPoints) + " points to " + nr + " rank", 11, Font.PLAIN,
            new Color(200,210,255)); nl.setAlignmentX(0);

        c.add(t); c.add(Box.createVerticalStrut(12));
        c.add(v); c.add(Box.createVerticalStrut(8));
        c.add(pb); c.add(Box.createVerticalStrut(8));
        c.add(nl);
        return c;
    }

    
    // COMPATIBILITY
    
    private JPanel buildCompatibility() {
        JPanel p = roundedPanel(CLR_CARD);
        p.setLayout(new BorderLayout(0, 12));
        p.setBorder(new CompoundBorder(new LineBorder(CLR_BORDER, 1, true), new EmptyBorder(18, 20, 18, 20)));
        p.setMaximumSize(new Dimension(9999, 120));

        JLabel t = lbl("\uD83D\uDD2C Donor Compatibility Matrix", 15, Font.BOLD, CLR_TEXT_MAIN);
        JLabel d = new JLabel("<html>As a <b>" + currentUserBloodGroup + "</b> donor, you can give to: "
            + "<font color='#B91C1C'>" + compat(currentUserBloodGroup) + "</font></html>");
        d.setFont(new Font("SansSerif", Font.PLAIN, 13));
        d.setForeground(CLR_TEXT_SUB);
        p.add(t, BorderLayout.NORTH); p.add(d, BorderLayout.CENTER);
        return p;
    }

    private String compat(String g) {
        if (g == null) return "Unknown";
        return switch (g.toUpperCase()) {
            case "O-"  -> "All Blood Types (Universal Donor)";
            case "O+"  -> "O+, A+, B+, AB+";
            case "A-"  -> "A\u2212, A+, AB\u2212, AB+";
            case "A+"  -> "A+, AB+";
            case "B-"  -> "B\u2212, B+, AB\u2212, AB+";
            case "B+"  -> "B+, AB+";
            case "AB-" -> "AB\u2212, AB+";
            case "AB+" -> "AB+ only";
            default    -> "Universal Recipients";
        };
    }

    
    // BLOOD GROUP INFO
   
    private JPanel buildBloodGroupInfo() {
        JPanel card = roundedPanel(CLR_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new CompoundBorder(new LineBorder(CLR_BORDER, 1, true), new EmptyBorder(18, 18, 18, 18)));
        card.setMaximumSize(new Dimension(400, 190));

        JLabel t = lbl("\uD83D\uDC89 Blood Group Info", 13, Font.BOLD, CLR_TEXT_MAIN); t.setAlignmentX(0);
        JLabel d = new JLabel("<html><div style='width:320px'>" + bgInfo(currentUserBloodGroup) + "</div></html>");
        d.setFont(new Font("SansSerif", Font.PLAIN, 12)); d.setForeground(CLR_TEXT_SUB); d.setAlignmentX(0);
        card.add(t); card.add(Box.createVerticalStrut(10)); card.add(d);
        return card;
    }

    private String bgInfo(String g) {
        if (g == null) return "No information available.";
        return switch (g.toUpperCase()) {
            case "O-"  -> "You are a <b>universal donor</b>! Your blood can save anyone in emergencies.";
            case "O+"  -> "Most common blood type. Very high demand for surgeries and emergencies.";
            case "A-"  -> "Rare type. Essential for A and AB patients during critical moments.";
            case "A+"  -> "Second most common. Vital for many medical procedures.";
            case "B-"  -> "Rare and valuable. Critical for B and AB patients.";
            case "B+"  -> "Relatively rare. Important for diverse patient needs.";
            case "AB-" -> "Rarest type! Precious for AB patients and universal plasma donor.";
            case "AB+" -> "Universal recipient — you can receive any blood type if needed.";
            default    -> "Blood group information not available.";
        };
    }

    
    // HEALTH TIP
   
    private JPanel buildHealthTip() {
        JPanel c = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0,0, CLR_NAVY, getWidth(), getHeight(), new Color(30,41,59)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }
        };
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBorder(new EmptyBorder(18, 18, 18, 18));
        c.setMaximumSize(new Dimension(400, 175));

        JLabel icon = lbl("\uD83D\uDCA1 HEALTH TIP", 10, Font.BOLD, new Color(148,163,184)); icon.setAlignmentX(0);
        JLabel tip = new JLabel("<html><body style='width:320px;color:white;font-size:12px;'>"
            + "Heroes like you need proper nutrition! Ensure your <b>iron intake</b> is adequate. "
            + "Include spinach, lentils and red meat to stay healthy and ready to donate.</body></html>");
        tip.setBorder(new EmptyBorder(10, 0, 0, 0)); tip.setAlignmentX(0);
        c.add(icon); c.add(tip);
        return c;
    }

    
    // DONATION HISTORY TABLE
    
    private JPanel buildHistoryTable() {
        JPanel p = new JPanel(new BorderLayout(0, 12)); p.setOpaque(false);
        JLabel t = lbl("\uD83D\uDCCB Donation History", 17, Font.BOLD, CLR_TEXT_MAIN);

        String[] cols = {"Date","Blood Group","Contact","Location"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        try (Connection con = DBConnection.connect()) {
            PreparedStatement ps = con.prepareStatement(
                "SELECT donation_date, blood_group, phone, city FROM donors WHERE name = ? ORDER BY donation_date DESC");
            ps.setString(1, currentUserName);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                model.addRow(new Object[]{rs.getTimestamp(1), rs.getString(2), rs.getString(3), rs.getString(4)});
        } catch (Exception e) { e.printStackTrace(); }

        JTable tbl = new JTable(model);
        tbl.setRowHeight(46); tbl.setShowGrid(false);
        tbl.setIntercellSpacing(new Dimension(0,0));
        tbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tbl.setSelectionBackground(new Color(238,242,255));
        tbl.setSelectionForeground(CLR_TEXT_MAIN);
        tbl.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable tb, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tb, v, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 14, 0, 14));
                if (!sel) { setBackground(row%2==0 ? Color.WHITE : new Color(248,250,252)); setForeground(CLR_TEXT_MAIN); }
                return this;
            }
        });
        JTableHeader hdr = tbl.getTableHeader();
        hdr.setPreferredSize(new Dimension(0, 42));
        hdr.setBackground(new Color(248,250,252));
        hdr.setFont(new Font("SansSerif", Font.BOLD, 12));
        hdr.setForeground(CLR_TEXT_MAIN);
        hdr.setReorderingAllowed(false);

        JScrollPane sc = new JScrollPane(tbl);
        sc.setBorder(new LineBorder(CLR_BORDER, 1, true));
        p.add(t, BorderLayout.NORTH); p.add(sc, BorderLayout.CENTER);
        return p;
    }

    
    // HELPERS
    
    private JLabel lbl(String text, int size, int style, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", style, size));
        l.setForeground(color);
        return l;
    }

    private JLabel fieldLbl(String text) {
        JLabel l = lbl(text, 11, Font.BOLD, CLR_TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField inputField() {
        JTextField f = new JTextField();
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setBackground(Color.WHITE); f.setForeground(CLR_TEXT_MAIN); f.setCaretColor(CLR_TEXT_MAIN);
        f.setBorder(new CompoundBorder(new LineBorder(CLR_BORDER,1,true), new EmptyBorder(8,10,8,10)));
        f.setMaximumSize(new Dimension(9999, 40)); f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }

    /** Solid coloured button that matches AdminDash solidBtn — custom-painted so Metal L&F can't override */
    private JButton solidBtn(String text, Color bg) {
        Color darker = bg.darker();
        JButton b = new JButton(text) {
            private Color cur = bg;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { cur = darker; repaint(); }
                public void mouseExited (MouseEvent e) { cur = bg;     repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cur);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(Color.WHITE); g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                              (getHeight()-fm.getHeight())/2 + fm.getAscent());
            }
        };
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setOpaque(false); b.setContentAreaFilled(false); b.setBorderPainted(false);
        b.setFocusPainted(false); b.setBorder(new EmptyBorder(10, 22, 10, 22));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JPanel actionCard(String title, String subtitle, ActionListener action) {
        JPanel card = roundedPanel(CLR_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new CompoundBorder(new LineBorder(CLR_BORDER,1,true), new EmptyBorder(14,14,14,14)));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel tl = lbl(title,    13, Font.BOLD,  CLR_TEXT_MAIN); tl.setAlignmentX(0);
        JLabel sl = lbl(subtitle, 11, Font.PLAIN, CLR_TEXT_MUTED); sl.setAlignmentX(0);
        card.add(tl); card.add(Box.createVerticalStrut(4)); card.add(sl);

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(238,242,255));
                card.setBorder(new CompoundBorder(new LineBorder(CLR_INFO,2,true), new EmptyBorder(13,13,13,13)));
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(CLR_CARD);
                card.setBorder(new CompoundBorder(new LineBorder(CLR_BORDER,1,true), new EmptyBorder(14,14,14,14)));
            }
            public void mouseClicked(MouseEvent e) {
                if (action != null) action.actionPerformed(new ActionEvent(card, ActionEvent.ACTION_PERFORMED, null));
            }
        });
        return card;
    }

    private JPanel roundedPanel(Color bg) {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }
            { setBackground(bg); setOpaque(false); }
        };
    }

    private void startClock() {
        // Nepal Standard Time = UTC+5:45  (Asia/Kathmandu)
        Timer t = new Timer(1000, e -> {
            ZonedDateTime nst = ZonedDateTime.now(ZoneId.of("Asia/Kathmandu"));
            // Format:  Monday, March 12, 2026  •  14:35:22 NST
            String datePart = nst.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"));
            String timePart = nst.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            if (lblTime != null)
                lblTime.setText("\uD83D\uDCC5  " + datePart + "   \u2022   \uD83D\uDD52 " + timePart + " NST");
        });
        t.setInitialDelay(0);
        t.start();
    }
}