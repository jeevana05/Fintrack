package com.example.fintrack.service;

import com.example.fintrack.model.Budget;
import com.example.fintrack.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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

   @Transactional
public double updateBudget(String category, double amount, String transactionDate) {
    try {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsedDate = LocalDate.parse(transactionDate, formatter);

        int year = parsedDate.getYear();
        String month = String.format("%02d", parsedDate.getMonthValue()); 

        List<Budget> budgets = budgetRepository.findByCategoryAndYearAndMonth(category, year, month);

        if (budgets.isEmpty()) {
            return -1; // Return -1 if no budget found
        }

        Budget budget = budgets.get(0);
        System.out.println("Before update - Remaining Budget: " + budget.getRemainingAmount());

        double newRemainingBudget = budget.getRemainingAmount() - amount;

        if (newRemainingBudget < 0) {
            newRemainingBudget = 0;
        }

        budget.setRemainingAmount(newRemainingBudget);
        budgetRepository.save(budget);

        return newRemainingBudget;

    } catch (DateTimeParseException e) {
        throw new IllegalArgumentException("Invalid date format. Expected format: YYYY-MM-DD");
    }
}

}