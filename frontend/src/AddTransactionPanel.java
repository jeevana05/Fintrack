
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AddTransactionPanel extends JPanel {

    private JComboBox<String> typeDropdown, categoryDropdown;
    private JTextField amountField;
    private JLabel categoryLabel;

    public AddTransactionPanel() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("Add Transaction");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(33, 150, 243));

        JLabel typeLabel = new JLabel("Transaction Type:");
        String[] types = {"Income", "Expense"};
        typeDropdown = new JComboBox<>(types);
        typeDropdown.setBackground(Color.WHITE);

        JLabel amountLabel = new JLabel("Amount:");
        amountField = new JTextField(15);

        categoryLabel = new JLabel("Category:");
        String[] categories = {"Food", "Hospital", "Fuel", "Rent", "Electricity", "Investments", "Others"};
        categoryDropdown = new JComboBox<>(categories);
        categoryDropdown.setBackground(Color.WHITE);

        JButton submitButton = new JButton("Add Transaction");
        submitButton.setBackground(new Color(33, 150, 243));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setFocusPainted(false);

        // Hide category initially (default: "Income" selected)
        categoryLabel.setVisible(false);
        categoryDropdown.setVisible(false);

        // Change listener for type dropdown
        typeDropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (typeDropdown.getSelectedItem().equals("Expense")) {
                    categoryLabel.setVisible(true);
                    categoryDropdown.setVisible(true);
                } else {
                    categoryLabel.setVisible(false);
                    categoryDropdown.setVisible(false);
                }
                revalidate();
                repaint();
            }
        });

        // Submit button action
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double amount = Double.parseDouble(amountField.getText().trim());
                    if (amount <= 0) {
                        JOptionPane.showMessageDialog(null, "Amount must be greater than zero!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String transactionType = (String) typeDropdown.getSelectedItem();
                    String category = transactionType.equals("Expense") ? (String) categoryDropdown.getSelectedItem() : "N/A";

                    JOptionPane.showMessageDialog(null,
                            "Transaction Added Successfully!\n\nType: " + transactionType
                            + "\nAmount: $" + amount
                            + "\nCategory: " + category,
                            "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Reset fields
                    amountField.setText("");
                    categoryDropdown.setSelectedIndex(0);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid amount!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Layout setup
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(title, gbc);

        gbc.gridy++;
        add(typeLabel, gbc);
        gbc.gridx = 1;
        add(typeDropdown, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(amountLabel, gbc);
        gbc.gridx = 1;
        add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(categoryLabel, gbc);
        gbc.gridx = 1;
        add(categoryDropdown, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        add(submitButton, gbc);
    }
}
