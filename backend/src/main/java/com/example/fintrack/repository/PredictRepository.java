package com.example.fintrack.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.fintrack.model.Predict;

@Repository
public interface PredictRepository extends MongoRepository<Predict, String> {

    Predict findByUserId(String userId); // Custom query to fetch by userId
}
