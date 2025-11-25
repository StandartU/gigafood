package com.example.gigafood.api.services

import com.example.gigafood.api.ApiRepository

class UserService(private val repo: ApiRepository) {

    fun getData(headers: Map<String, String>) =
        repo.apiRequest("user/get", "GET", headers = headers)

    fun redact(data: Any, headers: Map<String, String>) =
        repo.apiRequest("user/redact", "POST", body = data, headers = headers)
}