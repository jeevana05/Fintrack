package com.example.fintrack.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "predict") // MongoDB collection name
public class Predict {

    @Id
    private String id; // MongoDB document ID
    private String userId;
    private double yearlyBudget;

    // Constructors
    public Predict() {
    }

    public Predict(String userId, double yearlyBudget) {
        this.userId = userId;
        this.yearlyBudget = yearlyBudget;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getYearlyBudget() {
        return yearlyBudget;
    }

    public void setYearlyBudget(double yearlyBudget) {
        this.yearlyBudget = yearlyBudget;
    }
}
