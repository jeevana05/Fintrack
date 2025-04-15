import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class HomePanel extends JPanel {

    public HomePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245)); // Light gray background

        // Welcome Label
        JLabel label = new JLabel("Welcome to Personal Finance Tracker!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 22));
        label.setForeground(new Color(33, 150, 243)); // Blue text

        // Create a panel for center content
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centerPanel.setBackground(new Color(245, 245, 245)); // Match background

        centerPanel.add(label);
        centerPanel.add(new LoginPanel()); // Add your LoginPanel here

        add(centerPanel, BorderLayout.CENTER);
    }
}
