package com.example.fintrack.service;

import com.example.fintrack.model.Budget;
import com.example.fintrack.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private BudgetRepository budgetRepository;

    public void checkDueDatesAndNotify() {
        LocalDate today = LocalDate.now();
        List<Budget> dueBudgets = budgetRepository.findByDueDate(today);
        List<Budget> upcomingBudgets = budgetRepository.findByDueDate(today.plusDays(1));

        for (Budget budget : dueBudgets) {
            System.out.println("⚠ Reminder: Payment due today for " + budget.getCategory());
        }

        for (Budget budget : upcomingBudgets) {
            System.out.println("🔔 Reminder: Payment due tomorrow for " + budget.getCategory());
        }
    }

    public void checkBudgetLimits() {
        List<Budget> budgets = budgetRepository.findAll();
        for (Budget budget : budgets) {
            if (budget.getRemainingAmount() > budget.getAmount()) {
                System.out.println("🚨 Alert: Budget exceeded for " + budget.getCategory());
            }
        }
    }
}
