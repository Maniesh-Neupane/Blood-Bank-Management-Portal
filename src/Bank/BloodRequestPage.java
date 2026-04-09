package Bank;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import java.util.regex.Pattern;

public class BloodRequestPage extends JFrame {

    private static final Color PRIMARY_RED = new Color(220, 53, 69);
    private static final Color FORM_BG     = new Color(253, 241, 227);
    private static final Color TEXT_BROWN  = new Color(92, 64, 51);
    private static final Color BORDER_GRAY = new Color(210, 200, 190);

    private JTextField    nameF, phoneF, fileF;
    private JTextArea     noteArea;
    private JComboBox<String> bloodGrp;
    private File          selectedFile;

    // SESSION: set when opened from UserDashboard 
    // null = guest / standalone usage
    private final String loggedInUser;
    private final String loggedInBloodGroup;
    private final JFrame parentDash;   

    
    public BloodRequestPage() {
        this(null, null, null);
    }

    // Constructor 2: called from UserDashboard — session preserved 
    public BloodRequestPage(String userName, String bloodGroup, JFrame parent) {
        this.loggedInUser       = userName;
        this.loggedInBloodGroup = bloodGroup;
        this.parentDash         = parent;
        buildUI();
    }

    private void buildUI() {
        setTitle("Blood Request Portal | LifeLine");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1100, 850));
        // CRITICAL: DISPOSE_ON_CLOSE — never EXIT_ON_CLOSE
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.add(createHeaderWithNavigation(), BorderLayout.NORTH);

        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(20, 20, 60, 20));
        body.add(createModernForm());

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        root.add(scroll, BorderLayout.CENTER);
        add(root);
    }

    
    private JPanel createHeaderWithNavigation() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_RED);
        header.setPreferredSize(new Dimension(0, 240));

        // ── Nav bar
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(20, 0, 0, 60));

        // Session badge (only when logged in)
        if (loggedInUser != null) {
            JLabel badge = new JLabel("  \uD83D\uDC64  " + loggedInUser
                + "  |  \uD83E\uDE78 " + loggedInBloodGroup + "  ");
            badge.setFont(new Font("SansSerif", Font.BOLD, 11));
            badge.setForeground(Color.WHITE);
            badge.setBackground(new Color(255, 255, 255, 45));
            badge.setOpaque(true);
            badge.setBorder(new EmptyBorder(8, 12, 8, 12));
            navPanel.add(badge);
        }

        // Back button label changes depending on context
        String backLabel = (parentDash != null) ? "\u2190  Back to Dashboard" : "\u2190  Back to Home";
        JButton backBtn = new JButton(backLabel);
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(new Color(44, 62, 80));
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        backBtn.setPreferredSize(new Dimension(190, 40));
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> goBack());
        navPanel.add(backBtn);

        //  Title panel 
        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel title = new JLabel("Blood Request Form");
        title.setFont(new Font("SansSerif", Font.BOLD, 42));
        title.setForeground(Color.WHITE);

        JLabel subTitle = new JLabel("\u0930\u0917\u0924 \u091A\u093E\u0939\u093F\u092F\u094B\u0964  (Emergency Medical Assistance)");
        subTitle.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subTitle.setForeground(new Color(255, 255, 255, 180));

        gbc.gridx = 0; gbc.gridy = 0; titlePanel.add(title, gbc);
        gbc.gridy = 1; gbc.insets = new Insets(6, 0, 0, 0); titlePanel.add(subTitle, gbc);

        header.add(navPanel,    BorderLayout.NORTH);
        header.add(titlePanel,  BorderLayout.CENTER);
        return header;
    }

    
    private JPanel createModernForm() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(FORM_BG);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER_GRAY, 1),
            new EmptyBorder(40, 50, 40, 50)
        ));
        card.setPreferredSize(new Dimension(880, 590));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(10, 15, 10, 15);
        g.weightx = 0.5;

        // Row 0: labels
        g.gridx = 0; g.gridy = 0; card.add(createLabel("\uD83D\uDC64  PATIENT NAME *"), g);
        g.gridx = 1;              card.add(createLabel("\uD83D\uDCDE  PHONE NUMBER *"), g);

        // Row 1: fields
        nameF  = styledField();
        phoneF = styledField();
        g.gridx = 0; g.gridy = 1; card.add(nameF,  g);
        g.gridx = 1;              card.add(phoneF, g);

        // Row 2: labels
        g.gridx = 0; g.gridy = 2; card.add(createLabel("\uD83E\uDE78  BLOOD GROUP *"), g);
        g.gridx = 1;              card.add(createLabel("\uD83D\uDCC4  REQUISITION FILE"), g);

        // Row 3: blood group + file picker
        bloodGrp = new JComboBox<>(new String[]{
            "-- Select Group --","A+","A-","B+","B-","O+","O-","AB+","AB-"});
        bloodGrp.setPreferredSize(new Dimension(0, 45));
        bloodGrp.setBackground(Color.WHITE);
        bloodGrp.setFont(new Font("SansSerif", Font.PLAIN, 14));
        // Pre-select logged-in user's blood group if known
        if (loggedInBloodGroup != null) bloodGrp.setSelectedItem(loggedInBloodGroup);

        fileF = styledField();
        fileF.setEditable(false);
        fileF.setText("No file selected");

        JButton browse = new JButton("Browse");
        browse.setFont(new Font("SansSerif", Font.BOLD, 12));
        browse.setBackground(new Color(44, 62, 80));
        browse.setForeground(Color.WHITE);
        browse.setFocusPainted(false);
        browse.setBorderPainted(false);
        browse.setCursor(new Cursor(Cursor.HAND_CURSOR));
        browse.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser();
            if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedFile = jfc.getSelectedFile();
                fileF.setText(selectedFile.getName());
            }
        });

        JPanel fileBox = new JPanel(new BorderLayout(8, 0));
        fileBox.setOpaque(false);
        fileBox.add(fileF, BorderLayout.CENTER);
        fileBox.add(browse, BorderLayout.EAST);

        g.gridx = 0; g.gridy = 3; card.add(bloodGrp, g);
        g.gridx = 1;              card.add(fileBox,  g);

        // Row 4–5: note
        g.gridx = 0; g.gridy = 4; g.gridwidth = 2;
        card.add(createLabel("\uD83D\uDCAC  NOTE / URGENCY DETAILS"), g);
        noteArea = new JTextArea(4, 20);
        noteArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        noteArea.setBorder(new CompoundBorder(
            new LineBorder(BORDER_GRAY),
            new EmptyBorder(8, 10, 8, 10)
        ));
        g.gridy = 5; card.add(new JScrollPane(noteArea), g);

        // Guest notice
        if (loggedInUser == null) {
            JLabel notice = new JLabel(
                "  \u2139  Submitting as guest.  ");
            notice.setFont(new Font("SansSerif", Font.ITALIC, 11));
            notice.setForeground(new Color(67, 56, 202));
            notice.setBackground(new Color(238, 242, 255));
            notice.setOpaque(true);
            notice.setBorder(new EmptyBorder(6, 10, 6, 10));
            g.gridy = 6; g.insets = new Insets(8, 15, 0, 15);
            card.add(notice, g);
        }

        // Submit button
        JButton submit = new JButton("SUBMIT URGENT REQUEST");
        styleSubmitButton(submit);
        submit.addActionListener(e -> handleSubmission());

        g.gridy = (loggedInUser == null) ? 7 : 6;
        g.insets = new Insets(24, 15, 0, 15);
        card.add(submit, g);

        g.gridwidth = 1;
        return card;
    }

    
    // SUBMISSION
   
    private void handleSubmission() {
        String name  = nameF.getText().trim();
        String phone = phoneF.getText().trim();
        String group = (String) bloodGrp.getSelectedItem();
        String note  = noteArea.getText().trim();

        // Validate
        if (name.isEmpty()) {
            warn("Please enter the patient / hospital name."); return;
        }
        if (!Pattern.matches("^[0-9]{7,15}$", phone)) {
            warn("Please enter a valid phone number (7–15 digits)."); return;
        }
        if (bloodGrp.getSelectedIndex() == 0) {
            warn("Please select a blood group."); return;
        }
        // File is optional for guests, required if submitted via admin-enforced flow
       

        try (Connection con = DBConnection.connect()) {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO blood_requests " +
                "(patient_name, phone, blood_group, units_requested, note, " +
                " requisition_file, status, request_date) " +
                "VALUES (?, ?, ?, 1, ?, ?, 'Pending', NOW())");
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, group);
            ps.setString(4, note.isEmpty() ? null : note);
            ps.setString(5, selectedFile != null ? selectedFile.getAbsolutePath() : null);

            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this,
                    "\u2705  Request submitted successfully!\nOur team will review it shortly.",
                    "Request Submitted", JOptionPane.INFORMATION_MESSAGE);

                
                goBack();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

   
    private void goBack() {
        dispose();
        if (parentDash != null) {
            
            parentDash.setVisible(true);
            parentDash.toFront();
            
            if (parentDash instanceof UserDashboard) {
                ((UserDashboard) parentDash).refreshDashboard();
            }
        } else {
            
            new UserHome().setVisible(true);
        }
    }

    
    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }

    private JLabel createLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(TEXT_BROWN);
        return l;
    }

    private JTextField styledField() {
        JTextField f = new JTextField();
        f.setFont(new Font("SansSerif", Font.PLAIN, 14));
        f.setPreferredSize(new Dimension(0, 45));
        f.setBorder(new CompoundBorder(
            new LineBorder(BORDER_GRAY),
            new EmptyBorder(0, 10, 0, 10)
        ));
        return f;
    }

    private void styleSubmitButton(JButton b) {
        b.setBackground(PRIMARY_RED);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 15));
        b.setPreferredSize(new Dimension(0, 55));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BloodRequestPage().setVisible(true));
    }
}