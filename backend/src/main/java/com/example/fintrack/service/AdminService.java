package com.example.fintrack.service;

import com.example.fintrack.model.User;
import com.example.fintrack.model.Transaction;
import com.example.fintrack.model.Budget;
import com.example.fintrack.repository.UserRepository;
import com.example.fintrack.repository.TransactionRepository;
import com.example.fintrack.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    public List<String> fetchAllUsers() {
        return userRepository.findAll().stream()
                .map(User::getUsername)  // Assuming the User model has getUsername() method
                .collect(Collectors.toList());
    }

    public List<String> fetchAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(transaction -> transaction.getUserId() + " - " + transaction.getType() +
                        " - " + transaction.getCategory() + " - $" + transaction.getAmount())
                .collect(Collectors.toList());
    }

    public List<String> fetchAllBudgets() {
        return budgetRepository.findAll().stream()
                .map(budget -> budget.getuserId() + " - " + budget.getCategory() + " - $" + budget.getAmount())
                .collect(Collectors.toList());
    }
}
