
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class FinanceTrackerHome extends JFrame {

    private JPanel contentPanel;

    public FinanceTrackerHome() {
        setTitle("Personal Finance Tracker");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Navigation Panel (Styled)
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new GridLayout(1, 5));
        navPanel.setBackground(new Color(33, 150, 243)); // Blue navbar

        String[] options = {"Home", "Add Transactions", "Set Budgets", "Predictions", "View Reports"};
        for (String option : options) {
            JButton button = new JButton(option);
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setBackground(Color.WHITE);
            button.setForeground(new Color(33, 150, 243));
            button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            button.setFocusPainted(false);
            button.addActionListener(new NavButtonListener());
            navPanel.add(button);
        }

        // Content Panel (Default View)
        contentPanel = new JPanel(new BorderLayout());
        showPanel(new HomePanel());

        add(navPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void showPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private class NavButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = ((JButton) e.getSource()).getText();
            switch (command) {
                case "Home":
                    showPanel(new HomePanel());
                    break;
                case "Add Transactions":
                    showPanel(new AddTransactionPanel());
                    break;
                case "Set Budgets":
                    showPanel(new SetBudgetsPanel());
                    break;
                case "Predictions":
                    showPanel(new PredictionsPanel());
                    break;
                case "View Reports":
                    showPanel(new ViewReportsPanel());
                    break;
            }
        }
    }

    public static void main(String[] args) {
        new FinanceTrackerHome();
    }
}
