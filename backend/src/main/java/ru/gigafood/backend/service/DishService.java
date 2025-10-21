package ru.gigafood.backend.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import ru.gigafood.backend.dto.DishDto;
import ru.gigafood.backend.repository.MealRepository;

@Service
public class DishService {

    @Autowired
    private AiWebClientService aiWebClientService;

    @Autowired
    private PhotoService photoService;

    @Autowired MealRepository mealRepository;

    public DishDto.analyzeResponse analyze(DishDto.analyzeRequest dtoRequest, HttpServletRequest httpRequest) throws Exception {

		String headerAuth = httpRequest.getHeader("Authorization");		 
		String accessToken = headerAuth.substring(7, headerAuth.length());

        String jsonResponse = aiWebClientService.classifyFood(dtoRequest.file());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree(jsonResponse);

        String foodName = node.path("name").asText();
        Integer weight = node.path("weight_g").isInt() ? node.path("weight_g").asInt() : null;
        Integer caloriesEstimated = node.path("calories").isInt() ? node.path("calories").asInt() : null;
        Integer proteinEstimated = node.path("protein").isInt() ? node.path("fat").asInt() : null;
        Integer fatsEstimated = node.path("fat").isInt() ? node.path("fat").asInt() : null;
        Integer carbsEstimated = node.path("carbs").isInt() ? node.path("carbs").asInt() : null;
        Date mealTime = new Date();

        photoService.addAttachment(dtoRequest.file());





        return new DishDto.analyzeResponse(
                foodName,
                weight,
                caloriesEstimated,
                proteinEstimated,
                fatsEstimated,
                carbsEstimated,
                mealTime
        );
    }
}
