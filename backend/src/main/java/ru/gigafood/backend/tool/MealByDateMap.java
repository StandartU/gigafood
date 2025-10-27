package ru.gigafood.backend.tool;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MealByDateMap {
    public Map<LocalDate, Integer> getMapOfMeals(List<Object[]> rows) {
        Map<LocalDate, Integer> caloriesByDay = new LinkedHashMap<>();

        for (Object[] row : rows) {
            LocalDate day = ((Date) row[0]).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Integer total = ((Number) row[1]).intValue();
            caloriesByDay.put(day, total);
        }

        return caloriesByDay;
    }
}
