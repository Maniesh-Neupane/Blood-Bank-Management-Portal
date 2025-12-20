package Bank;

import javax.swing.*;
import java.awt.*;

public class FindBankPage extends JFrame {
    public FindBankPage() {
        setTitle("Find Nearby Blood Banks");
        setSize(600, 400);
        setLocationRelativeTo(null);
        
        String[] columns = {"Bank Name", "Location", "Contact"};
        Object[][] data = {
            {"City Red Cross", "Downtown", "011-2233"},
            {"Metro Blood Care", "North Wing", "011-4455"},
            {"LifeLine Central", "Main St.", "011-9988"}
        };

        JTable table = new JTable(data, columns);
        add(new JScrollPane(table));
    }
}