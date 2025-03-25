
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PredictionsPanel extends JPanel {

    public PredictionsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        JLabel label = new JLabel("Predictions Based on Spending Habits", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(new Color(33, 150, 243));

        add(label, BorderLayout.CENTER);
    }
}
