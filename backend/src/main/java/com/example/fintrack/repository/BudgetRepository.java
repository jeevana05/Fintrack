package com.example.fintrack.repository;

import com.example.fintrack.model.Budget;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BudgetRepository extends MongoRepository<Budget, String> {
    List<Budget> findByUserId(String userId); // Fetch budgets by userId  
    List<Budget> findByCategory(String category);
    List<Budget> findByDueDate(LocalDate dueDate);  
    List<Budget> findByCategoryAndYearAndMonth(String category, int year, String month);
    List<Budget> findByUserIdAndYearAndMonth(String userId, int year, String month); // Fetch user's budgets by month & year
    List<Budget> findByUserIdAndCategory(String userId, String category); // ✅ Add this method
}
