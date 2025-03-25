package com.example.fintrack.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.fintrack.model.Transaction;

public interface TransactionRepository extends MongoRepository<Transaction, String> {

    List<Transaction> findByType(String type);
}
