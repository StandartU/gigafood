package com.example.gigafood.data.model

data class FoodItem(
    val id: Int,
    val imageData: String?,
    val description: String,
    val calories: String,
    val protein: String,
    val fats: String,
    val carbs: String
)