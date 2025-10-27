package ru.gigafood.backend.dto;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import ru.gigafood.backend.entity.Meal;

public class DishDto {
    public record analyzeRequest(MultipartFile file) {
    }
    public record analyzeResponse(String foodName, Integer weight, Integer caloriesEstimated, Integer proteinEstimated, Integer fatsEstimated, Integer carbsEstimated, Date mealTime, String Uuid, String manualCorrection) {
    }
    public record redactRequest(String foodName, Integer weight, Integer caloriesEstimated, Integer proteinEstimated, Integer fatsEstimated, Integer carbsEstimated, Date mealTime) {
    }
    public record redactResponse(String message) {
    }

    public record getDichResponse(Meal dish) {
    }

}
