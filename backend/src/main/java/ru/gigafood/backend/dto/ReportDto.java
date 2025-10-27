package ru.gigafood.backend.dto;

import java.time.LocalDate;
import java.util.Map;

import ru.gigafood.backend.entity.WeeklyReport;

public class ReportDto {
    public record getWeekReportResponce(WeeklyReport report, Map<LocalDate, Integer> dayCalories) {}

    public record getDailyReportResponce(Integer callories) {}
}
