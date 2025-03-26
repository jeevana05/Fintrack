package com.example.fintrack.service;

import java.util.List;
import java.util.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fintrack.model.Transaction;
import com.example.fintrack.repository.TransactionRepository;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    private final MongoTemplate mongoTemplate;

    public TransactionService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    public Transaction addTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public List<Transaction> getTransactionsByType(String type) {
        return transactionRepository.findByType(type);
    }

    public void deleteTransaction(String id) {
        transactionRepository.deleteById(id);
    }

    // Method to calculate total income for the current month
     public double getMonthlyIncome(String monthCode) {
    // Get the current year
    int currentYear = LocalDate.now().getYear();
    
    // Construct YearMonth using the current year and provided month code
    YearMonth yearMonth = YearMonth.of(currentYear, Integer.parseInt(monthCode));

    // Aggregation pipeline to sum income transactions for the specified month
    Aggregation aggregation = Aggregation.newAggregation(
        Aggregation.match(
            Criteria.where("type").is("Income")
                    .and("date").gte(yearMonth.atDay(1))
                    .lt(yearMonth.plusMonths(1).atDay(1))
        ),
        Aggregation.group().sum("amount").as("totalIncome")
    );

    AggregationResults<IncomeResult> result = mongoTemplate.aggregate(aggregation, "transaction", IncomeResult.class);

    // Return the calculated total income, defaulting to 0 if no income found
    return result.getUniqueMappedResult() != null ? result.getUniqueMappedResult().totalIncome : 0.0;
}

// Helper class to map aggregation result
private static class IncomeResult {
    double totalIncome;
}

}
