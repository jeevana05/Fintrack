package com.example.fintrack.repository;

import com.example.fintrack.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import java.util.Date;
import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {

    List<Transaction> findByType(String type);
     List<Transaction> findByUserId(String userId);

    @Query("{ 'type': 'Income', 'date': { $gte: ?0, $lte: ?1 } }")
    Double getTotalIncomeForMonth(Date startDate, Date endDate);
}
