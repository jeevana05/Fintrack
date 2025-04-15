package com.example.fintrack.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.*;
import com.example.fintrack.service.TransactionService;

import com.example.fintrack.model.Transaction;
import com.example.fintrack.repository.TransactionRepository;

@RestController
@RequestMapping("/transactions")
@CrossOrigin(origins = "*") // Allow requests from Swing
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private MongoTemplate mongoTemplate; // MongoDB template for aggregation

    @PostMapping("/add")
    public Map<String, Object> addTransaction(@RequestBody Map<String, Object> transactionData) {
        // Extract values from the Map
        String userId = (String) transactionData.get("userId");
        String type = (String) transactionData.get("type");
        double amount = Double.parseDouble(transactionData.get("amount").toString());
        String category = (String) transactionData.get("category");
        String dateStr = (String) transactionData.get("date");
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Create Transaction object
         Transaction transaction = new Transaction(userId, type, amount, category, date);

        // Save transaction in MongoDB
        transactionRepository.save(transaction);

        // If it's an income transaction, calculate the total income for the month
       
        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("transaction", transaction);
        
        return response;
    }

    @GetMapping("/all")
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    @Autowired
private TransactionService transactionService;
@GetMapping("/user")
public List<Transaction> getUserTransactions(@RequestParam String userId) {
    return transactionRepository.findByUserId(userId);
}


@GetMapping("/income/monthly")
public double getMonthlyIncome(@RequestParam String userId, @RequestParam String monthCode) {
    return transactionService.getMonthlyIncome(userId, monthCode);
}
@GetMapping("/report/monthly")
public Map<String, Double> getMonthlyReport(@RequestParam String userId, @RequestParam String monthCode) {
    return transactionService.getMonthlyReport(userId, monthCode);
}

@GetMapping("/report/yearly")
public Map<String, Double> getYearlyReport(@RequestParam String userId, @RequestParam int year) {
    return transactionService.getYearlyReport(userId, year);
}
/*     @GetMapping("/budget/current")
    public Map<String, Double> getCurrentBudget(@RequestParam String userId, @RequestParam int year) {
        return transactionService.getCurrentBudget(userId, year);
    } */
@GetMapping("/budget/current")
public Map<String, Object> getCurrentBudgetAndPredictions(@RequestParam String userId, @RequestParam int year) {
    return transactionService.getCurrentBudgetAndPredictions(userId, year);
}
}

