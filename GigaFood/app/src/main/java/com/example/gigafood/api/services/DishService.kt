package com.example.gigafood.api.services

import com.example.gigafood.api.ApiRepository
import java.io.File

class DishService(private val repo: ApiRepository) {

    fun analyze(file: File, headers: Map<String, String>) =
        repo.apiRequest("dish/analyze", "POST", file = file, headers = headers)

    fun getDish(uuid: String, headers: Map<String, String>) =
        repo.apiRequest("dish/get/$uuid", "POST", headers = headers)

    fun redactDish(uuid: String, data: Any, headers: Map<String, String>) =
        repo.apiRequest("dish/redact/$uuid", "POST", body = data, headers = headers)

    fun getAll(headers: Map<String, String>) =
        repo.apiRequest("dish/all", "POST", headers = headers)

    fun getPhoto(photoUrl: String, headers: Map<String, String>) =
        repo.apiFileRequest("dish/get_photo/$photoUrl", headers)
}