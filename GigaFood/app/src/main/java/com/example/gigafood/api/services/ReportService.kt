package com.example.gigafood.api.services

import com.example.gigafood.api.ApiRepository

class ReportService(private val repo: ApiRepository) {

    fun day(headers: Map<String, String>) =
        repo.apiRequest("report/day", "GET", headers = headers)

    fun week(headers: Map<String, String>) =
        repo.apiRequest("report/week", "GET", headers = headers)
}