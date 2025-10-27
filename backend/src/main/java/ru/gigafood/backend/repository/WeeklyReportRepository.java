package ru.gigafood.backend.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.gigafood.backend.entity.User;
import ru.gigafood.backend.entity.WeeklyReport;

@Repository
public interface WeeklyReportRepository extends JpaRepository<WeeklyReport, Long> {

    @Query("""
        SELECT w
        FROM WeeklyReport w
        WHERE w.user = :user
          AND w.weeklyStartDate = :start
          AND w.weeklyEndDate = :end
    """)
    Optional<WeeklyReport> findByUserAndWeekRange(
        @Param("user") User user,
        @Param("start") Date start,
        @Param("end") Date end
    );
}
