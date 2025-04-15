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
public Map<String, Double> getCurrentBudget(String userId, int year) {
    LocalDate start = LocalDate.of(year, 1, 1);
    LocalDate end = start.plusYears(1);

    Date startDate = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant());
    Date endDate = Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant());

    Map<String, Double> result = new HashMap<>();

    Aggregation expenseAgg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("type").is("Expense")
            .and("userId").is(userId)
            .and("date").gte(startDate).lt(endDate)),
        Aggregation.group().sum("amount").as("total")
    );

    double currentBudget = getTotalAmount(expenseAgg);
    result.put("currentBudget", currentBudget);

    return result;
}


private double getTotalAmount(Aggregation aggregation) {
    AggregationResults<Result> results = mongoTemplate.aggregate(aggregation, "transaction", Result.class);
    return results.getUniqueMappedResult() != null ? results.getUniqueMappedResult().total : 0.0;
}

private static class Result {
    public double total;
}
public Map<String, Object> getCurrentBudgetAndPredictions(String userId, int year) {
        // Fetch current budget
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = start.plusYears(1);

        Date startDate = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Map<String, Object> result = new HashMap<>();

        Aggregation expenseAgg = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("type").is("Expense")
                .and("userId").is(userId)
                .and("date").gte(startDate).lt(endDate)),
            Aggregation.group().sum("amount").as("total")
        );

        double currentBudget = getTotalAmount(expenseAgg);
        result.put("currentBudget", currentBudget);

        // Fetch future budget predictions based on current budget
        double[] futureBudgets = predictFutureBudgets(currentBudget);
        result.put("futureBudgets", futureBudgets);

        return result;
    }

/*     private double getTotalAmount(Aggregation aggregation) {
        // Execute the aggregation query
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "transaction", Map.class);
        Map resultMap = results.getUniqueMappedResult();
        return resultMap != null ? (double) resultMap.get("total") : 0.0;
    } */

    // Method to predict future budgets based on current budget and inflation rates
    private double[] predictFutureBudgets(double currentBudget) {
        double[] futureBudgets = new double[3];
        double[] inflationRates = {0.06, 0.07, 0.08};  // 6%, 7%, 8% inflation for the next 3 years

        for (int i = 0; i < 3; i++) {
            currentBudget += currentBudget * inflationRates[i];
            futureBudgets[i] = currentBudget;
        }

        return futureBudgets;
    }

}
