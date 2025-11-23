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

import com.example.gigafood.ui.components.ScreenHeader
import com.example.gigafood.ui.theme.*
import com.example.gigafood.ui.theme.GigaFoodDimens.ScreenPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("profile_prefs", Context.MODE_PRIVATE) }
    var profileData by remember { mutableStateOf(ProfileData.fromPrefs(prefs)) }

    // Авто-расчёт калорий
    LaunchedEffect(
        profileData.age, profileData.weight, profileData.height,
        profileData.gender, profileData.activity, profileData.goal, profileData.autoCalc
    ) {
        if (profileData.autoCalc && hasValidData(profileData)) {
            profileData = profileData.copy(dailyLimit = calculateCalories(profileData).toString())
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ScreenPadding)
        ) {
            Spacer(modifier = Modifier.height(44.dp))

            // Такой же хедер, как в PhotoPreviewScreen
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
                ProfileDataCard(
                    profileData = profileData,
                    onProfileDataChange = { profileData = it },
                    onSaveClick = {
                        profileData.saveToPrefs(prefs)
                        Toast.makeText(context, "Профиль сохранён", Toast.LENGTH_SHORT).show()
                    },
                    onDeleteClick = {
                        prefs.edit().clear().apply()
                        profileData = ProfileData()
                        Toast.makeText(context, "Аккаунт удалён", Toast.LENGTH_LONG).show()
                    }
                )

                Spacer(modifier = Modifier.height(100.dp)) // запас под прокрутку
            }
        }
    }
}

@Composable
private fun ProfileDataCard(
    profileData: ProfileData,
    onProfileDataChange: (ProfileData) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // ФИО
            ProfileTextField(
                value = profileData.fio,
                onValueChange = { onProfileDataChange(profileData.copy(fio = it)) },
                label = "ФИО",
                leadingIcon = Icons.Default.Person
            )
            Spacer(Modifier.height(16.dp))

            // Пол + Возраст
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ProfileDropdown(
                    value = profileData.gender,
                    onValueSelected = { onProfileDataChange(profileData.copy(gender = it)) },
                    label = "Пол",
                    options = listOf("Мужской", "Женский"),
                    modifier = Modifier.weight(1f)
                )
                ProfileTextField(
                    value = profileData.age,
                    onValueChange = { onProfileDataChange(profileData.copy(age = it.filter { it.isDigit() })) },
                    label = "Возраст",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(16.dp))

            // Рост + Вес
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ProfileTextField(
                    value = profileData.height,
                    onValueChange = { onProfileDataChange(profileData.copy(height = it.filter { it.isDigit() })) },
                    label = "Рост (см)",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
                ProfileTextField(
                    value = profileData.weight,
                    onValueChange = { onProfileDataChange(profileData.copy(weight = it.filter { it.isDigit() })) },
                    label = "Вес (кг)",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(24.dp))

            // Активность и цель
            ProfileDropdown(
                value = profileData.activity,
                onValueSelected = { onProfileDataChange(profileData.copy(activity = it)) },
                label = "Уровень активности",
                options = listOf("Низкий", "Средний", "Высокий", "Очень высокий")
            )
            Spacer(Modifier.height(16.dp))

            ProfileDropdown(
                value = profileData.goal,
                onValueSelected = { onProfileDataChange(profileData.copy(goal = it)) },
                label = "Цель",
                options = listOf("Сброс веса", "Поддержание", "Набор массы")
            )
            Spacer(Modifier.height(28.dp))

            // Авто-расчёт
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text("Авто-расчёт калорий", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "По формуле Миффлина-Сан Жеора",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Switch(
                    checked = profileData.autoCalc,
                    onCheckedChange = { onProfileDataChange(profileData.copy(autoCalc = it)) }
                )
            }

            Spacer(Modifier.height(20.dp))

            ProfileTextField(
                value = profileData.dailyLimit,
                onValueChange = { onProfileDataChange(profileData.copy(dailyLimit = it.filter { it.isDigit() })) },
                label = "Лимит калорий в день",
                keyboardType = KeyboardType.Number,
                readOnly = profileData.autoCalc
            )

            // Детали расчёта
            if (profileData.autoCalc && hasValidData(profileData)) {
                Spacer(Modifier.height(16.dp))
                CalculationDetails(profileData)
            }

            Spacer(Modifier.height(36.dp))

            // Кнопка "Сохранить"
            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BigButtonHeight),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Сохранить изменения", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(12.dp))

            // Удалить аккаунт
            OutlinedButton(
                onClick = onDeleteClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.horizontalGradient(listOf(Color.Red.copy(0.4f), Color.Red))
                )
            ) {
                Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color.Red)
                Spacer(Modifier.width(8.dp))
                Text("Удалить аккаунт", color = Color.Red, fontWeight = FontWeight.Medium)
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