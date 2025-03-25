package com.example.fintrack.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "budgets")
public class Budget {

    @Id
    private String id;
    private String category;
    private double amount;
    private double allocatedAmount;
    private LocalDate dueDate;

    public Budget() {}

    public Budget(String category, double amount, double allocatedAmount, LocalDate dueDate) {
        this.category = category;
        this.amount = amount;
        this.allocatedAmount = allocatedAmount;
        this.dueDate = dueDate;
    }

    public String getId() { return id; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public double getAllocatedAmount() { return allocatedAmount; }
    public LocalDate getDueDate() { return dueDate; }

    public void setCategory(String category) { this.category = category; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setAllocatedAmount(double allocatedAmount) { this.allocatedAmount = allocatedAmount; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}
