package com.example.gigafood.ui.screens.profile

import ProfileData
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gigafood.ui.theme.GigaFoodDimens.BigButtonHeight
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.style.TextAlign
import com.example.gigafood.api.ApiClient
import com.example.gigafood.api.ApiRepository
import com.example.gigafood.api.ApiService
import com.example.gigafood.api.AuthTokens
import com.example.gigafood.api.services.UserService

import com.example.gigafood.ui.components.ScreenHeader
import com.example.gigafood.ui.theme.*
import com.example.gigafood.ui.theme.GigaFoodDimens.ScreenPadding
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var profileData by remember { mutableStateOf(ProfileData()) }

    // Создаем сервис API
    val api = ApiClient.retrofit.create(ApiService::class.java)
    val repo = ApiRepository(api)
    val userService = UserService(repo)



    // Загрузка данных пользователя при старте
    LaunchedEffect(Unit) {
        isLoading = true
        val call = userService.getData(AuthTokens.getAuthHeader())
        call.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        try {
                            val user = parseUserData(body.string())
                            profileData = user
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                isLoading = false
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                isLoading = false
            }
        })
        isLoading = false
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ScreenPadding)
        ) {
            Spacer(modifier = Modifier.height(44.dp))

            ScreenHeader(
                title = "Мой профиль",
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                if (isLoading) {
                    Text(
                        "Загрузка...",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium
                    )
                } else {
                    val scope = rememberCoroutineScope()

                    ProfileDataCard(
                        profileData = profileData,
                        onProfileDataChange = { profileData = it },
                        onSaveClick = {
                            isLoading = true
                            val call = userService.redact(
                                mapProfileDataToDto(profileData),
                                AuthTokens.getAuthHeader()
                            )
                            call.enqueue(object : retrofit2.Callback<ResponseBody> {
                                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                    isLoading = false
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "Профиль сохранён", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Ошибка сохранения", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                    isLoading = false
                                    t.printStackTrace()
                                    Toast.makeText(context, "Ошибка сети", Toast.LENGTH_SHORT).show()
                                }
                            })
                        },
                        onDeleteClick = {
                            profileData = ProfileData()
                            Toast.makeText(context, "Аккаунт удалён", Toast.LENGTH_LONG).show()
                        }
                    )
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

// Преобразование JSON ответа API в ProfileData
private fun parseUserData(jsonString: String): ProfileData {
    // Простой парсинг с использованием org.json
    val json = org.json.JSONObject(jsonString)
    val user = json.getJSONObject("user")
    return ProfileData(
        fio = "", // на сервере имени нет, оставляем пустым
        age = user.optInt("age", 30).toString(),
        gender = when (user.optString("gender", "MALE")) {
            "FEMALE" -> "Женский"
            else -> "Мужской"
        },
        height = user.optInt("height", 180).toString(),
        weight = user.optInt("weight", 75).toString(),
        activity = when (user.optString("activityLevel", "NORMAL")) {
            "SPORT" -> "Высокий"
            "LAZY" -> "Низкий"
            else -> "Средний"
        },
        goal = when (user.optString("goalType", "KEEP_FIT")) {
            "LOSE" -> "Сброс веса"
            "INCREASE_STR" -> "Набор массы"
            else -> "Поддержание"
        },
        dailyLimit = user.optInt("dailyCalorieLimit", 2000).toString(),
        autoCalc = user.optBoolean("autoCalcCalloriesLimit", true)
    )
}

// Преобразование ProfileData в DTO для API
private fun mapProfileDataToDto(profileData: ProfileData): Map<String, Any> {
    val gender = if (profileData.gender == "Женский") "FEMALE" else "MALE"
    val activity = when (profileData.activity) {
        "Низкий" -> "LAZY"
        "Высокий" -> "SPORT"
        else -> "NORMAL"
    }
    val goal = when (profileData.goal) {
        "Сброс веса" -> "LOSE"
        "Набор массы" -> "INCREASE_STR"
        else -> "KEEP_FIT"
    }
    return mapOf(
        "gender" to gender,
        "age" to (profileData.age.toIntOrNull() ?: 0),
        "height" to (profileData.height.toIntOrNull() ?: 0),
        "weight" to (profileData.weight.toIntOrNull() ?: 0),
        "activityLevel" to activity,
        "goalType" to goal,
        "dailyCalorieLimit" to (profileData.dailyLimit.toIntOrNull() ?: 0),
        "autoCalcCalloriesLimit" to profileData.autoCalc
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDataCard(
    profileData: ProfileData,
    onProfileDataChange: (ProfileData) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ProfileTextField(
                    value = profileData.age,
                    onValueChange = { onProfileDataChange(profileData.copy(age = it)) },
                    label = "Возраст",
                    keyboardType = KeyboardType.Number
                )
                Spacer(modifier = Modifier.height(8.dp))
                ProfileTextField(
                    value = profileData.height,
                    onValueChange = { onProfileDataChange(profileData.copy(height = it)) },
                    label = "Рост (см)",
                    keyboardType = KeyboardType.Number
                )
                Spacer(modifier = Modifier.height(8.dp))
                ProfileTextField(
                    value = profileData.weight,
                    onValueChange = { onProfileDataChange(profileData.copy(weight = it)) },
                    label = "Вес (кг)",
                    keyboardType = KeyboardType.Number
                )
                Spacer(modifier = Modifier.height(8.dp))
                ProfileDropdown(
                    value = profileData.gender,
                    onValueSelected = { onProfileDataChange(profileData.copy(gender = it)) },
                    label = "Пол",
                    options = listOf("Мужской", "Женский")
                )
                Spacer(modifier = Modifier.height(8.dp))
                ProfileDropdown(
                    value = profileData.activity,
                    onValueSelected = { onProfileDataChange(profileData.copy(activity = it)) },
                    label = "Активность",
                    options = listOf("Низкий", "Средний", "Высокий")
                )
                Spacer(modifier = Modifier.height(8.dp))
                ProfileDropdown(
                    value = profileData.goal,
                    onValueSelected = { onProfileDataChange(profileData.copy(goal = it)) },
                    label = "Цель",
                    options = listOf("Сброс веса", "Поддержание", "Набор массы")
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Авторасчет калорий
                if (profileData.autoCalc && hasValidData(profileData)) {
                    CalculationDetails(profileData = profileData)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(
                        onClick = onSaveClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Сохранить")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = onDeleteClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Удалить")
                    }
                }
            }
        }
    }
}

// Функция расчета калорий по формуле Миффлина-Сан Жеора
private fun calculateCalories(profileData: ProfileData): Int {
    val age = profileData.age.toIntOrNull() ?: 30
    val weight = profileData.weight.toDoubleOrNull() ?: 75.0
    val height = profileData.height.toIntOrNull() ?: 180
    val gender = profileData.gender
    val activity = profileData.activity
    val goal = profileData.goal

    // Расчет BMR (Basal Metabolic Rate)
    val bmr = if (gender == "Мужской") {
        10 * weight + 6.25 * height - 5 * age + 5
    } else {
        10 * weight + 6.25 * height - 5 * age - 161
    }

    // Коэффициенты активности
    val activityMultipliers = mapOf(
        "Низкий" to 1.2,
        "Средний" to 1.55,
        "Высокий" to 1.9,
    )

    // Расчет TDEE (Total Daily Energy Expenditure)
    val activityMultiplier = activityMultipliers[activity] ?: 1.55
    val tdee = bmr * activityMultiplier

    // Корректировка по цели
    val goalMultipliers = mapOf(
        "Сброс веса" to 0.85,
        "Поддержание" to 1.0,
        "Набор массы" to 1.15
    )

    var calories = tdee * (goalMultipliers[goal] ?: 1.0)

    // Дополнительная корректировка для экстремальных целей
    calories = when (goal) {
        "Сброс веса" -> {
            val minCalories = if (gender == "Мужской") 1500.0 else 1200.0
            max(calories, minCalories)
        }
        "Набор массы" -> {
            val maxCalories = tdee + 500
            min(calories, maxCalories)
        }
        else -> calories
    }

    return calories.roundToInt()
}

// Проверка валидности данных для расчета
private fun hasValidData(profileData: ProfileData): Boolean {
    return profileData.age.isNotEmpty() &&
            profileData.weight.isNotEmpty() &&
            profileData.height.isNotEmpty() &&
            profileData.gender.isNotEmpty() &&
            profileData.activity.isNotEmpty() &&
            profileData.goal.isNotEmpty()
}

// Компонент для отображения деталей расчета
@Composable
private fun CalculationDetails(profileData: ProfileData) {
    val age = profileData.age.toIntOrNull() ?: 30
    val weight = profileData.weight.toDoubleOrNull() ?: 75.0
    val height = profileData.height.toIntOrNull() ?: 180
    val gender = profileData.gender
    val activity = profileData.activity
    val goal = profileData.goal

    // Расчет BMR
    val bmr = if (gender == "Мужской") {
        10 * weight + 6.25 * height - 5 * age + 5
    } else {
        10 * weight + 6.25 * height - 5 * age - 161
    }

    // Коэффициенты активности
    val activityMultipliers = mapOf(
        "Низкий" to 1.2,
        "Средний" to 1.55,
        "Высокий" to 1.9,
    )

    val activityMultiplier = activityMultipliers[activity] ?: 1.55
    val tdee = bmr * activityMultiplier
    val finalCalories = calculateCalories(profileData)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Детали расчета:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "• BMR (основной обмен): ${bmr.roundToInt()} ккал",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Text(
                text = "• TDEE (с учетом активности): ${tdee.roundToInt()} ккал",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Text(
                text = "• Корректировка по цели: $goal",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Text(
                text = "• Итоговый лимит: $finalCalories ккал",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    readOnly: Boolean = false,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = leadingIcon?.let { { Icon(it, null, tint = MaterialTheme.colorScheme.primary) } },
        readOnly = readOnly,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileDropdown(
    value: String,
    onValueSelected: (String) -> Unit,
    label: String,
    options: List<String>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}