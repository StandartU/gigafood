package com.example.gigafood.ui.screens.recommendations

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gigafood.api.ApiClient
import com.example.gigafood.api.ApiRepository
import com.example.gigafood.api.ApiService
import com.example.gigafood.api.AuthTokens
import com.example.gigafood.api.services.ReportService
import com.example.gigafood.ui.components.ScreenHeader
import com.example.gigafood.ui.theme.*
import com.example.gigafood.ui.theme.GigaFoodDimens.ScreenPadding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationsScreen(onBackClick: () -> Unit) {

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ScreenPadding)
        ) {
            Spacer(modifier = Modifier.height(44.dp),)
            
            ScreenHeader(
                title = "Рекомендации",

                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                RecommendationsCard()

                Spacer(modifier = Modifier.height(100.dp)) // запас под прокрутку
            }
        }
    }
}

@Composable
private fun RecommendationsCard() {
    val api = ApiClient.retrofit.create(ApiService::class.java)
    val repo = ApiRepository(api)
    val reportService = ReportService(repo)

    // --- состояние для загрузки рекомендаций с API ---
    var apiRecommendation by remember { mutableStateOf<String?>(null) }

    // --- загрузка один раз при появлении карточки ---
    LaunchedEffect(Unit) {
        try {
            // используем токен из AuthTokens
            val call = reportService.week(AuthTokens.getAuthHeader())
            call.enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val jsonStr = response.body()?.string()
                        if (!jsonStr.isNullOrEmpty()) {
                            val gson = com.google.gson.Gson()
                            val map = gson.fromJson(jsonStr, Map::class.java)
                            val report = map["report"] as? Map<*, *>
                            val rec = report?.get("recomendations") as? String
                            if (!rec.isNullOrEmpty()) {
                                apiRecommendation = rec
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // --- твой существующий дефолтный список ---
    val defaultRecommendations = listOf(
        "Загрузка рекомендаций..."
    )


    val recommendations = if (apiRecommendation != null) listOf(apiRecommendation!!) else defaultRecommendations

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.TipsAndUpdates,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Персональные рекомендации",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(24.dp))

            recommendations.forEachIndexed { index, recommendation ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = index * 150)) +
                            slideInVertically(animationSpec = tween(600, delayMillis = index * 150))
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            modifier = Modifier
                                .size(24.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = recommendation,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}