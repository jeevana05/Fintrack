import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AdminPanel extends JPanel {
    private JButton viewUsersButton, viewTransactionsButton, viewBudgetsButton;
    private final String BASE_URL = "http://localhost:8080/admin";

    public AdminPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // Title
        JLabel titleLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 152, 219));
        titleLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 20, 20));
        buttonPanel.setBorder(new EmptyBorder(50, 150, 50, 150));
        buttonPanel.setBackground(new Color(236, 240, 241));

        viewUsersButton = new JButton("View All Users");
        viewTransactionsButton = new JButton("View All Transactions");
        viewBudgetsButton = new JButton("View All Budgets");

        // Style buttons
        JButton[] buttons = { viewUsersButton, viewTransactionsButton, viewBudgetsButton };
        for (JButton btn : buttons) {
            btn.setFont(new Font("Arial", Font.BOLD, 16));
            btn.setBackground(new Color(52, 152, 219));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
        }

        // Add action listeners
        viewUsersButton.addActionListener(e -> fetchAndShow("Users", "/users"));
        viewTransactionsButton.addActionListener(e -> fetchAndShow("Transactions", "/transactions"));
        viewBudgetsButton.addActionListener(e -> fetchAndShow("Budgets", "/budgets"));

        buttonPanel.add(viewUsersButton);
        buttonPanel.add(viewTransactionsButton);
        buttonPanel.add(viewBudgetsButton);

        add(buttonPanel, BorderLayout.CENTER);
    }

    private void fetchAndShow(String title, String endpoint) {
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                try {
                    URL url = new URL(BASE_URL + endpoint);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line).append("\n");
                    }
                    in.close();
                    return response.toString();
                } catch (Exception ex) {
                    return "Error: " + ex.getMessage();
                }
            }

            @Override
            protected void done() {
                try {
                    String result = get();
                    showResultDialog(title, result);
                } catch (Exception ex) {
                    showResultDialog("Error", "Failed to fetch data.");
                }
            }
        };
        worker.execute();
    }

    private void showResultDialog(String title, String data) {
        JTextArea textArea = new JTextArea(data);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(this, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
    }

    // Getters (if needed externally)
    public JButton getViewUsersButton() {
        return viewUsersButton;
    }

    public JButton getViewTransactionsButton() {
        return viewTransactionsButton;
    }

    public JButton getViewBudgetsButton() {
        return viewBudgetsButton;
    }
}
