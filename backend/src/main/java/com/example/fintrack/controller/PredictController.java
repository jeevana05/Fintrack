package com.example.fintrack.controller;

import com.example.fintrack.dto.BudgetResponse;
import com.example.fintrack.service.BudgetPredictionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.time.Year;


@RestController
@RequestMapping("/predict")
public class PredictController {

    private final BudgetPredictionService budgetPredictionService;

    public PredictController(BudgetPredictionService budgetPredictionService) {
        this.budgetPredictionService = budgetPredictionService;
    }
	@GetMapping("/fetch")
	public ResponseEntity<?> fetchAndPredictBudget(@RequestParam String userId) {
		try {
			int currentYear = Year.now().getValue();
			budgetPredictionService.updateYearlyBudgetForUser(userId, currentYear);
			return ResponseEntity.ok(budgetPredictionService.predictFutureBudgets(userId));
		} catch (RuntimeException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		}
	}
}