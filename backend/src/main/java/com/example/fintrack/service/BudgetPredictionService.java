package com.example.fintrack.service;

import com.example.fintrack.dto.BudgetResponse;
import com.example.fintrack.model.Predict;
import com.example.fintrack.repository.PredictRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.aggregation.*;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class BudgetPredictionService {

    private final PredictRepository predictRepository;
    private final MongoTemplate mongoTemplate;

    public BudgetPredictionService(PredictRepository predictRepository, MongoTemplate mongoTemplate) {
        this.predictRepository = predictRepository;
        this.mongoTemplate = mongoTemplate;
    }

    // Aggregate yearly budget from "budgets" collection directly
    public void updateYearlyBudgetForUser(String userId, int year) {
        MatchOperation match = match(Criteria.where("userId").is(userId).and("year").is(String.valueOf(year)));
        GroupOperation group = group("userId").sum("amount").as("yearlyBudget");

        Aggregation aggregation = newAggregation(match, group);
        AggregationResults<YearlyBudgetResult> result = mongoTemplate.aggregate(aggregation, "budgets", YearlyBudgetResult.class);

        List<YearlyBudgetResult> budgetList = result.getMappedResults();
        double yearlyBudget = budgetList.isEmpty() ? 0.0 : budgetList.get(0).getYearlyBudget();

        Predict predict = predictRepository.findByUserId(userId);
        if (predict == null) {
            predict = new Predict();
            predict.setUserId(userId);
        }

        predict.setYearlyBudget(yearlyBudget);
        predictRepository.save(predict);
    }

    public BudgetResponse predictFutureBudgets(String userId) {
        Predict predict = predictRepository.findByUserId(userId);
        if (predict == null) {
            throw new RuntimeException("User not found");
        }

        double currentBudget = predict.getYearlyBudget();
        double[] futureBudgets = new double[3];
        double[] inflationRates = {0.06, 0.07, 0.08};

        for (int i = 0; i < 3; i++) {
            currentBudget += currentBudget * inflationRates[i];
            futureBudgets[i] = currentBudget;
        }

        return new BudgetResponse(predict.getYearlyBudget(), futureBudgets);
    }

    // Helper class to hold aggregation result
    private static class YearlyBudgetResult {
        private double yearlyBudget;

        public double getYearlyBudget() {
            return yearlyBudget;
        }

        public void setYearlyBudget(double yearlyBudget) {
            this.yearlyBudget = yearlyBudget;
        }
    }
}
