package ru.gigafood.backend.dto;

public class AuthDto {
    public record LoginRequest(String username, String password) {};
    public record SingupRequest(String username, String password) {};
    public record LoginResponse(String message, String access_jwt_token, String refresh_jwt_token) {};
    public record RefreshTokenResponse(String access_jwt_token, String refresh_jwt_token) {};
    public record SingupResponse(String message) {};
}
