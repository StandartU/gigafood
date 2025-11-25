package com.example.gigafood.api

import android.util.Base64
import com.example.gigafood.api.services.DishService
import com.example.gigafood.api.services.UserService
import com.example.gigafood.data.model.FoodItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

object AuthTokens {
    var accessToken: String? = null
    var refreshToken: String? = null

    fun getAuthHeader(): Map<String, String> {
        return if (!accessToken.isNullOrEmpty()) {
            mapOf("Authorization" to "Bearer $accessToken")
        } else emptyMap()
    }
}

object ApiHelper {

    suspend fun getMealsWithImages(dishService: DishService): List<FoodItem> {
        return withContext(Dispatchers.IO) {
            val headers = AuthTokens.getAuthHeader()
            val response = dishService.getAll(headers).execute()
            if (!response.isSuccessful || response.body() == null) return@withContext emptyList<FoodItem>()

            val bodyString = response.body()!!.string()
            val jsonArray = JSONArray(bodyString)
            val foodItems = mutableListOf<FoodItem>()

            for (i in 0 until jsonArray.length()) {
                val json = jsonArray.getJSONObject(i)
                val description = json.optString("foodName", "Блюдо")
                val calories = json.optInt("caloriesEstimated", 0)
                val protein = json.optInt("proteinEstimated", 0)
                val fats = json.optInt("fatsEstimated", 0)
                val carbs = json.optInt("carbsEstimated", 0)
                val photoUrl = json.optString("photoUrl", "")

                // Получаем картинку по photoUrl
                val imageData = if (photoUrl.isNotEmpty()) {
                    val photoResp = dishService.getPhoto(photoUrl, headers).execute()
                    if (photoResp.isSuccessful && photoResp.body() != null) {
                        val bytes = photoResp.body()!!.bytes()
                        Base64.encodeToString(bytes, Base64.DEFAULT)
                    } else null
                } else null

                foodItems.add(
                    FoodItem(
                        id = i,
                        description = description,
                        calories = calories.toString(),
                        protein = protein.toString(),
                        fats = fats.toString(),
                        carbs = carbs.toString(),
                        imageData = imageData
                    )
                )
            }
            foodItems
        }
    }

    suspend fun getUserCalorieGoal(userService: UserService): Int {
        return withContext(Dispatchers.IO) {
            val headers = AuthTokens.getAuthHeader()
            val response = userService.getData(headers).execute()
            if (!response.isSuccessful || response.body() == null) return@withContext 2000

            val bodyString = response.body()!!.string()
            val json = JSONObject(bodyString)
            json.optInt("dailyCalorieLimit", 2000)
        }
    }
}