package ru.gigafood.backend.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import ru.gigafood.backend.config.TokenService;
import ru.gigafood.backend.dto.ReportDto;
import ru.gigafood.backend.entity.Meal;
import ru.gigafood.backend.entity.User;
import ru.gigafood.backend.entity.WeeklyReport;
import ru.gigafood.backend.repository.MealRepository;
import ru.gigafood.backend.repository.WeeklyReportRepository;
import ru.gigafood.backend.tool.DateTools;
import ru.gigafood.backend.tool.MealByDateMap;

@Service
public class ReportService {
    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AiWebClientService aiWebClientService;

    @Autowired
    private WeeklyReportRepository weeklyReportRepository;

    private MealByDateMap mapTool; 

    private DateTools dateTools;

    public ReportDto.getWeekReportResponce weekReport(HttpServletRequest httpRequest) throws JsonMappingException, JsonProcessingException {
        User user = tokenService.getUserByRequest(httpRequest);

        Map<LocalDate, Integer> dateCaloriesMap = mapTool.getMapOfMeals(mealRepository.findWeeklyCaloriesByUser(user.getId()));

        WeeklyReport nowReport = updateReport(user);

        return new ReportDto.getWeekReportResponce(nowReport, dateCaloriesMap);
    }

    public ReportDto.getDailyReportResponce dayReport(HttpServletRequest httpRequest) {
        return new ReportDto.getDailyReportResponce(mealRepository.findTodayTotalCalories(tokenService.getUserByRequest(httpRequest).getId()));
    }

    public WeeklyReport updateReport(User user) throws JsonMappingException, JsonProcessingException {
        Integer totalWeekCalories = mealRepository.findWeekTotalCalories(user.getId());

        ObjectMapper mapper = new ObjectMapper();

        List<Meal> foodList = mealRepository.findMealsForCurrentWeek(user.getId());

        String foodListJson = mapper
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(foodList);

        String prompt = """
        Ты — нутрициолог. Я передаю тебе данные за день о питании.
        Лимит калорий на день: %d
        Список съеденных блюд (в JSON-формате):
        %s

        Проанализируй:
        1. Общие итоги калорий и БЖУ.
        2. Оцени, соблюдается ли режим.
        3. Дай рекомендации: что стоит сократить, что добавить.
        """.formatted(user.getUserProfile().getDailyCalorieLimit(), foodListJson);

        String jsonResponse = aiWebClientService.generateText(prompt, 200);

        JsonNode node = mapper.readTree(jsonResponse);

        String rawRecommendation = node.path("generated_text").asText();
        String recommendation = (rawRecommendation == null || rawRecommendation.isBlank()) ? "none" : rawRecommendation;

        Optional<WeeklyReport> weeklyReportOpt = weeklyReportRepository.findByUserAndWeekRange(
            user,
            dateTools.getStartOfWeek(),
            dateTools.getEndOfWeek()
        );

        WeeklyReport nowReport = weeklyReportOpt
            .map(report -> {
                report.setRecomendations(recommendation);
                return weeklyReportRepository.save(report);
            })
            .orElseGet(() -> {
                WeeklyReport weeklyReport = new WeeklyReport();
                weeklyReport.setTotalCalories(totalWeekCalories);
                weeklyReport.setUser(user);
                weeklyReport.setWeeklyStartDate(dateTools.getStartOfWeek());
                weeklyReport.setWeeklyEndDate(dateTools.getEndOfWeek());
                weeklyReport.setRecomendations(recommendation);
                return weeklyReportRepository.save(weeklyReport);
            });
        return nowReport;
    }
}
