
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;

import javax.swing.SwingWorker; // For SwingWorker
import java.io.BufferedReader;  // For BufferedReader
import java.io.InputStreamReader;  // For InputStreamReader
import java.nio.charset.StandardCharsets;  // For StandardCharsets

 
public class AddTransactionPanel extends JPanel {

    private JComboBox<String> typeDropdown, categoryDropdown;
    private JTextField amountField, dateField;
    private JLabel categoryLabel;
    private String userId; // User ID to be passed to the backend
    public AddTransactionPanel(String userId) {
        this.userId = userId;
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

        JLabel dateLabel = new JLabel("Transaction Date:");
        dateField = new JTextField(12);
        dateField.setEditable(false);
        JButton dateButton = new JButton("📅");
        dateButton.setFocusPainted(false);
        dateButton.addActionListener(e -> openDatePicker());

        categoryLabel = new JLabel("Category:");
        String[] categories = {"Food", "Hospital", "Fuel", "Rent", "Electricity", "Investments", "Others"};
        categoryDropdown = new JComboBox<>(categories);
        categoryDropdown.setBackground(Color.WHITE);

        JButton submitButton = new JButton("Add Transaction");
        submitButton.setBackground(new Color(33, 150, 243));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setFocusPainted(false);

        // Hide category initially
        categoryLabel.setVisible(false);
        categoryDropdown.setVisible(false);

        // Show/hide category based on transaction type
        typeDropdown.addActionListener(e -> {
            boolean isExpense = typeDropdown.getSelectedItem().equals("Expense");
            categoryLabel.setVisible(isExpense);
            categoryDropdown.setVisible(isExpense);
            revalidate();
            repaint();
        });

        // Submit button action
        submitButton.addActionListener(this::addTransaction);

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
        add(dateLabel, gbc);
        gbc.gridx = 1;
        add(dateField, gbc);
        gbc.gridx = 2;
        add(dateButton, gbc);

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

    // 📌 **Send Data to Spring Boot Backend using Map**
    public void addTransaction(ActionEvent e) {
        try {
            // Validate input
            double amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than zero!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String transactionType = (String) typeDropdown.getSelectedItem();
            String category = transactionType.equals("Expense") ? (String) categoryDropdown.getSelectedItem() : "N/A";
            String date = dateField.getText().trim();

            if (date.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a valid date!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create a Map for the JSON structure
            Map<String, String> transactionData = new HashMap<>();
            transactionData.put("userId", userId);
            transactionData.put("type", transactionType);
            transactionData.put("amount", String.valueOf(amount));
            transactionData.put("category", category);
            transactionData.put("date", date);

            // Convert Map to JSON string manually
            String json = String.format(
    "{\"userId\":\"%s\", \"type\":\"%s\", \"amount\":%s, \"category\":\"%s\", \"date\":\"%s\"}",
    transactionData.get("userId"),
    transactionData.get("type"),
    transactionData.get("amount"),
    transactionData.get("category"),
    transactionData.get("date")
);

            // Send HTTP POST request to backend
            URL url = new URI("http://localhost:8080/transactions/add").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                JOptionPane.showMessageDialog(this, "Transaction Added Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                amountField.setText("");
                categoryDropdown.setSelectedIndex(0);
                dateField.setText("");
                if (transactionType.equals("Expense")) {
                    String transactionDate = date.toString();
                updateRemainingBudget(category, amount,transactionDate);
            }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add transaction!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
         
    }
      private void updateRemainingBudget(String category, double amount, String transactionDate) {
    new SwingWorker<Void, Void>() {
        @Override
        protected Void doInBackground() {
            try {
                // Create the URL with the necessary parameters (without encoding the transaction date)
                URL url = new URL("http://localhost:8080/budgets/update?category=" + category + 
                  "&amount=" + amount + 
                  "&transactionDate=" + transactionDate + 
                  "&userId=" + userId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true); // Set to true if you need to send data in the body
                
                // Get the response code from the server
                int responseCode = conn.getResponseCode();

                // Check if the response code indicates a successful request
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the server response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                    String response = reader.readLine().trim(); // Assuming response is a single number (remaining budget)
                    reader.close();

                    // Parse the response as a double (remaining budget)
                    double remainingBudget = Double.parseDouble(response);

                    // If remaining budget is less than or equal to 0, show a warning message
                    if (remainingBudget <= 0) {
                        JOptionPane.showMessageDialog(null, "Budget exceeded for category: " + category, "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    // Handle non-OK response from the server
                    JOptionPane.showMessageDialog(null, "Failed to update budget for category: " + category, "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                // Handle any exceptions that occur during the process
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "An error occurred while updating the budget.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            return null;
        }
    }.execute();
}

    // 📌 **Custom Date Picker Dialog**
    private void openDatePicker() {
        JDialog dateDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Select Date", true);
        dateDialog.setLayout(new BorderLayout());

        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(editor);

        JButton selectButton = new JButton("OK");
        selectButton.addActionListener(e -> {
            Date selectedDate = (Date) dateSpinner.getValue();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dateField.setText(sdf.format(selectedDate));
            dateDialog.dispose();
        });

        JPanel panel = new JPanel();
        panel.add(dateSpinner);
        panel.add(selectButton);

        dateDialog.add(panel, BorderLayout.CENTER);
        dateDialog.pack();
        dateDialog.setLocationRelativeTo(this);
        dateDialog.setVisible(true);
    }
}
