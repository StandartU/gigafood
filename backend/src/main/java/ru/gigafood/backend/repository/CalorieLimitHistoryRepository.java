package ru.gigafood.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.gigafood.backend.entity.CalorieLimitHistory;

@Repository
public interface CalorieLimitHistoryRepository extends JpaRepository<CalorieLimitHistory, Long> {
    
}
