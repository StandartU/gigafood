package com.example.gigafood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GigaFoodTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun GigaFoodTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF4C9AFF),
            onPrimary = Color.White,
            secondary = Color(0xFF00C48C),
            background = Color(0xFFF7FAFF),
            surface = Color.White,
            onSurface = Color(0xFF111827)
        ),
        typography = Typography(),
        content = content
    )
}

private val ScreenPadding = 16.dp
private val CardShape = RoundedCornerShape(12.dp)
private val BigButtonHeight = 52.dp

// Навигация
enum class Screen {
    Login, Register, ResetPassword, NewPassword, Main, Profile, Recommendations, Camera, PhotoPreview, WeeklyReport, FoodEdit
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf(Screen.Login) }
    var capturedImageData by remember { mutableStateOf<String?>(null) }
    var foodItems by remember { mutableStateOf(listOf<FoodItem>()) }
    var selectedFoodItem by remember { mutableStateOf<FoodItem?>(null) }

    when (currentScreen) {
        Screen.Login -> LoginScreen(
            onLoginClick = { currentScreen = Screen.Main },
            onRegisterClick = { currentScreen = Screen.Register },
            onResetPasswordClick = { currentScreen = Screen.ResetPassword }
        )
        Screen.Register -> RegisterScreen(
            onRegisterClick = { currentScreen = Screen.Login },
            onCancelClick = { currentScreen = Screen.Login }
        )
        Screen.ResetPassword -> ResetPasswordScreen(
            onSendClick = { currentScreen = Screen.NewPassword },
            onCancelClick = { currentScreen = Screen.Login }
        )
        Screen.NewPassword -> NewPasswordScreen(
            onSaveClick = { currentScreen = Screen.Login },
            onCancelClick = { currentScreen = Screen.Login }
        )
        Screen.Main -> MainScreen(
            onProfileClick = { currentScreen = Screen.Profile },
            onRecommendationsClick = { currentScreen = Screen.Recommendations },
            onWeeklyReportClick = { currentScreen = Screen.WeeklyReport },
            onCameraClick = { currentScreen = Screen.Camera },
            foodItems = foodItems,
            onFoodItemClick = { foodItem ->
                selectedFoodItem = foodItem
                currentScreen = Screen.FoodEdit
            },
            onFoodAdded = { newFoodItem ->
                foodItems = foodItems + newFoodItem
            }
        )
        Screen.Profile -> ProfileScreen(
            onBackClick = { currentScreen = Screen.Main }
        )
        Screen.Recommendations -> RecommendationsScreen(
            onBackClick = { currentScreen = Screen.Main }
        )
        Screen.Camera -> CameraScreen(
            onBackClick = { currentScreen = Screen.Main },
            onPhotoTaken = { imageData ->
                capturedImageData = imageData
                currentScreen = Screen.PhotoPreview
            }
        )
        Screen.PhotoPreview -> PhotoPreviewScreen(
            imageData = capturedImageData,
            onBackClick = { currentScreen = Screen.Camera },
            onConfirmClick = { calories, protein, fats, carbs, description ->
                val newFoodItem = FoodItem(
                    id = foodItems.size + 1,
                    imageData = capturedImageData,
                    description = description,
                    calories = calories,
                    protein = protein,
                    fats = fats,
                    carbs = carbs
                )
                foodItems = foodItems + newFoodItem
                currentScreen = Screen.Main
            }
        )
        Screen.WeeklyReport -> WeeklyReportScreen(
            onBackClick = { currentScreen = Screen.Main }
        )
        Screen.FoodEdit -> selectedFoodItem?.let { foodItem ->
            FoodEditScreen(
                foodItem = foodItem,
                onSaveClick = { updatedFoodItem ->
                    foodItems = foodItems.map { if (it.id == updatedFoodItem.id) updatedFoodItem else it }
                    currentScreen = Screen.Main
                },
                onBackClick = { currentScreen = Screen.Main }
            )
        }
    }
}

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onResetPasswordClick: () -> Unit
) {
    var mail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ScreenPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "GigaFood",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = CardShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = mail,
                        onValueChange = { mail = it },
                        label = { Text("Почта") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Пароль") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onLoginClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(BigButtonHeight),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Войти")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = onRegisterClick,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Зарегистрироваться")
                    }
                    TextButton(
                        onClick = onResetPasswordClick,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Забыли пароль?")
                    }
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(
    onRegisterClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ScreenPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Регистрация",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = CardShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = login,
                        onValueChange = { login = it },
                        label = { Text("Логин") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Пароль") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = confirm,
                        onValueChange = { confirm = it },
                        label = { Text("Подтверждение пароля") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = onRegisterClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(BigButtonHeight),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Зарегистрироваться")
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = onCancelClick,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Отмена")
                    }
                }
            }
        }
    }
}

@Composable
fun ResetPasswordScreen(
    onSendClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ScreenPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Сброс пароля",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = CardShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Электронная почта") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = onSendClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(BigButtonHeight),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Отправить инструкцию")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = onCancelClick,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Отмена")
                    }
                }
            }
        }
    }
}

@Composable
fun NewPasswordScreen(
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    var pass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ScreenPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Новый пароль",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = CardShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = pass,
                        onValueChange = { pass = it },
                        label = { Text("Новый пароль") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = confirm,
                        onValueChange = { confirm = it },
                        label = { Text("Подтверждение пароля") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = onSaveClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(BigButtonHeight),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Сохранить")
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = onCancelClick,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Отмена")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onProfileClick: () -> Unit,
    onRecommendationsClick: () -> Unit,
    onWeeklyReportClick: () -> Unit,
    onCameraClick: () -> Unit,
    foodItems: List<FoodItem>,
    onFoodItemClick: (FoodItem) -> Unit,
    onFoodAdded: (FoodItem) -> Unit
) {
    var drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showPhotoMenu by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "Меню",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                NavigationDrawerItem(
                    label = { Text("Профиль") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onProfileClick()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Рекомендации") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onRecommendationsClick()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Еженедельный отчет") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onWeeklyReportClick()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(44.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Добро пожаловать",
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 28.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = {
                        scope.launch { drawerState.open() }
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Открыть меню")
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Цель по калориям", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("2000 ккал", style = MaterialTheme.typography.headlineSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Потреблено: 1200 ккал")
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(progress = 0.6f, modifier = Modifier.fillMaxWidth())
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Лента с фотографиями еды
                Text(
                    text = "Еда за день",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                if (foodItems.isEmpty()) {
                    Text(
                        text = "Нет добавленных блюд",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(foodItems) { foodItem ->
                            Card(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clickable { onFoodItemClick(foodItem) },
                                shape = RoundedCornerShape(8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFF2D2D2D))
                                ) {
                                    Text(
                                        text = foodItem.description.take(10) + "...",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .background(Color.Black.copy(alpha = 0.5f))
                                            .padding(4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showPhotoMenu = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Camera")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Сфотографировать еду")
                }

                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Рекомендации", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• Увеличьте потребление белка до 100 г в день.")
                        Text("• Сократите потребление сахара на 20%.")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onRecommendationsClick,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Подробнее")
                        }
                    }
                }
            }
        }
    }

    if (showPhotoMenu) {
        AlertDialog(
            onDismissRequest = { showPhotoMenu = false },
            title = { Text("Добавить еду") },
            text = {
                Column {
                    Button(
                        onClick = {
                            showPhotoMenu = false
                            onCameraClick()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Сфотографировать")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            showPhotoMenu = false
                            onCameraClick()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Загрузить из галереи")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPhotoMenu = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun ProfileScreen(onBackClick: () -> Unit) {
    var fio by remember { mutableStateOf("Иван Иванов") }
    var gender by remember { mutableStateOf("Мужской") }
    var age by remember { mutableStateOf("30") }
    var height by remember { mutableStateOf("180") }
    var weight by remember { mutableStateOf("75") }

    var activityExpanded by remember { mutableStateOf(false) }
    var activity by remember { mutableStateOf("Средний") }

    var goalExpanded by remember { mutableStateOf(false) }
    var goal by remember { mutableStateOf("Поддержание") }

    var autoCalc by remember { mutableStateOf(true) }
    var dailyLimit by remember { mutableStateOf("2000") }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ScreenPadding)
        ) {
            Spacer(modifier = Modifier.height(44.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Профиль",
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Button(
                    onClick = onBackClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Назад", color = MaterialTheme.colorScheme.onSecondary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = CardShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = fio,
                        onValueChange = { fio = it },
                        label = { Text("ФИО") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = gender,
                            onValueChange = { gender = it },
                            label = { Text("Пол") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = age,
                            onValueChange = { age = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Возраст") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = height,
                            onValueChange = { height = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Рост (см)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = weight,
                            onValueChange = { weight = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Вес (кг)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = activity,
                            onValueChange = {},
                            label = { Text("Уровень активности") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { activityExpanded = !activityExpanded }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Активность")
                                }
                            }
                        )
                        DropdownMenu(
                            expanded = activityExpanded,
                            onDismissRequest = { activityExpanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            DropdownMenuItem(onClick = {
                                activity = "Низкий"
                                activityExpanded = false
                            }, text = { Text("Низкий") })
                            DropdownMenuItem(onClick = {
                                activity = "Средний"
                                activityExpanded = false
                            }, text = { Text("Средний") })
                            DropdownMenuItem(onClick = {
                                activity = "Высокий"
                                activityExpanded = false
                            }, text = { Text("Высокий") })
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = goal,
                            onValueChange = {},
                            label = { Text("Цель") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { goalExpanded = !goalExpanded }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Цель")
                                }
                            }
                        )
                        DropdownMenu(
                            expanded = goalExpanded,
                            onDismissRequest = { goalExpanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            DropdownMenuItem(onClick = {
                                goal = "Сброс веса"
                                goalExpanded = false
                            }, text = { Text("Сброс веса") })
                            DropdownMenuItem(onClick = {
                                goal = "Поддержание"
                                goalExpanded = false
                            }, text = { Text("Поддержание") })
                            DropdownMenuItem(onClick = {
                                goal = "Набор массы"
                                goalExpanded = false
                            }, text = { Text("Набор массы") })
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "Авто расчет лимита калорий",
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Switch(
                            checked = autoCalc,
                            onCheckedChange = { autoCalc = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = dailyLimit,
                        onValueChange = { dailyLimit = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Лимит калорий на день") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = autoCalc
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { /* сохранить профиль */ },
                        modifier = Modifier.fillMaxWidth().height(BigButtonHeight),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Сохранить")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = { /* удалить аккаунт */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                    ) {
                        Text("Удалить аккаунт")
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendationsScreen(onBackClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ScreenPadding)
        ) {
            Spacer(modifier = Modifier.height(44.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Рекомендации",
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Button(
                    onClick = onBackClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Назад", color = MaterialTheme.colorScheme.onSecondary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = CardShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("• Увеличьте потребление белка до 100 г в день.", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Сократите потребление сахара на 20%.", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Добавляйте овощи к каждому приему пищи.", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Пейте не менее 2 литров воды в день.", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun WeeklyReportScreen(onBackClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ScreenPadding)
        ) {
            Spacer(modifier = Modifier.height(44.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Еженедельный отчет",
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Button(
                    onClick = onBackClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Назад", color = MaterialTheme.colorScheme.onSecondary)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = CardShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Период: 20.10 - 26.10.2025", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Средняя калорийность", style = MaterialTheme.typography.titleSmall)
                    Text("1850 ккал/день", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Достижение цели", style = MaterialTheme.typography.titleSmall)
                    LinearProgressIndicator(progress = 0.92f, modifier = Modifier.fillMaxWidth())
                    Text("92% дней в пределах нормы", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Макронутриенты", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Белки", style = MaterialTheme.typography.bodyMedium)
                            Text("85 г/день", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                        }
                        Column {
                            Text("Жиры", style = MaterialTheme.typography.bodyMedium)
                            Text("60 г/день", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                        }
                        Column {
                            Text("Углеводы", style = MaterialTheme.typography.bodyMedium)
                            Text("220 г/день", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Рекомендации на следующую неделю:", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Увеличьте потребление белка на 15 г", style = MaterialTheme.typography.bodyMedium)
                    Text("• Сократите сладкое на 10%", style = MaterialTheme.typography.bodyMedium)
                    Text("• Добавьте больше овощей в рацион", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun CameraScreen(
    onBackClick: () -> Unit,
    onPhotoTaken: (String) -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ScreenPadding)
        ) {
            Spacer(modifier = Modifier.height(44.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Камера",
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Button(
                    onClick = onBackClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Назад", color = MaterialTheme.colorScheme.onSecondary)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ScreenPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier
                    .size(320.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF2D2D2D))
                ) {
                    Text(
                        "Предпросмотр камеры",
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(44.dp))

            Button(
                onClick = { onPhotoTaken("captured_photo_data") },
                modifier = Modifier.fillMaxWidth().height(BigButtonHeight),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Сфотографировать и проанализировать")
            }
        }
    }
}

@Composable
fun PhotoPreviewScreen(
    imageData: String?,
    onBackClick: () -> Unit,
    onConfirmClick: (String, String, String, String, String) -> Unit
) {
    var calories by remember { mutableStateOf("450") }
    var protein by remember { mutableStateOf("25") }
    var fats by remember { mutableStateOf("18") }
    var carbs by remember { mutableStateOf("52") }
    var description by remember { mutableStateOf("Куриная грудка с рисом и овощами") }
    var showEditDialog by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ScreenPadding)
        ) {
            Spacer(modifier = Modifier.height(44.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Результат анализа",
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Button(
                    onClick = onBackClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Назад", color = MaterialTheme.colorScheme.onSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF2D2D2D))
                ) {
                    Text(
                        "Фото блюда",
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = CardShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Описание блюда", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(description, style = MaterialTheme.typography.bodyLarge)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Пищевая ценность", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Калории", style = MaterialTheme.typography.bodyMedium)
                            Text("$calories ккал", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                        }
                        Column {
                            Text("Белки", style = MaterialTheme.typography.bodyMedium)
                            Text("$protein г", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Жиры", style = MaterialTheme.typography.bodyMedium)
                            Text("$fats г", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                        }
                        Column {
                            Text("Углеводы", style = MaterialTheme.typography.bodyMedium)
                            Text("$carbs г", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { showEditDialog = true },
                        modifier = Modifier.fillMaxWidth().height(BigButtonHeight),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Изменить данные вручную", color = MaterialTheme.colorScheme.onSecondary)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { onConfirmClick(calories, protein, fats, carbs, description) },
                        modifier = Modifier.fillMaxWidth().height(BigButtonHeight),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Подтвердить и сохранить")
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Редактировать данные") },
            text = {
                Column {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Описание") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = calories,
                        onValueChange = { calories = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Калории (ккал)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = protein,
                        onValueChange = { protein = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Белки (г)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = fats,
                        onValueChange = { fats = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Жиры (г)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = carbs,
                        onValueChange = { carbs = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Углеводы (г)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showEditDialog = false }) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun FoodEditScreen(
    foodItem: FoodItem,
    onSaveClick: (FoodItem) -> Unit,
    onBackClick: () -> Unit
) {
    var description by remember { mutableStateOf(foodItem.description) }
    var calories by remember { mutableStateOf(foodItem.calories) }
    var protein by remember { mutableStateOf(foodItem.protein) }
    var fats by remember { mutableStateOf(foodItem.fats) }
    var carbs by remember { mutableStateOf(foodItem.carbs) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ScreenPadding)
        ) {
            Spacer(modifier = Modifier.height(44.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Выбранное блюдо",
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Button(
                    onClick = onBackClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Назад", color = MaterialTheme.colorScheme.onSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF2D2D2D))
                ) {
                    Text(
                        "Фото блюда",
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = CardShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Описание") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = calories,
                        onValueChange = { calories = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Калории (ккал)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = protein,
                        onValueChange = { protein = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Белки (г)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = fats,
                        onValueChange = { fats = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Жиры (г)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = carbs,
                        onValueChange = { carbs = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Углеводы (г)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val updatedFoodItem = foodItem.copy(
                                description = description,
                                calories = calories,
                                protein = protein,
                                fats = fats,
                                carbs = carbs
                            )
                            onSaveClick(updatedFoodItem)
                        },
                        modifier = Modifier.fillMaxWidth().height(BigButtonHeight),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Сохранить")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview_App() {
    GigaFoodTheme { AppNavigation() }
}

data class FoodItem(
    val id: Int,
    val imageData: String?,
    val description: String,
    val calories: String,
    val protein: String,
    val fats: String,
    val carbs: String
)