package com.example.fintrack.dto;

public class BudgetResponse {
    private double currentBudget;
    private double[] futureBudgets;

    // Constructor
    public BudgetResponse(double currentBudget, double[] futureBudgets) {
        this.currentBudget = currentBudget;
        this.futureBudgets = futureBudgets;
    }

    // Getters
    public double getCurrentBudget() {
        return currentBudget;
    }

    public double[] getFutureBudgets() {
        return futureBudgets;
    }

    // Setters (Optional)
    public void setCurrentBudget(double currentBudget) {
        this.currentBudget = currentBudget;
    }

    public void setFutureBudgets(double[] futureBudgets) {
        this.futureBudgets = futureBudgets;
    }
}
