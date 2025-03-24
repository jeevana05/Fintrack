
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class HomePanel extends JPanel {

    public HomePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245)); // Light gray background

        JLabel label = new JLabel("Welcome to Personal Finance Tracker!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 22));
        label.setForeground(new Color(33, 150, 243)); // Blue text

        add(label, BorderLayout.CENTER);
    }
}
