package ru.gigafood.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import ru.gigafood.backend.dto.AuthDto;
import ru.gigafood.backend.service.authService;

@RestController
@RequestMapping(value = "/gigafood/api/v1/", produces = {"application/json"})
public class AuthController {

    @Autowired
    private authService authService;

    @PostMapping("/signup")
	public ResponseEntity<AuthDto.SingupResponse> singup(@RequestBody AuthDto.SingupRequest request) {
        AuthDto.SingupResponse response = authService.signup(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .header(HttpHeaders.LOCATION, "/gigafood/api/v1/login")
            .body(response);
	}

	@PostMapping("/login")
	public AuthDto.LoginResponse login(@RequestBody AuthDto.LoginRequest request) {
        return authService.login(request);
	}

	@GetMapping("/token/refresh")
	public AuthDto.RefreshTokenResponse refreshToken(HttpServletRequest request) {
        return authService.refresh(request);
	}
}
