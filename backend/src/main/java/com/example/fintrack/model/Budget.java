package com.example.fintrack.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "budgets")
public class Budget {

    @Id
   
    private String id;
    private String userId; 
    private String category;
    private double amount;
    private double remainingAmount;
    private LocalDate dueDate;
    private String month; // Added month field
    private int year;     // Added year field

    public Budget() {}

    public Budget(String userId,String category, double amount, double remainingAmount, LocalDate dueDate, String month, int year) {
        this.userId = userId;
        this.category = category;
        this.amount = amount;
        this.remainingAmount = remainingAmount;
        this.dueDate = dueDate;
        this.month = month;
        this.year = year;
    }

    public String getuserId() { return userId; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public double getRemainingAmount() { return remainingAmount; }
    public LocalDate getDueDate() { return dueDate; }
    public String getMonth() { return month; }
    public int getYear() { return year; }
    

    public void setCategory(String category) { this.category = category; }
     public void setUserId(String userId) { this.userId = userId; } 
    public void setAmount(double amount) { this.amount = amount; }
    public void setRemainingAmount(double remainingAmount) { this.remainingAmount = remainingAmount; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setMonth(String month) { this.month = month; }
    public void setYear(int year) { this.year = year; }
}
