package com.example.fintrack.controller;

import com.example.fintrack.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")  // Allow requests from Swing frontend
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/users")
    public List<String> getAllUsers() {
        return adminService.fetchAllUsers();
    }

    @GetMapping("/transactions")
    public List<String> getAllTransactions() {
        return adminService.fetchAllTransactions();
    }

    @GetMapping("/budgets")
    public List<String> getAllBudgets() {
        return adminService.fetchAllBudgets();
    }
}
