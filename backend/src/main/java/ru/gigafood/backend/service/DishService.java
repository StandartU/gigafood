package ru.gigafood.backend.service;

import java.net.MalformedURLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import ru.gigafood.backend.config.TokenService;
import ru.gigafood.backend.dto.DishDto;
import ru.gigafood.backend.entity.Meal;
import ru.gigafood.backend.entity.Photo;
import ru.gigafood.backend.entity.User;
import ru.gigafood.backend.mapper.MealMapper;
import ru.gigafood.backend.repository.MealRepository;
import ru.gigafood.backend.repository.PhotoRepository;

@Service
public class DishService {

    @Autowired
    private AiWebClientService aiWebClientService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private MealMapper mealMapper;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private ReportService reportService;

    public DishDto.analyzeResponse analyze(DishDto.analyzeRequest dtoRequest, HttpServletRequest httpRequest) throws Exception {

        User user = tokenService.getUserByRequest(httpRequest);

        String jsonResponse = aiWebClientService.classifyFood(dtoRequest.file());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree(jsonResponse);

        String foodName = node.path("name").asText();
        Integer weight = node.path("weight_g").isInt() ? node.path("weight_g").asInt() : null;
        Integer caloriesEstimated = node.path("calories").isInt() ? node.path("calories").asInt() : null;
        Integer proteinEstimated = node.path("protein").isInt() ? node.path("fat").asInt() : null;
        Integer fatsEstimated = node.path("fat").isInt() ? node.path("fat").asInt() : null;
        Integer carbsEstimated = node.path("carbs").isInt() ? node.path("carbs").asInt() : null;

        Photo photo = photoService.addAttachment(dtoRequest.file(), user);

        Integer dailyCalorieLimit = user.getUserProfile().getDailyCalorieLimit();

        Integer caloriesConsumedToday = mealRepository.findTodayTotalCalories(user.getId());

        String prompt = String.format("""
        Ты — нутриционист. Пользователь хочет узнать, стоит ли ему есть данное блюдо, 
        учитывая дневной лимит калорий %d ккал.

        Текущий каллории, которые пользователь потратил за день: %d.

        Вот данные о блюде:
        - Название: %s
        - Вес: %s г
        - Калории: %s ккал
        - Белки: %s г
        - Жиры: %s г
        - Углеводы: %s г

        Дай краткий ответ в 2–3 предложениях: оцени блюдо, укажи, насколько оно подходит в рамках лимита, 
        и порекомендуй — стоило ли есть это блюдо сейчас и стоит ли в будующем
        """,
        dailyCalorieLimit != null ? dailyCalorieLimit : 0,
        caloriesConsumedToday != null ? caloriesConsumedToday : 0,
        foodName != null ? foodName : "неизвестно",
        weight != null ? weight : "не указано",
        caloriesEstimated != null ? caloriesEstimated : "не указано",
        proteinEstimated != null ? proteinEstimated : "не указано",
        fatsEstimated != null ? fatsEstimated : "не указано",
        carbsEstimated != null ? carbsEstimated : "не указано"
        );

        jsonResponse = aiWebClientService.generateText(prompt, 200);

        node = objectMapper.readTree(jsonResponse);

        String manualCorrection = node.path("generated_text").asText();
        manualCorrection = (manualCorrection == null || manualCorrection.isBlank()) ? "none" : manualCorrection;

        Meal meal = new Meal();
        meal.setFoodName(foodName);
        meal.setWeight(weight);
        meal.setCaloriesEstimated(caloriesEstimated);
        meal.setProteinEstimated(proteinEstimated);
        meal.setFatsEstimated(fatsEstimated);
        meal.setCarbsEstimated(carbsEstimated);
        meal.setUser(user);
        meal.setManualCorrection(manualCorrection);
        meal.setPhotoUrl(photo.getAttachTitle());
        
        mealRepository.save(meal);

        reportService.updateReport(user);

        return new DishDto.analyzeResponse(       
            meal.getFoodName(),
            meal.getWeight(),
            meal.getCaloriesEstimated(),
            meal.getProteinEstimated(),
            meal.getFatsEstimated(),
            meal.getCarbsEstimated(),
            meal.getMealTime(),
            meal.getUuid(),
            meal.getManualCorrection()
        );
    }

    public List<Meal> all(HttpServletRequest httpRequest) {
        return mealRepository.findAllByUser(tokenService.getUserByRequest(httpRequest));
    }

    public Resource getPhoto(String photoUrl, HttpServletRequest httpRequest) throws MalformedURLException {
        boolean userPhoto = photoRepository.existsByAttachTitleAndUser(photoUrl, tokenService.getUserByRequest(httpRequest));
        if (userPhoto) {
            return photoService.loadFileAsResource(photoUrl);
        } else {
            throw new RuntimeException("Photo not found or you don't have permission to access it");
        }
    }

    public DishDto.redactResponse redact(String uuid, HttpServletRequest httpRequest, DishDto.redactRequest dtoRequest) {
        User user = tokenService.getUserByRequest(httpRequest);

        Meal meal = mealRepository.findByUuidAndUser(uuid, user).orElseThrow(() -> new RuntimeException("Meal not found or dont have premission"));

        mealMapper.updateMealFromDto(dtoRequest, meal);

        mealRepository.save(meal);

        return new DishDto.redactResponse("Redaction complete successfully");
    }

    public DishDto.getDichResponse getDish(String uuid,  HttpServletRequest httpRequest) {
        User user = tokenService.getUserByRequest(httpRequest);
        Meal meal = mealRepository.findByUuidAndUser(uuid, user).orElseThrow(() -> new RuntimeException("Meal not found or dosent have premissions"));
        return new DishDto.getDichResponse(meal);
    }
}
