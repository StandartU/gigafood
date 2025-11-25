package com.example.gigafood.api.services

import com.example.gigafood.api.ApiRepository

class AuthService(private val repo: ApiRepository) {

    fun signup(data: Any, headers: Map<String, String> = emptyMap()) =
        repo.apiRequest("auth/signup", "POST", body = data, headers = headers)

    fun login(data: Any, headers: Map<String, String> = emptyMap()) =
        repo.apiRequest("auth/login", "POST", body = data, headers = headers)

    fun refresh(headers: Map<String, String>) =
        repo.apiRequest("auth/token/refresh", "GET", headers = headers)
}