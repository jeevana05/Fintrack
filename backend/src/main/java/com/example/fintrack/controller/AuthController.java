package com.example.fintrack.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fintrack.model.User;
import com.example.fintrack.service.UserService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*") // Allow requests from Swing
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> registrationData) {
        try {
            // Extract registration data
            String username = registrationData.get("username");
            String email = registrationData.get("email");
            String password = registrationData.get("password");
            
            // Create new user
            User newUser = new User(username, email, password);
            
            // Register user
            User registeredUser = userService.registerUser(newUser);
            
            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User registered successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginData) {
        try {
            // Extract login data
            String username = loginData.get("username");
            String password = loginData.get("password");
            
            // Authenticate user
            User user = userService.authenticateUser(username, password);
            
            if (user != null) {
                // Create success response
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("username", user.getUsername());
                response.put("email", user.getEmail());
                response.put("role", user.getRole());
                
                // In a real app, you might generate and return a JWT token here
                // response.put("token", jwtTokenProvider.generateToken(user));
                
                return ResponseEntity.ok(response);
            } else {
                // Authentication failed
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Invalid username or password");
                
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}