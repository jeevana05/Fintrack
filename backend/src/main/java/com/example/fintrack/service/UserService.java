package com.example.fintrack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fintrack.model.User;
import com.example.fintrack.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User registerUser(User user) {
        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        
        // In a real application, you would hash the password here
        // user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Save user to database
        return userRepository.save(user);
    }
    
    public User authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        
        // If user exists and password matches
        if (user != null && user.getPassword().equals(password)) {
            // In a real app, you would use: passwordEncoder.matches(password, user.getPassword())
            return user;
        }
        
        return null; // Authentication failed
    }
}