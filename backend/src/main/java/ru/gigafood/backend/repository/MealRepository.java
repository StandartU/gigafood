package ru.gigafood.backend.repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.gigafood.backend.entity.Meal;
import ru.gigafood.backend.entity.User;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    Optional<Meal> findByUuid(String uuid);

    Optional<Meal> findByUuidAndUser(String uuid, User user);

    List<Meal> findAllByUser(User user);

    @Query(value = """
        SELECT COALESCE(SUM(m.calories_etimated), 0)
        FROM meals m
        WHERE m.user_id = :userId
        AND DATE_TRUNC('day', m.meal_time) = DATE_TRUNC('day', CURRENT_DATE)
    """, nativeQuery = true)
    Integer findTodayTotalCalories(@Param("userId") Long userId);

    @Query(value = """
        SELECT COALESCE(SUM(m.calories_etimated), 0)
        FROM meals m
        WHERE m.user_id = :userId
        AND m.meal_time >= DATE_TRUNC('week', CURRENT_DATE)
        AND m.meal_time < DATE_TRUNC('week', CURRENT_DATE) + INTERVAL '7 day'
    """, nativeQuery = true)
    Integer findWeekTotalCalories(@Param("userId") Long userId);

    @Query("SELECT SUM(m.caloriesEstimated) FROM Meal m WHERE m.user = :user AND m.mealTime BETWEEN :start AND :end")
    Integer findCaloriesBetween(@Param("user") User user, @Param("start") Date start, @Param("end") Date end);

    List<Meal> findAllByUserAndMealTimeBetween(User user, Date start, Date end);

    @Query("""
    SELECT COALESCE(SUM(m.caloriesEstimated), 0)
    FROM Meal m
    WHERE m.user = :user
    AND FUNCTION('DATE', m.mealTime) = FUNCTION('DATE', :targetDate)
    """)
    Integer findTotalCaloriesByUserAndDate(@Param("user") User user, @Param("targetDate") Date targetDate);

    @Query(value = """
    SELECT DATE_TRUNC('day', m.meal_time) AS day,
        COALESCE(SUM(m.calories_etimated), 0) AS total_calories
    FROM meals m
    WHERE m.user_id = :userId
    AND m.meal_time >= DATE_TRUNC('week', CURRENT_DATE)
    AND m.meal_time < DATE_TRUNC('week', CURRENT_DATE) + INTERVAL '7 day'
    GROUP BY day
    ORDER BY day
    """, nativeQuery = true)
    List<Object[]> findWeeklyCaloriesByUser(@Param("userId") Long userId);

    @Query(value = """
        SELECT m
        FROM Meal m
        WHERE m.user.id = :userId
        AND m.mealTime >= FUNCTION('DATE_TRUNC', 'week', CURRENT_DATE)
        AND m.mealTime < FUNCTION('DATE_TRUNC', 'week', CURRENT_DATE) + 7
        ORDER BY m.mealTime
    """, nativeQuery = true)
    List<Meal> findMealsForCurrentWeek(@Param("userId") Long userId);
}