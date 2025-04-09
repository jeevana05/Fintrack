package com.example.fintrack.controller;

import com.example.fintrack.model.Budget;
import com.example.fintrack.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.fintrack.service.BudgetService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.example.fintrack.service.TransactionService;


@RestController
@RequestMapping("/budgets")
@CrossOrigin(origins = "*")
public class BudgetController {

    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
private BudgetService budgetService;

    @PostMapping("/add")
    public Budget addBudget(@RequestBody Budget budget) {
        System.out.println("Received budget: " + budget);
        return budgetRepository.save(budget);
    }
@Autowired
private TransactionService transactionService;

//     @PostMapping("/add")
// public Budget addBudget(@RequestBody Budget budget) {
//     String userId = budget.getuserId();
//     String month = budget.getMonth();
//     int year = budget.getYear();
//     System.out.println("Received budget: " + budget);
//     System.out.println("User ID: " + userId + ", Month: " + month + ", Year: " + year);
//     // Construct monthCode in format yyyyMM
//     String monthCode = year + String.format("%02d", Integer.parseInt(month));
    
//     // ✅ Get income for this specific user for that month
//     double monthlyIncome = transactionService.getMonthlyIncome(userId, monthCode);

//     budget.setAmount(monthlyIncome);
//     budget.setRemainingAmount(monthlyIncome); // Full amount available initially

//     return budgetRepository.save(budget);
// }


   @GetMapping("/all")
public List<Budget> getAllBudgets(@RequestParam String userId) {
    return budgetRepository.findByUserId(userId);
}
    @GetMapping("/allocated")
    public ResponseEntity<Double> getAllocatedBudget(@RequestParam String userId, @RequestParam String monthCode) {
        double allocatedBudget = budgetService.getAllocatedBudget(userId, monthCode);
        return ResponseEntity.ok(allocatedBudget);
    }

    @GetMapping("/{id}")
    public Optional<Budget> getBudgetById(@PathVariable String id) {
        return budgetRepository.findById(id);
    }

    @GetMapping("/category/{category}")
    public List<Budget> getBudgetsByCategory(@PathVariable String category) {
        return budgetRepository.findByCategory(category);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteBudget(@PathVariable String id) {
        budgetRepository.deleteById(id);
        return "Budget deleted successfully";
    }

   @PostMapping("/update")
public ResponseEntity<Double> updateBudget(
    @RequestParam String userId,
    @RequestParam String category,
    @RequestParam double amount,
    @RequestParam String transactionDate
) {
    try {
        double updated = budgetService.updateBudget(userId,category, amount, transactionDate);

        if (updated >= 0) {
            System.out.println("Budget updated successfully");
            return ResponseEntity.ok(updated); // ✅ Return numeric value
        } else {
            return ResponseEntity.status(400).body(-1.0); // ✅ Return -1 if budget not found
        }
    } catch (DateTimeParseException e) {
        return ResponseEntity.status(400).body(-2.0); // ✅ Return -2 for date format errors
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body(-3.0); // ✅ Return -3 for internal server error
    }
}

}
