import java.awt.*;
import java.awt.event.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

public class SetBudgetsPanel extends JPanel {
    private JTextField budgetField, dueDateField;
    private JComboBox<String> categoryDropdown;
    private JLabel remainingLabel;
    private JButton dateButton;
    
    private double totalBudget = 5000; // Default total budget
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
        String[] categories = {"Food", "Hospital", "Fuel", "Rent", "Electricity", "Investments", "Others"};
        categoryDropdown = new JComboBox<>(categories);
        categoryDropdown.setBackground(Color.WHITE);

        JLabel budgetLabel = new JLabel("Budget Amount:");
        budgetField = new JTextField(10);

        JLabel dueDateLabel = new JLabel("Due Date:");
        dueDateField = new JTextField(10);
        dueDateField.setEditable(false);
        
        dateButton = new JButton("📅");
        dateButton.setFocusPainted(false);
        dateButton.setEnabled(false); // Initially disabled
        
        JButton setBudgetButton = new JButton("Set Budget");
        setBudgetButton.setBackground(new Color(33, 150, 243));
        setBudgetButton.setForeground(Color.WHITE);
        setBudgetButton.setFont(new Font("Arial", Font.BOLD, 14));
        setBudgetButton.setFocusPainted(false);

        remainingLabel = new JLabel("Remaining Budget: Rs " + (totalBudget - allocatedBudget));
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
        add(categoryDropdown, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(budgetLabel, gbc);
        gbc.gridx = 1;
        add(budgetField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(dueDateLabel, gbc);
        gbc.gridx = 1;
        add(dueDateField, gbc);
        gbc.gridx = 2;
        add(dateButton, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        add(setBudgetButton, gbc);

        gbc.gridy++;
        add(remainingLabel, gbc);

        // Event Listeners
        setBudgetButton.addActionListener(this::setBudget);
        dateButton.addActionListener(e -> openDatePicker());

        // Enable date picker button only for specific categories
        categoryDropdown.addItemListener(e -> {
            String selectedCategory = (String) categoryDropdown.getSelectedItem();
            boolean enableDatePicker = selectedCategory.equals("Electricity") || selectedCategory.equals("Rent");
            dateButton.setEnabled(enableDatePicker);
        });
    }

    /**
     * Handles the budget submission to the backend.
     */
    private void setBudget(ActionEvent e) {
        try {
            double budgetAmount = Double.parseDouble(budgetField.getText().trim());
            if (budgetAmount <= 0) {
                JOptionPane.showMessageDialog(this, "Enter a valid budget amount!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (allocatedBudget + budgetAmount > totalBudget) {
                JOptionPane.showMessageDialog(this, "Budget exceeds total available amount!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String category = categoryDropdown.getSelectedItem().toString();
            String dueDate = dueDateField.getText().trim();

            if ((category.equals("Electricity") || category.equals("Rent")) && dueDate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a valid due date!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Send budget data to backend
            sendBudgetToBackend(category, budgetAmount, dueDate);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Sends budget data to backend as a JSON object.
     */
    private void sendBudgetToBackend(String category, double amount, String dueDate) {
        try {
            URL url = new URI("http://localhost:8080/budgets/add").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Create JSON payload
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("category", category);
            requestData.put("amount", amount);
            requestData.put("allocatedAmount", 0);
            requestData.put("dueDate", dueDate);

            String jsonPayload = mapToJson(requestData);

            // Send JSON request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Check response
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                allocatedBudget += amount;
                remainingLabel.setText("Remaining Budget: Rs " + (totalBudget - allocatedBudget));
                JOptionPane.showMessageDialog(this, "Budget set for " + category, "Success", JOptionPane.INFORMATION_MESSAGE);
                budgetField.setText("");
                dueDateField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update budget! Try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error connecting to server!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Converts a Map<String, Object> to a JSON string manually.
     */
    private String mapToJson(Map<String, Object> map) {
        StringBuilder json = new StringBuilder("{");

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            json.append("\"").append(entry.getKey()).append("\":");
            if (entry.getValue() instanceof String) {
                json.append("\"").append(entry.getValue()).append("\"");
            } else {
                json.append(entry.getValue());
            }
            json.append(",");
        }

        // Remove last comma and close JSON
        if (json.length() > 1) json.setLength(json.length() - 1);
        json.append("}");

        return json.toString();
    }

    /**
     * Opens a date picker dialog.
     */
    private void openDatePicker() {
        JDialog dateDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Select Due Date", true);
        dateDialog.setLayout(new BorderLayout());

        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(editor);

        JButton selectButton = new JButton("OK");
        selectButton.addActionListener(e -> {
            Date selectedDate = (Date) dateSpinner.getValue();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dueDateField.setText(sdf.format(selectedDate));
            dateDialog.dispose();
        });

        JPanel panel = new JPanel();
        panel.add(dateSpinner);
        panel.add(selectButton);

        dateDialog.add(panel, BorderLayout.CENTER);
        dateDialog.pack();
        dateDialog.setLocationRelativeTo(this);
        dateDialog.setAlwaysOnTop(true);
        dateDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dateDialog.setVisible(true);
    }
}
