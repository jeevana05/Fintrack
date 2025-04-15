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

    // ✅ Save budget for a specific user
    public Budget saveBudget(String userId, Budget budget) {
        budget.setUserId(userId); // Ensure the budget is linked to a user
        return budgetRepository.save(budget);
    }

    // ✅ Get budgets for a specific user
    public List<Budget> getBudgetsByUser(String userId) {
        return budgetRepository.findByUserId(userId);
    }

    public Budget getBudgetById(String id) {
        return budgetRepository.findById(id).orElse(null);
    }

    // ✅ Get budgets for a user by category
    public List<Budget> getBudgetsByCategory(String userId, String category) {
        return budgetRepository.findByUserIdAndCategory(userId, category);
    }

    public void deleteBudget(String id) {
        budgetRepository.deleteById(id);
    }
   public double getAllocatedBudget(String userId, String monthCode) {
    int year = 2025;
    // Adjust monthCode to be 2-digit string (01 for January, 02 for February, ..., 12 for December)
   
    List<Budget> budgets = budgetRepository.findByUserIdAndYearAndMonth(userId, year, monthCode);
    return budgets.stream().mapToDouble(Budget::getAmount).sum();  // Summing up all allocated budgets for the user and month
}

    // ✅ Update budget for a specific user
    @Transactional
    public double updateBudget(String userId, String category, double amount, String transactionDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate parsedDate = LocalDate.parse(transactionDate, formatter);

            int year = parsedDate.getYear();
            String month = String.format("%02d", parsedDate.getMonthValue()); 

            // ✅ Fetch only the logged-in user's budget for the given month & category
             List<Budget> budgets = budgetRepository.findByUserIdAndYearAndMonthAndCategory(userId, year, month, category);

            if (budgets.isEmpty()) {
                return -1; // No budget found for user
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
