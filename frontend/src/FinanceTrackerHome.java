/* 
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

    private final JPanel contentPanel;
    private static String userId ;
    public FinanceTrackerHome(String userId) {
        this.userId = userId;
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
                case "Home" ->
                    showPanel(new HomePanel());
                case "Add Transactions" ->
    
                    showPanel(new AddTransactionPanel(userId));
                case "Set Budgets" ->
                    showPanel(new SetBudgetsPanel(userId));
                case "Predictions" ->
                    showPanel(new PredictionsPanel(userId));
                case "View Reports" ->
                    showPanel(new ViewReportsPanel(userId));
            }
        }
    }

    public static void main(String[] args) {
        new FinanceTrackerHome(userId);
    }
}
 */
 
 
 import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class FinanceTrackerHome extends JFrame {

    private final JPanel contentPanel;
    private static String userId;

    public FinanceTrackerHome(String userId) {
        this.userId = userId;
        setTitle("Personal Finance Tracker");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Navigation Panel (Styled)
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new GridLayout(1, 6)); // Increased to fit Logout button
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

        // Logout Button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setBackground(new Color(244, 67, 54)); // Red color
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
            );
            if (choice == JOptionPane.YES_OPTION) {
                dispose(); // Close current window
				//new LoginPanel();
                //System.exit(0); // Or redirect to login screen if available
            }
        });

        navPanel.add(logoutButton);

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
                case "Home" ->
                    showPanel(new HomePanel());
                case "Add Transactions" ->
                    showPanel(new AddTransactionPanel(userId));
                case "Set Budgets" ->
                    showPanel(new SetBudgetsPanel(userId));
                case "Predictions" ->
                    showPanel(new PredictionsPanel(userId));
                case "View Reports" ->
                    showPanel(new ViewReportsPanel(userId));
            }
        }
    }

    public static void main(String[] args) {
        new FinanceTrackerHome(userId);
    }
}
