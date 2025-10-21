package ru.gigafood.backend.dto;

import ru.gigafood.backend.entity.enums.ActivityLevel;
import ru.gigafood.backend.entity.enums.Gender;
import ru.gigafood.backend.entity.enums.GoalType;

public class UserDto {
    public record redactRequest(Gender gender, Integer age, Integer height, Integer weight, ActivityLevel activityLevel, GoalType goalType, Integer dailyCalorieLimit) {};
    public record redactResponse(String message) {};
}
