import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class RegistrationDialog extends JDialog {

    private JTextField usernameField, emailField;
    private JPasswordField passwordField, confirmPasswordField;

    public RegistrationDialog(Component parent) {
        super((Frame) SwingUtilities.getWindowAncestor(parent), "Register New Account", true);
        
        setSize(400, 300);
        setLocationRelativeTo(parent);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(33, 150, 243));
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(15);
        
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(15);
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(15);
        
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordField = new JPasswordField(15);
        
        JButton registerButton = new JButton("Register");
        registerButton.setBackground(new Color(76, 175, 80));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setFocusPainted(false);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(244, 67, 54));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setFocusPainted(false);
        
        // Add action listeners
        registerButton.addActionListener(this::handleRegistration);
        cancelButton.addActionListener(e -> dispose());
        
        // Layout setup
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(title, gbc);
        
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(usernameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(emailLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(emailField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(passwordField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(confirmPasswordLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(confirmPasswordField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(registerButton, gbc);
        gbc.gridx = 1;
        panel.add(cancelButton, gbc);
        
        add(panel, BorderLayout.CENTER);
    }
    
    private void handleRegistration(ActionEvent e) {
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                String username = usernameField.getText().trim();
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                
                // Validate input
                if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(RegistrationDialog.this, 
                        "All fields are required", 
                        "Registration Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                
                // Check if passwords match
                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(RegistrationDialog.this, 
                        "Passwords do not match", 
                        "Registration Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                
                // Simple email validation
                if (!email.contains("@") || !email.contains(".")) {
                    JOptionPane.showMessageDialog(RegistrationDialog.this, 
                        "Please enter a valid email address", 
                        "Registration Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                
                try {
                    // Create a Map for the JSON structure
                    Map<String, String> userData = new HashMap<>();
                    userData.put("username", username);
                    userData.put("email", email);
                    userData.put("password", password);
                    
                    // Convert Map to JSON string
                    String json = String.format(
                            "{\"username\":\"%s\", \"email\":\"%s\", \"password\":\"%s\"}",
                            userData.get("username"),
                            userData.get("email"),
                            userData.get("password")
                    );
                    
                    // Send HTTP POST request to backend
                    URL url = new URI("http://localhost:8080/auth/register").toURL();
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    
                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = json.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }
                    
                    int responseCode = conn.getResponseCode();
                    return responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED;
                    
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
                        JOptionPane.showMessageDialog(RegistrationDialog.this, 
                            "Registration successful! Please login with your new account.", 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(RegistrationDialog.this, 
                            "Registration failed. Username may already be taken.", 
                            "Registration Failed", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(RegistrationDialog.this, 
                        "Error: " + ex.getMessage(), 
                        "Registration Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}