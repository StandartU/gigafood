package ru.gigafood.backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import ru.gigafood.backend.config.TokenService;
import ru.gigafood.backend.dto.UserDto;
import ru.gigafood.backend.entity.UserProfile;
import ru.gigafood.backend.repository.UserProfileRepository;

@Service
public class UserService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserProfileRepository userProfileRepository;

    public UserDto.redactResponse redact(UserDto.redactRequest dtoRequest, HttpServletRequest httpRequest) {
        String headerAuth = httpRequest.getHeader("Authorization");		 
		String accessToken = headerAuth.substring(7, headerAuth.length());
		
		String email = tokenService.parseToken(accessToken);
		UserProfile user = userProfileRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Optional.ofNullable(dtoRequest.gender()).ifPresent(user::setGender);
        Optional.ofNullable(dtoRequest.age()).ifPresent(user::setAge);
        Optional.ofNullable(dtoRequest.height()).ifPresent(user::setHeight);
        Optional.ofNullable(dtoRequest.weight()).ifPresent(user::setWeight);
        Optional.ofNullable(dtoRequest.activityLevel()).ifPresent(user::setActivityLevel);
        Optional.ofNullable(dtoRequest.goalType()).ifPresent(user::setGoalType);
        Optional.ofNullable(dtoRequest.dailyCalorieLimit()).ifPresent(user::setDailyCalorieLimit);

        return new UserDto.redactResponse("Пользователь изменён");

    }
}
