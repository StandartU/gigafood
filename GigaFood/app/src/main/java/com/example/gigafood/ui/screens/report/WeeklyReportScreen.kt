package com.example.gigafood.ui.screens.report

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gigafood.api.ApiClient
import com.example.gigafood.api.ApiRepository
import com.example.gigafood.api.ApiService
import com.example.gigafood.api.AuthTokens
import com.example.gigafood.api.services.ReportService
import com.example.gigafood.api.services.UserService
import com.example.gigafood.ui.components.ScreenHeader
import com.example.gigafood.ui.theme.GigaFoodDimens.ScreenPadding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.get

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyReportScreen(onBackClick: () -> Unit) {

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ScreenPadding)
        ) {
            Spacer(modifier = Modifier.height(44.dp))

            // Единый хедер — как в Recommendations, PhotoPreview и Profile
            ScreenHeader(
                title = "Недельный отчет",
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                // Красивый фон с градиентом
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Главная карточка отчёта
                    ReportSummaryCard()

                    Spacer(modifier = Modifier.height(24.dp))

                    // Карточка с макронутриентами
                    MacronutrientsCard()

                    Spacer(modifier = Modifier.height(100.dp)) // запас под прокрутку
                }
            }
        }
    }
}

@Composable
fun CurrentWeekText() {
    val today = LocalDate.now()
    val monday = today.with(DayOfWeek.MONDAY)
    val sunday = today.with(DayOfWeek.SUNDAY)

    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy") // Например: 25 ноября 2025
    val weekText = "${monday.format(formatter)} — ${sunday.format(formatter)}"

    Text(
        weekText,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    )
}

@Composable
private fun ReportSummaryCard() {

    val api = ApiClient.retrofit.create(ApiService::class.java)
    val repo = ApiRepository(api)
    val reportService = ReportService(repo)
    val userService = UserService(repo)

    var calories by remember { mutableStateOf("Load... ккал") }

    var percentage by remember { mutableStateOf(0.00f) }

    var percentageText by remember { mutableStateOf("Load % выполнения лимита в неделю") }

    var dailyCalories by remember { mutableStateOf(0) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(20.dp, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(modifier = Modifier.padding(28.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.BarChart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    "Отчет за неделю",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(8.dp))
            CurrentWeekText()

            Spacer(Modifier.height(32.dp))

            var call = reportService.week(AuthTokens.getAuthHeader())
            call.enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val jsonStr = response.body()?.string()
                        if (!jsonStr.isNullOrEmpty()) {
                            val gson = com.google.gson.Gson()
                            val map = gson.fromJson(jsonStr, Map::class.java)
                            val report = map["dayCalories"] as? Map<*, *>

                            val avgCalories: Int = report
                                ?.values
                                ?.filterIsInstance<Number>()?.sumOf { it.toInt() }
                                ?.div(report.size.takeIf { it > 0 } ?: 1)
                                ?: 0

                            calories = "$avgCalories ккал"

                            Log.d("DEBUG", avgCalories.toString())

                            Log.d("DEBUG", dailyCalories.toString())

                            percentage = avgCalories.toFloat() / dailyCalories.toFloat()

                            Log.d("DEBUG", percentage.toString())

                            percentageText = "${(percentage * 100).toInt()} % выполнения лимита в неделю"
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                }
            })

            call = userService.getData(AuthTokens.getAuthHeader())
            call.enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        response.body()?.let { body ->
                            try {
                                val jsonStr = response.body()?.string()
                                if (!jsonStr.isNullOrEmpty()) {
                                    val gson = com.google.gson.Gson()
                                    val map = gson.fromJson(jsonStr, Map::class.java)
                                    val report = map["user"] as? Map<*, *>
                                    if (report != null) {
                                        val limit = report["dailyCalorieLimit"] as? Number
                                        if (limit != null) {
                                            Log.d("DEBUD", limit.toString())
                                            dailyCalories = limit.toInt()
                                            Log.d("DEBUD", dailyCalories.toString())
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                }
            })



            // Средняя калорийность
            StatRow(
                icon = Icons.Default.Whatshot,
                label = "Средняя калорийность",
                value = calories,
                subtitle = "в день"
            )

            Spacer(Modifier.height(24.dp))

            // Достижение цели
            Column {
                Text("Достижение цели", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { percentage },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    percentageText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun MacronutrientsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(modifier = Modifier.padding(28.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.PieChart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    "Макронутриенты",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MacroItem(
                    label = "Белки",
                    value = "-",
                    color = Color(0xFF4CAF50),
                    icon = Icons.Default.FitnessCenter
                )
                MacroItem(
                    label = "Жиры",
                    value = "-",
                    color = Color(0xFFFF9800),
                    icon = Icons.Default.Grass
                )
                MacroItem(
                    label = "Углеводы",
                    value = "-",
                    color = Color(0xFF2196F3),
                    icon = Icons.Default.LocalPizza
                )
            }
        }
    }
}

@Composable
private fun StatRow(icon: ImageVector, label: String, value: String, subtitle: String? = null) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            subtitle?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
private fun MacroItem(label: String, value: String, color: Color, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(color.copy(alpha = 0.15f), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(36.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
    }
}