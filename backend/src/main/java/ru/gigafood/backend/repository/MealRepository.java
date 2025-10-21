package ru.gigafood.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.gigafood.backend.entity.Meal;
import ru.gigafood.backend.entity.User;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    Optional<Meal> findByUuid(String uuid);

    boolean existsByUuidAndUser(String uuid, User user);
}