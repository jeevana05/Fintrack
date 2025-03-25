package com.example.fintrack.controller;

import com.example.fintrack.model.Budget;
import com.example.fintrack.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/budgets")
@CrossOrigin(origins = "*")
public class BudgetController {

    @Autowired
    private BudgetRepository budgetRepository;

    @PostMapping("/add")
    public Budget addBudget(@RequestBody Budget budget) {
        System.out.println("Received budget: " + budget);
        return budgetRepository.save(budget);
    }

    @GetMapping("/all")
    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
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
}
