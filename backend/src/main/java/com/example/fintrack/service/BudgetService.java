package com.example.fintrack.service;

import com.example.fintrack.model.Budget;
import com.example.fintrack.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    public Budget saveBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    public Budget getBudgetById(String id) {
        return budgetRepository.findById(id).orElse(null);
    }

    public List<Budget> getBudgetsByCategory(String category) {
        return budgetRepository.findByCategory(category);
    }

    public void deleteBudget(String id) {
        budgetRepository.deleteById(id);
    }
}
