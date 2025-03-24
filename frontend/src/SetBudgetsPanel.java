
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SetBudgetsPanel extends JPanel {

    private JTextField categoryField, budgetField;
    private JLabel remainingLabel;
    private double totalBudget = 5000; // Default total budget (Change as needed)
    private double allocatedBudget = 0;

    public SetBudgetsPanel() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel title = new JLabel("Set Your Monthly Budget");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(33, 150, 243));

        JLabel categoryLabel = new JLabel("Category:");
        categoryField = new JTextField(15);

        JLabel budgetLabel = new JLabel("Budget Amount:");
        budgetField = new JTextField(10);

        JButton setBudgetButton = new JButton("Set Budget");
        setBudgetButton.setBackground(new Color(33, 150, 243));
        setBudgetButton.setForeground(Color.WHITE);
        setBudgetButton.setFont(new Font("Arial", Font.BOLD, 14));
        setBudgetButton.setFocusPainted(false);

        remainingLabel = new JLabel("Remaining Budget: $" + (totalBudget - allocatedBudget));
        remainingLabel.setFont(new Font("Arial", Font.BOLD, 14));
        remainingLabel.setForeground(Color.RED);

        // Add components to panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        add(categoryLabel, gbc);
        gbc.gridx = 1;
        add(categoryField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(budgetLabel, gbc);
        gbc.gridx = 1;
        add(budgetField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        add(setBudgetButton, gbc);

        gbc.gridy++;
        add(remainingLabel, gbc);

        // Button action listener
        setBudgetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double budgetAmount = Double.parseDouble(budgetField.getText());
                    if (budgetAmount <= 0) {
                        JOptionPane.showMessageDialog(null, "Enter a valid budget amount!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (allocatedBudget + budgetAmount > totalBudget) {
                        JOptionPane.showMessageDialog(null, "Budget exceeds total available amount!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    allocatedBudget += budgetAmount;
                    remainingLabel.setText("Remaining Budget: Rs" + (totalBudget - allocatedBudget));
                    JOptionPane.showMessageDialog(null, "Budget set for " + categoryField.getText(), "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Clear input fields
                    categoryField.setText("");
                    budgetField.setText("");

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
