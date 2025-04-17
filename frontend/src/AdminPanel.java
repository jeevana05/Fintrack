
 import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AdminPanel extends JPanel {
    private JButton viewUsersButton, viewTransactionsButton, viewBudgetsButton;
    private final String BASE_URL = "http://localhost:8080/admin";

    public AdminPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(250, 250, 250));

        // Title
        JLabel titleLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setBorder(new EmptyBorder(20, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 20, 20));
        buttonPanel.setBorder(new EmptyBorder(30, 300, 30, 300));
        buttonPanel.setBackground(new Color(250, 250, 250));

        viewUsersButton = createStyledButton("View All Users");
        viewTransactionsButton = createStyledButton("View All Transactions");
        viewBudgetsButton = createStyledButton("View All Budgets");

        viewUsersButton.addActionListener(e -> fetchAndShow("Users", "/users"));
        viewTransactionsButton.addActionListener(e -> fetchAndShow("Transactions", "/transactions"));
        viewBudgetsButton.addActionListener(e -> fetchAndShow("Budgets", "/budgets"));

        buttonPanel.add(viewUsersButton);
        buttonPanel.add(viewTransactionsButton);
        buttonPanel.add(viewBudgetsButton);

        add(buttonPanel, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setPreferredSize(new Dimension(200, 40));
        button.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 1, true));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));
            }
        });

        return button;
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
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(this, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
    }

    // Optional: getters for testing
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