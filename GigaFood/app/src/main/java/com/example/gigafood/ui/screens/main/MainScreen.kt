package com.example.gigafood.ui.screens.main

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gigafood.api.ApiClient
import com.example.gigafood.api.ApiRepository
import com.example.gigafood.api.ApiService
import com.example.gigafood.api.AuthTokens
import com.example.gigafood.data.model.FoodItem
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import com.example.gigafood.api.services.AuthService
import com.example.gigafood.api.services.DishService
import com.example.gigafood.api.services.ReportService
import com.example.gigafood.api.services.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject


object ApiServices {
    private val api: ApiService = ApiClient.retrofit.create(ApiService::class.java)
    private val repo = ApiRepository(api)

    val authService = AuthService(repo)
    val dishService = DishService(repo)
    val reportService = ReportService(repo)
    val userService = UserService(repo)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onProfileClick: () -> Unit,
    onRecommendationsClick: () -> Unit,
    onWeeklyReportClick: () -> Unit,
    onCameraClick: () -> Unit,
    onFoodItemClick: (FoodItem) -> Unit,
    onManualFoodAdded: (FoodItem) -> Unit = {},
    onGalleryPhotoSelected: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showPhotoMenu by remember { mutableStateOf(false) }
    var showManualInputDialog by remember { mutableStateOf(false) }

    var foodItems by remember { mutableStateOf(listOf<FoodItem>()) }
    var goalCalories by remember { mutableStateOf(2000) }
    var totalCalories by remember { mutableStateOf(0) }

    // Launcher для выбора фото из галереи
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (bitmap != null) {
                    val base64String = bitmapToBase64(bitmap)
                    onGalleryPhotoSelected(base64String)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Загружаем еду и цель по калориям
    LaunchedEffect(Unit) {
        val header = AuthTokens.getAuthHeader()
        val dishesResponse = withContext(Dispatchers.IO) {
            ApiServices.dishService.getAll(header).execute()
        }
        val userResponse = withContext(Dispatchers.IO) {
            ApiServices.userService.getData(header).execute()
        }
        val bodyString = userResponse.body()?.string() ?: "{}"
        val json = JSONObject(bodyString)
        goalCalories = json.optJSONObject("user")?.optInt("dailyCalorieLimit") ?: 2000

        val items = mutableListOf<FoodItem>()
        if (dishesResponse.isSuccessful) {
            val jsonArray = JSONArray(dishesResponse.body()?.string() ?: "[]")
            for (i in 0 until jsonArray.length()) {
                val dish = jsonArray.getJSONObject(i)
                val description = dish.optString("foodName", "Unknown")
                val calories = dish.optInt("caloriesEstimated", 0)
                val protein = dish.optInt("proteinEstimated", 0)
                val fats = dish.optInt("fatsEstimated", 0)
                val carbs = dish.optInt("carbsEstimated", 0)
                val photoUrl = dish.optString("photoUrl", null)

                val imageData = photoUrl?.let {
                    val photoResp = withContext(Dispatchers.IO) {
                        ApiServices.dishService.getPhoto(it, header).execute()
                    }
                    if (photoResp.isSuccessful) {
                        val bytes = photoResp.body()?.bytes()
                        if (bytes != null) Base64.encodeToString(bytes, Base64.DEFAULT) else null
                    } else null
                }

                items.add(
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
        }
        foodItems = items
        totalCalories = items.sumOf { it.calories.toIntOrNull() ?: 0 }

    }

    // UI
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onProfileClick = { scope.launch { drawerState.close() }; onProfileClick() },
                onRecommendationsClick = { scope.launch { drawerState.close() }; onRecommendationsClick() },
                onWeeklyReportClick = { scope.launch { drawerState.close() }; onWeeklyReportClick() }
            )
        }
    ) {
        Scaffold(
            topBar = {
                MediumTopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Добро пожаловать!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(Icons.Default.Menu, contentDescription = "Меню")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    )
                )
            },
            floatingActionButton = {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SmallFloatingActionButton(
                        onClick = { showManualInputDialog = true },
                        containerColor = MaterialTheme.colorScheme.secondary
                    ) { Icon(Icons.Default.Edit, contentDescription = "Ручной ввод") }

                    FloatingActionButton(
                        onClick = { showPhotoMenu = true },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) { Icon(Icons.Default.CameraAlt, contentDescription = "Добавить еду") }
                }
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item { CalorieGoalCard(foodItems, goalCalories, totalCalories) }
                item { FoodListSection(foodItems, onFoodItemClick) }
            }
        }
    }

    if (showPhotoMenu) {
        PhotoMenuDialog(
            onDismiss = { showPhotoMenu = false },
            onCameraClick = {
                showPhotoMenu = false
                onCameraClick()
            },
            onGalleryClick = {
                showPhotoMenu = false
                galleryLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        )
    }

    if (showManualInputDialog) {
        ManualInputDialog(
            onDismiss = { showManualInputDialog = false },
            onConfirm = { description, calories, protein, fats, carbs ->
                val newFoodItem = FoodItem(
                    id = foodItems.size + 1,
                    description = description,
                    calories = calories,
                    protein = protein,
                    fats = fats,
                    carbs = carbs,
                    imageData = null
                )
                foodItems = foodItems + newFoodItem
                totalCalories += calories.toIntOrNull() ?: 0
                onManualFoodAdded(newFoodItem)
                showManualInputDialog = false
            }
        )
    }
}

private fun bitmapToBase64(bitmap: android.graphics.Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, outputStream)
    return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
}

@Composable
private fun CalorieGoalCard(foodItems: List<FoodItem>, goalCalories: Int, totalCalories: Int) {
    val progress = (totalCalories.toFloat() / goalCalories).coerceIn(0f, 1f)
    val percentage = (progress * 100).toInt()

    Card(modifier = Modifier.fillMaxWidth().shadow(12.dp, RoundedCornerShape(20.dp)), shape = RoundedCornerShape(20.dp)) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Цель по калориям", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text("$goalCalories ккал", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Потреблено: $totalCalories ккал", fontWeight = FontWeight.Medium)
                Spacer(Modifier.width(12.dp))
                Text("$percentage%", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))
            LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)), color = MaterialTheme.colorScheme.primary, trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
        }
    }
}

@Composable
private fun FoodListSection(foodItems: List<FoodItem>, onFoodItemClick: (FoodItem) -> Unit) {
    Column {
        Text("Еда за день", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(16.dp))
        if (foodItems.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.padding(40.dp), contentAlignment = Alignment.Center) {
                    Text("Пока ничего не добавлено\nНажмите на камеру, чтобы начать!", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(foodItems) { FoodItemCard(it, onFoodItemClick) }
            }
        }
    }
}

@Composable
private fun FoodItemCard(foodItem: FoodItem, onClick: (FoodItem) -> Unit) {
    Card(onClick = { onClick(foodItem) }, modifier = Modifier.size(120.dp).shadow(8.dp, RoundedCornerShape(16.dp)), shape = RoundedCornerShape(16.dp)) {
        Box {
            if (foodItem.imageData != null) {
                val imageBitmap = remember(foodItem.imageData) {
                    try {
                        val decoded = Base64.decode(foodItem.imageData, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(decoded, 0, decoded.size)?.asImageBitmap()
                    } catch (e: Exception) { null }
                }
                if (imageBitmap != null) Image(bitmap = imageBitmap, contentDescription = foodItem.description, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                else Box(modifier = Modifier.fillMaxSize().background(Color.Gray))
            } else Box(modifier = Modifier.fillMaxSize().background(Color.Gray))

            Column(modifier = Modifier.align(Alignment.BottomCenter).background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.8f)))).padding(8.dp).fillMaxWidth()) {
                Text(foodItem.description.take(20) + if (foodItem.description.length > 20) "..." else "", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Text("${foodItem.calories} ккал", color = Color.White.copy(0.8f), fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun DrawerContent(onProfileClick: () -> Unit, onRecommendationsClick: () -> Unit, onWeeklyReportClick: () -> Unit) {
    ModalDrawerSheet {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("GigaFood", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 16.dp))
            Divider()
            NavigationDrawerItem(icon = { Icon(Icons.Default.Person, null) }, label = { Text("Профиль") }, selected = false, onClick = onProfileClick)
            NavigationDrawerItem(icon = { Icon(Icons.Default.Lightbulb, null) }, label = { Text("Рекомендации") }, selected = false, onClick = onRecommendationsClick)
            NavigationDrawerItem(icon = { Icon(Icons.Default.BarChart, null) }, label = { Text("Еженедельный отчёт") }, selected = false, onClick = onWeeklyReportClick)
        }
    }
}





@Composable
private fun PhotoMenuDialog(
    onDismiss: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить еду", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onCameraClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CameraAlt, null)
                    Spacer(Modifier.width(12.dp))
                    Text("Сфотографировать")
                }
                OutlinedButton(
                    onClick = onGalleryClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PhotoLibrary, null)
                    Spacer(Modifier.width(12.dp))
                    Text("Из галереи")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}

@Composable
private fun ManualInputDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var fats by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ручной ввод", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Название блюда") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Калории (ккал)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = protein,
                        onValueChange = { protein = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Белки (г)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = fats,
                        onValueChange = { fats = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Жиры (г)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                OutlinedTextField(
                    value = carbs,
                    onValueChange = { carbs = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Углеводы (г)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (description.isNotBlank() && calories.isNotBlank()) {
                        onConfirm(
                            description,
                            calories,
                            protein.ifBlank { "0" },
                            fats.ifBlank { "0" },
                            carbs.ifBlank { "0" }
                        )
                    }
                },
                enabled = description.isNotBlank() && calories.isNotBlank()
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}

