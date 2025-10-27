package ru.gigafood.backend.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import ru.gigafood.backend.config.TokenService;
import ru.gigafood.backend.dto.UserDto;
import ru.gigafood.backend.entity.CalorieLimitHistory;
import ru.gigafood.backend.entity.UserProfile;
import ru.gigafood.backend.entity.enums.ActivityLevel;
import ru.gigafood.backend.entity.enums.Gender;
import ru.gigafood.backend.repository.CalorieLimitHistoryRepository;
import ru.gigafood.backend.repository.UserProfileRepository;

@Service
public class UserService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private CalorieLimitHistoryRepository calorieLimitHistoryRepository;

    public UserDto.redactResponse redact(UserDto.redactRequest dtoRequest, HttpServletRequest httpRequest) {
        UserProfile user = tokenService.getUserByRequest(httpRequest).getUserProfile();

        Optional.ofNullable(dtoRequest.gender()).ifPresent(user::setGender);
        Optional.ofNullable(dtoRequest.age()).ifPresent(user::setAge);
        Optional.ofNullable(dtoRequest.height()).ifPresent(user::setHeight);
        Optional.ofNullable(dtoRequest.weight()).ifPresent(user::setWeight);
        Optional.ofNullable(dtoRequest.activityLevel()).ifPresent(user::setActivityLevel);
        Optional.ofNullable(dtoRequest.goalType()).ifPresent(user::setGoalType);
        Optional.ofNullable(dtoRequest.autoCalcCalloriesLimit()).ifPresent(user::setAutoCalcCalloriesLimit);

        if (user.getAutoCalcCalloriesLimit()) {
            Map<Gender, Integer> additionalIndexes = Map.of(
                Gender.FEMALE, -161,
                Gender.MALE, 5
            );
            Map<ActivityLevel, Double> coefficient = Map.of(
                ActivityLevel.LAZY, 1.2,
                ActivityLevel.NORMAL, 1.55,
                ActivityLevel.SPORT, 1.8
            );

            double tdee = (10 * user.getWeight() + 6.25 * user.getHeight() - 5 * user.getAge() + additionalIndexes.get(user.getGender())) * coefficient.get(user.getActivityLevel());

            user.setDailyCalorieLimit((int) tdee);
            CalorieLimitHistory calorieLimitHistory = new CalorieLimitHistory();
            calorieLimitHistory.setUser(user.getUser());
            calorieLimitHistory.setDailyLimit(user.getDailyCalorieLimit());
            calorieLimitHistoryRepository.save(calorieLimitHistory);
        } else {
            Optional.ofNullable(dtoRequest.dailyCalorieLimit()).ifPresent(user::setDailyCalorieLimit);
        }

        if (Optional.ofNullable(dtoRequest.dailyCalorieLimit()).isPresent()) {
            CalorieLimitHistory calorieLimitHistory = new CalorieLimitHistory();
            calorieLimitHistory.setUser(user.getUser());
            calorieLimitHistory.setDailyLimit(dtoRequest.dailyCalorieLimit());
            calorieLimitHistoryRepository.save(calorieLimitHistory);
        }

        userProfileRepository.save(user);

        return new UserDto.redactResponse("Пользователь изменён");
    }

    public UserDto.getUserDataResponce getUserData(HttpServletRequest httpRequest) {
        return new UserDto.getUserDataResponce(tokenService.getUserByRequest(httpRequest).getUserProfile());
    }
}
