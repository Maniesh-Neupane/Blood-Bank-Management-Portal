package Bank;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;

public class DonationForm extends JDialog {
    private JTextField txtName, txtPhone, txtCity;
    private JComboBox<String> comboBlood;
    private String currentUserName;
    private UserDashboard parentDash;

    public DonationForm(UserDashboard parent, String userName, String bloodGroup) {
        super(parent, "Blood Donation Registration", true);
        this.currentUserName = userName;
        this.parentDash = parent;
        
        setSize(450, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Header - Modern Red
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(new Color(220, 38, 38));
        header.setPreferredSize(new Dimension(0, 80));
        JLabel title = new JLabel("Register Donation");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        header.add(title);

        // Form Fields
        JPanel form = new JPanel(new GridLayout(8, 1, 5, 5));
        form.setBorder(new EmptyBorder(20, 45, 20, 45));
        form.setBackground(Color.WHITE);

        txtName = styledTextField(userName, false);
        txtPhone = styledTextField("", true);
        txtCity = styledTextField("", true);
        
        String[] groups = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
        comboBlood = new JComboBox<>(groups);
        comboBlood.setSelectedItem(bloodGroup);
        comboBlood.setBackground(Color.WHITE);

        form.add(new JLabel("Full Name (Verified)"));
        form.add(txtName);
        form.add(new JLabel("Your Blood Group"));
        form.add(comboBlood);
        form.add(new JLabel("Contact Phone"));
        form.add(txtPhone);
        form.add(new JLabel("Current City"));
        form.add(txtCity);

        // Action Button
        JButton btnSubmit = new JButton("Confirm & Earn +10 Points");
        btnSubmit.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnSubmit.setBackground(new Color(22, 163, 74));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setPreferredSize(new Dimension(0, 60));
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSubmit.setBorderPainted(false);
        btnSubmit.setOpaque(true);
        
        btnSubmit.addActionListener(e -> handleSubmission());

        add(header, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(btnSubmit, BorderLayout.SOUTH);
    }

    private JTextField styledTextField(String text, boolean editable) {
        JTextField tf = new JTextField(text);
        tf.setEditable(editable);
        tf.setBorder(new CompoundBorder(new LineBorder(new Color(200, 200, 200)), new EmptyBorder(5, 10, 5, 10)));
        if (!editable) tf.setBackground(new Color(245, 245, 245));
        return tf;
    }

    private void handleSubmission() {
        String phone = txtPhone.getText().trim();
        String city = txtCity.getText().trim();

        if(phone.isEmpty() || city.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide phone and city details.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.connect()) {
            con.setAutoCommit(false); 

            // 1. Insert into 'donors' log
            String donorSql = "INSERT INTO donors (name, blood_group, phone, city) VALUES (?, ?, ?, ?)";
            PreparedStatement ps1 = con.prepareStatement(donorSql);
            ps1.setString(1, currentUserName);
            ps1.setString(2, comboBlood.getSelectedItem().toString());
            ps1.setString(3, phone);
            ps1.setString(4, city);
            ps1.executeUpdate();

            // 2. Update the user's points and stats
            String userSql = "UPDATE users SET points = points + 10, donation_count = donation_count + 1, last_donation_date = CURRENT_DATE WHERE name = ?";
            PreparedStatement ps2 = con.prepareStatement(userSql);
            ps2.setString(1, currentUserName);
            ps2.executeUpdate();

            con.commit();
            JOptionPane.showMessageDialog(this, "Thank you! Points updated successfully.");
            
            // 3. UI Sync - Call the parent dashboard to refresh
            if (parentDash != null) {
                parentDash.refreshDashboard(); 
            }
            
            dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Submission Error: " + e.getMessage());
        }
    }
}