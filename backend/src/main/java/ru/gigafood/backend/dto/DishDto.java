package ru.gigafood.backend.dto;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

public class DishDto {
    public record analyzeRequest(MultipartFile file) {
    }
    public record analyzeResponse(String foodName, Integer weight, Integer caloriesEstimated, Integer proteinEstimated, Integer fatsEstimated, Integer carbsEstimated, Date mealTime) {
    }
    public record redactRequest(String photoUrl, String foodName, Integer weight, Integer caloriesEstimated, Integer proteinEstimated, Integer fatsEstimated, Integer carbsEstimated, Date mealTime) {
    }
    public record redactResponse(String message) {
    }

}
