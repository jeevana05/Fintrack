package com.example.fintrack.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fintrack.model.Transaction;
import com.example.fintrack.repository.TransactionRepository;

@RestController
@RequestMapping("/transactions")
@CrossOrigin(origins = "*") // Allow requests from Swing
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @PostMapping("/add")
    public Transaction addTransaction(@RequestBody Map<String, Object> transactionData) {
        // Extract values from the Map
        String type = (String) transactionData.get("type");
        double amount = Double.parseDouble(transactionData.get("amount").toString());
        String category = (String) transactionData.get("category");
        String dateStr = (String) transactionData.get("date");
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        // Create Transaction object
        Transaction transaction = new Transaction(type, amount, category, date);

        // Save transaction in MongoDB
        return transactionRepository.save(transaction);
    }

    @GetMapping("/all")
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
