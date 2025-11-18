// Full MainScreen.kt with gallery support
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gigafood.data.model.FoodItem
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onProfileClick: () -> Unit,
    onRecommendationsClick: () -> Unit,
    onWeeklyReportClick: () -> Unit,
    onCameraClick: () -> Unit,
    foodItems: List<FoodItem>,
    onFoodItemClick: (FoodItem) -> Unit,
    onManualFoodAdded: (FoodItem) -> Unit = {},
    onGalleryPhotoSelected: (String) -> Unit = {}  // Callback для фото из галереи
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showPhotoMenu by remember { mutableStateOf(false) }
    var showManualInputDialog by remember { mutableStateOf(false) }

    // Launcher для выбора фото из галереи
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            try {
                // Конвертируем URI в Base64
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

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onProfileClick = {
                    scope.launch { drawerState.close() }
                    onProfileClick()
                },
                onRecommendationsClick = {
                    scope.launch { drawerState.close() }
                    onRecommendationsClick()
                },
                onWeeklyReportClick = {
                    scope.launch { drawerState.close() }
                    onWeeklyReportClick()
                }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
                    ),
                    scrollBehavior = scrollBehavior
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
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Ручной ввод")
                    }

                    FloatingActionButton(
                        onClick = { showPhotoMenu = true },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Добавить еду")
                    }
                }
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            LazyColumn(
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
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }
                item { CalorieGoalCard(foodItems = foodItems) }
                item { FoodListSection(foodItems = foodItems, onFoodItemClick = onFoodItemClick) }
                item { Spacer(Modifier.height(80.dp)) }
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
                    imageData = null,
                    description = description,
                    calories = calories,
                    protein = protein,
                    fats = fats,
                    carbs = carbs
                )
                onManualFoodAdded(newFoodItem)
                showManualInputDialog = false
            }
        )
    }
}

// Функция конвертации Bitmap в Base64
private fun bitmapToBase64(bitmap: Bitmap): String {
    val maxSize = 1024
    val ratio = Math.min(
        maxSize.toFloat() / bitmap.width,
        maxSize.toFloat() / bitmap.height
    )
    val width = (ratio * bitmap.width).toInt()
    val height = (ratio * bitmap.height).toInt()

    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)

    val outputStream = ByteArrayOutputStream()
    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
    val byteArray = outputStream.toByteArray()

    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

@Composable
private fun DrawerContent(
    onProfileClick: () -> Unit,
    onRecommendationsClick: () -> Unit,
    onWeeklyReportClick: () -> Unit
) {
    ModalDrawerSheet {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "GigaFood",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Divider()
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Person, null) },
                label = { Text("Профиль") },
                selected = false,
                onClick = onProfileClick
            )
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Lightbulb, null) },
                label = { Text("Рекомендации") },
                selected = false,
                onClick = onRecommendationsClick
            )
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.BarChart, null) },
                label = { Text("Еженедельный отчёт") },
                selected = false,
                onClick = onWeeklyReportClick
            )
        }
    }
}

@Composable
private fun CalorieGoalCard(foodItems: List<FoodItem>) {
    val totalCalories = foodItems.sumOf { it.calories.toIntOrNull() ?: 0 }
    val goalCalories = 2000
    val progress = (totalCalories.toFloat() / goalCalories).coerceIn(0f, 1f)
    val percentage = (progress * 100).toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp)
    ) {
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
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
private fun FoodListSection(
    foodItems: List<FoodItem>,
    onFoodItemClick: (FoodItem) -> Unit
) {
    Column {
        Text("Еда за день", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(16.dp))

        if (foodItems.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.padding(40.dp), contentAlignment = Alignment.Center) {
                    Text(
                        "Пока ничего не добавлено\nНажмите на камеру, чтобы начать!",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(foodItems) { item ->
                    FoodItemCard(item, onFoodItemClick)
                }
            }
        }
    }
}

@Composable
private fun FoodItemCard(foodItem: FoodItem, onClick: (FoodItem) -> Unit) {
    Card(
        onClick = { onClick(foodItem) },
        modifier = Modifier.size(120.dp).shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box {
            if (foodItem.imageData != null) {
                val imageBitmap = remember(foodItem.imageData) {
                    try {
                        val decodedBytes = Base64.decode(foodItem.imageData, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                        bitmap?.asImageBitmap()
                    } catch (e: Exception) {
                        null
                    }
                }

                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = foodItem.description,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF333333)))
                }
            } else {
                Box(modifier = Modifier.fillMaxSize().background(Color(0xFF333333))) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.Center),
                        tint = Color.White.copy(alpha = 0.3f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.8f))))
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = foodItem.description.take(20) + if (foodItem.description.length > 20) "..." else "",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2
                )
                Text("${foodItem.calories} ккал", color = Color.White.copy(0.8f), fontSize = 10.sp)
            }
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