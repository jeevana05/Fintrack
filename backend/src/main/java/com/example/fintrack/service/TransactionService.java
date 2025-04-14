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

   // Method to calculate total income for the current month for a specific user
public double getMonthlyIncome(String userId, String monthCode) {
    // Get the current year
    int currentYear = LocalDate.now().getYear();

    // Construct YearMonth using the current year and provided month code
    YearMonth yearMonth = YearMonth.of(currentYear, Integer.parseInt(monthCode));

    // Aggregation pipeline to sum income transactions for the specified month and user
    Aggregation aggregation = Aggregation.newAggregation(
        Aggregation.match(
            Criteria.where("type").is("Income")
                    .and("userId").is(userId) // Filter by user
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
// Monthly income and expenses
public Map<String, Double> getMonthlyReport(String userId, String monthCode) {
    int currentYear = LocalDate.now().getYear();
    YearMonth yearMonth = YearMonth.of(currentYear, Integer.parseInt(monthCode));

    Date startDate = Date.from(yearMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
    Date endDate = Date.from(yearMonth.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

    Map<String, Double> report = new HashMap<>();

    // Income
    Aggregation incomeAgg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("type").is("Income")
            .and("userId").is(userId)
            .and("date").gte(startDate).lt(endDate)),
        Aggregation.group().sum("amount").as("total")
    );
    double income = getTotalAmount(incomeAgg);

    // Expense
    Aggregation expenseAgg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("type").is("Expense")
            .and("userId").is(userId)
            .and("date").gte(startDate).lt(endDate)),
        Aggregation.group().sum("amount").as("total")
    );
    double expense = getTotalAmount(expenseAgg);

    report.put("income", income);
    report.put("expense", expense);

    return report;
}

// Yearly total aggregation
public Map<String, Double> getYearlyReport(String userId, int year) {
    LocalDate start = LocalDate.of(year, 1, 1);
    LocalDate end = start.plusYears(1);

    Date startDate = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant());
    Date endDate = Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant());

    Map<String, Double> report = new HashMap<>();

    Aggregation incomeAgg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("type").is("Income")
            .and("userId").is(userId)
            .and("date").gte(startDate).lt(endDate)),
        Aggregation.group().sum("amount").as("total")
    );
    double income = getTotalAmount(incomeAgg);

    Aggregation expenseAgg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("type").is("Expense")
            .and("userId").is(userId)
            .and("date").gte(startDate).lt(endDate)),
        Aggregation.group().sum("amount").as("total")
    );
    double expense = getTotalAmount(expenseAgg);

    report.put("income", income);
    report.put("expense", expense);

    return report;
}

private double getTotalAmount(Aggregation aggregation) {
    AggregationResults<Result> results = mongoTemplate.aggregate(aggregation, "transaction", Result.class);
    return results.getUniqueMappedResult() != null ? results.getUniqueMappedResult().total : 0.0;
}

private static class Result {
    public double total;
}

}
