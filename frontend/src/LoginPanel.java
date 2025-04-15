import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.JFrame;

public class LoginPanel extends JPanel {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;

    public LoginPanel() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("User Login");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(33, 150, 243));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(15);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(15);

        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(33, 150, 243));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setFocusPainted(false);

        registerButton = new JButton("Register");
        registerButton.setBackground(new Color(76, 175, 80));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setFocusPainted(false);

        // Add action listeners
        loginButton.addActionListener(this::handleLogin);
        registerButton.addActionListener(this::showRegistrationDialog);

        // Layout setup
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(title, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(usernameLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        add(passwordLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);
        gbc.gridx = 1;
        add(registerButton, gbc);
    }

    private void handleLogin(ActionEvent e) {
    final String username = usernameField.getText().trim(); // Moved here
    final String password = new String(passwordField.getPassword());

    new SwingWorker<Boolean, Void>() {
        @Override
        protected Boolean doInBackground() throws Exception {
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginPanel.this, 
                    "Username and password cannot be empty", 
                    "Login Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }

            try {
                Map<String, String> loginData = new HashMap<>();
                loginData.put("username", username);
                loginData.put("password", password);

                String json = String.format(
                        "{\"username\":\"%s\", \"password\":\"%s\"}",
                        loginData.get("username"),
                        loginData.get("password")
                );

                URL url = new URI("http://localhost:8080/auth/login").toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = json.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // Store token if needed here

                    return true;
                } else {
                    return false;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }

        @Override
		protected void done() {
			try {
				boolean success = get();
				if (success) {
					JOptionPane.showMessageDialog(LoginPanel.this, 
						"Login successful!", 
						"Success", 
						JOptionPane.INFORMATION_MESSAGE);

					// Check for admin
					if (username.equalsIgnoreCase("admin")) {
						JFrame adminFrame = new JFrame("Admin Dashboard");
						adminFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
						adminFrame.setSize(600, 400);
						adminFrame.setLocationRelativeTo(null);
						adminFrame.add(new AdminPanel()); // Open Admin Panel
						adminFrame.setVisible(true);
					} else {
						new FinanceTrackerHome(username); // For regular users
					}

					usernameField.setText("");
					passwordField.setText("");
				} else {
					JOptionPane.showMessageDialog(LoginPanel.this, 
						"Invalid username or password", 
						"Login Failed", 
						JOptionPane.ERROR_MESSAGE);
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(LoginPanel.this, 
					"Error: " + ex.getMessage(), 
					"Login Error", 
					JOptionPane.ERROR_MESSAGE);
			}
		}
    }.execute();
}


    private void showRegistrationDialog(ActionEvent e) {
        // Create a registration dialog
        RegistrationDialog dialog = new RegistrationDialog(this);
        dialog.setVisible(true);
    }
}