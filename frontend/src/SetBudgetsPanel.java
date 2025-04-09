import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

public class SetBudgetsPanel extends JPanel {
    private JTextField budgetField, dueDateField;
    private JComboBox<String> categoryDropdown, monthDropdown;
    private JLabel remainingLabel;
    private JButton dateButton;

    private double monthlyIncome = 0.0;
    private double allocatedBudget = 0;
    private double totalBudget = 0; 

    private String userId; 

    public SetBudgetsPanel(String userId) {
        this.userId = userId;
        System.out.println("SetBudgetsPanel - userId: " + userId);

        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        JLabel title = new JLabel("Set Your Monthly Budget");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(33, 150, 243));

        // Month Selection
        JLabel monthLabel = new JLabel("Select Month:");
        String[] months = {"January", "February", "March", "April", "May", "June", 
                           "July", "August", "September", "October", "November", "December"};
        monthDropdown = new JComboBox<>(months);
        monthDropdown.setBackground(Color.WHITE);

        // Category Selection
        JLabel categoryLabel = new JLabel("Category:");
        String[] categories = {"Food", "Hospital", "Fuel", "Rent", "Electricity", "Investments", "Others"};
        categoryDropdown = new JComboBox<>(categories);
        categoryDropdown.setBackground(Color.WHITE);

        // Budget Amount
        JLabel budgetLabel = new JLabel("Budget Amount:");
        budgetField = new JTextField(10);

        // Due Date
        JLabel dueDateLabel = new JLabel("Due Date:");
        dueDateField = new JTextField(10);
        dueDateField.setEditable(false);
        
        dateButton = new JButton("📅");
        dateButton.setFocusPainted(false);
        dateButton.setEnabled(false);

        // Set Budget Button
        JButton setBudgetButton = new JButton("Set Budget");
        setBudgetButton.setBackground(new Color(33, 150, 243));
        setBudgetButton.setForeground(Color.WHITE);
        setBudgetButton.setFont(new Font("Arial", Font.BOLD, 14));
        setBudgetButton.setFocusPainted(false);

        // Remaining Budget Label
        remainingLabel = new JLabel("Remaining Budget: Rs 0");
        remainingLabel.setFont(new Font("Arial", Font.BOLD, 14));
        remainingLabel.setForeground(Color.RED);

        // Add components to panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(title, gbc);

        // Month Selection Row
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        add(monthLabel, gbc);
        gbc.gridx = 1;
        add(monthDropdown, gbc);

        // Category Row
        gbc.gridx = 0;
        gbc.gridy++;
        add(categoryLabel, gbc);
        gbc.gridx = 1;
        add(categoryDropdown, gbc);

        // Budget Amount Row
        gbc.gridx = 0;
        gbc.gridy++;
        add(budgetLabel, gbc);
        gbc.gridx = 1;
        add(budgetField, gbc);

        // Due Date Row
        gbc.gridx = 0;
        gbc.gridy++;
        add(dueDateLabel, gbc);
        gbc.gridx = 1;
        add(dueDateField, gbc);
        gbc.gridx = 2;
        add(dateButton, gbc);

        // Set Budget Button Row
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        add(setBudgetButton, gbc);

        // Remaining Budget Row
        gbc.gridy++;
        add(remainingLabel, gbc);

        // Event Listeners
        setBudgetButton.addActionListener(this::setBudget);
        dateButton.addActionListener(e -> openDatePicker());

        // Month Selection Listener
        monthDropdown.addActionListener(e -> {
            String selectedMonth = (String) monthDropdown.getSelectedItem();
            String monthCode = convertMonthToCode(selectedMonth);
            fetchMonthlyIncome(userId,monthCode);
        });

        // Enable date picker button only for specific categories
        categoryDropdown.addItemListener(e -> {
            String selectedCategory = (String) categoryDropdown.getSelectedItem();
            boolean enableDatePicker = selectedCategory.equals("Electricity") || selectedCategory.equals("Rent");
            dateButton.setEnabled(enableDatePicker);
        });

        // Default to current month
        monthDropdown.setSelectedItem(getCurrentMonthName());
        fetchMonthlyIncome(userId,getCurrentMonthCode());
    }

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
            String selectedMonth = (String) monthDropdown.getSelectedItem();

            if ((category.equals("Electricity") || category.equals("Rent")) && dueDate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a valid due date!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            sendBudgetToBackend(userId,category, budgetAmount, dueDate, selectedMonth);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendBudgetToBackend(String userId,String category, double amount, String dueDate, String selectedMonth) {
    try {
        URL url = new URI("http://localhost:8080/budgets/add").toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Extract month code and current year
        String monthCode = convertMonthToCode(selectedMonth);
        int currentYear = LocalDate.now().getYear(); // Get the current year

        // Prepare JSON payload
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("userId", userId);
        requestData.put("category", category);
        requestData.put("amount", amount);
        requestData.put("remainingAmount", amount);
        requestData.put("dueDate", dueDate);
        requestData.put("month", monthCode);  
        requestData.put("year", currentYear); 

        String jsonPayload = mapToJson(requestData);
        System.out.println(jsonPayload); // Debugging output


        // Send JSON request
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Handle response
        int responseCode = conn.getResponseCode();
System.out.println("RESPONSE CODE: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
            allocatedBudget += amount;
            remainingLabel.setText("Remaining Budget: Rs " + (totalBudget - allocatedBudget));
            JOptionPane.showMessageDialog(this, "Budget set for " + category + " in " + selectedMonth + " " + currentYear, "Success", JOptionPane.INFORMATION_MESSAGE);
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


//     private void fetchMonthlyIncome(String userId,String monthCode) {
//     SwingWorker<Double, Void> worker = new SwingWorker<>() {
//         @Override
//         protected Double doInBackground() {
//             try {
               
//                 URL url = new URL("http://localhost:8080/transactions/income/monthly?userId=" + userId + "&monthCode=" + monthCode);

//                 HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                 conn.setRequestMethod("GET");

//                 int responseCode = conn.getResponseCode();
//                 System.out.println("Response Code: " + responseCode);

//                 if (responseCode == HttpURLConnection.HTTP_OK) {
//                     try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
//                         String response = reader.readLine().trim();
//                         System.out.println("Response: " + response);
                        
//                         monthlyIncome = Double.parseDouble(response);
//                         totalBudget = monthlyIncome;
//                         allocatedBudget = 0; // Reset allocated budget when changing month
                        
//                         return monthlyIncome;
//                     }
//                 } else {
//                     System.out.println("Error: Non-OK response from server");
//                     JOptionPane.showMessageDialog(SetBudgetsPanel.this, 
//                         "Failed to fetch income for the selected month", 
//                         "Error", 
//                         JOptionPane.ERROR_MESSAGE);
//                 }
//             } catch (Exception e) {
//                 System.out.println("Exception while fetching income:");
//                 e.printStackTrace();
//                 JOptionPane.showMessageDialog(SetBudgetsPanel.this, 
//                     "Error connecting to server", 
//                     "Error", 
//                     JOptionPane.ERROR_MESSAGE);
//             }
//             return 0.0;
//         }

//         @Override
//         protected void done() {
//             try {
//                 Double income = get();
//                 if (income > 0) {
//                     remainingLabel.setText("Remaining Budget: Rs " + income);
//                 }
//             } catch (Exception e) {
//                 e.printStackTrace();
//             }
//         }
//     };

//     worker.execute();
// }

private void fetchMonthlyIncome(String userId, String monthCode) {
    SwingWorker<Double, Void> worker = new SwingWorker<>() {
        @Override
        protected Double doInBackground() {
            try {
                // Fetch both monthly income and the already allocated budget for the selected month
                URL url = new URL("http://localhost:8080/transactions/income/monthly?userId=" + userId + "&monthCode=" + monthCode);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                System.out.println("Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                        String response = reader.readLine().trim();
                        System.out.println("Response: " + response);
                        
                        monthlyIncome = Double.parseDouble(response);
                        totalBudget = monthlyIncome;

                        // Fetch the allocated budget for the selected month
                        double allocatedForMonth = fetchAllocatedBudgetForMonth(userId, monthCode);
                        allocatedBudget = allocatedForMonth;

                        // Recalculate remaining budget
                        double remainingBudget = totalBudget - allocatedBudget;
                        
                        return remainingBudget;
                    }
                } else {
                    System.out.println("Error: Non-OK response from server");
                    JOptionPane.showMessageDialog(SetBudgetsPanel.this, 
                        "Failed to fetch income for the selected month", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                System.out.println("Exception while fetching income:");
                e.printStackTrace();
                JOptionPane.showMessageDialog(SetBudgetsPanel.this, 
                    "Error connecting to server", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            return 0.0;
        }

        @Override
        protected void done() {
            try {
                Double remainingBudget = get();
                if (remainingBudget >= 0) {
                    remainingLabel.setText("Remaining Budget: Rs " + remainingBudget);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    worker.execute();
}

// A method to fetch the allocated budget for the selected month from the backend
private double fetchAllocatedBudgetForMonth(String userId, String monthCode) {
    double allocated = 0.0;
    try {
        URL url = new URL("http://localhost:8080/budgets/allocated?userId=" + userId + "&monthCode=" + monthCode);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String response = reader.readLine().trim();
                allocated = Double.parseDouble(response);
            }
        } else {
            System.out.println("Error: Non-OK response from server");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return allocated;
}



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

    // Utility Methods
    private String convertMonthToCode(String monthName) {
        switch(monthName) {
            case "January": return "01";
            case "February": return "02";
            case "March": return "03";
            case "April": return "04";
            case "May": return "05";
            case "June": return "06";
            case "July": return "07";
            case "August": return "08";
            case "September": return "09";
            case "October": return "10";
            case "November": return "11";
            case "December": return "12";
            default: return "01";
        }
    }

    private String getCurrentMonthName() {
        return new SimpleDateFormat("MMMM").format(new Date());
    }

    private String getCurrentMonthCode() {
        return new SimpleDateFormat("MM").format(new Date());
    }

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
        if (json.length() > 1) json.setLength(json.length() - 1);
        json.append("}");
        return json.toString();
    }
}