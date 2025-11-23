package com.example.gigafood.ui.screens.camera

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.gigafood.ui.components.ScreenHeader
import com.example.gigafood.ui.theme.*
import com.example.gigafood.ui.theme.GigaFoodDimens.BigButtonHeight
import com.example.gigafood.ui.theme.GigaFoodDimens.ScreenPadding

@Composable
fun PhotoPreviewScreen(
    imageData: String?,
    onBackClick: () -> Unit,
    onConfirmClick: (calories: String, protein: String, fats: String, carbs: String, description: String) -> Unit
) {
    var calories by remember { mutableStateOf("450") }
    var protein by remember { mutableStateOf("25") }
    var fats by remember { mutableStateOf("18") }
    var carbs by remember { mutableStateOf("52") }
    var description by remember { mutableStateOf("Куриная грудка с рисом и овощами") }
    var showEditDialog by remember { mutableStateOf(false) }

    // Декодируем изображение заранее
    val decodedImage = remember(imageData) {
        imageData?.let { decodeBase64ToImageBitmap(it) }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ScreenPadding)
        ) {
            Spacer(modifier = Modifier.height(44.dp))
            ScreenHeader(title = "Результат анализа", onBackClick = onBackClick)

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Показ фото
                PhotoCard(decodedImage = decodedImage)

                Spacer(modifier = Modifier.height(16.dp))

                NutritionInfoCard(
                    description = description,
                    calories = calories,
                    protein = protein,
                    fats = fats,
                    carbs = carbs,
                    onEditClick = { showEditDialog = true },
                    onConfirmClick = { onConfirmClick(calories, protein, fats, carbs, description) }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Диалог редактирования
    if (showEditDialog) {
        EditNutritionDialog(
            description = description,
            calories = calories,
            protein = protein,
            fats = fats,
            carbs = carbs,
            onDismiss = { showEditDialog = false },
            onSave = { newDesc, newCal, newProt, newFats, newCarbs ->
                description = newDesc
                calories = newCal
                protein = newProt
                fats = newFats
                carbs = newCarbs
                showEditDialog = false
            }
        )
    }
}

@Composable
private fun PhotoCard(decodedImage: ImageBitmap?) {
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
                .background(Color(0xFF2D2D2D)),
            contentAlignment = Alignment.Center
        ) {
            if (decodedImage != null) {
                Image(
                    bitmap = decodedImage,
                    contentDescription = "Сфотографированное блюдо",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text("Фото блюда", color = Color.White)
            }
        }
    }
}

@Composable
private fun NutritionInfoCard(
    description: String,
    calories: String,
    protein: String,
    fats: String,
    carbs: String,
    onEditClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NutritionItem("Калории", "$calories ккал")
                NutritionItem("Белки", "$protein г")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NutritionItem("Жиры", "$fats г")
                NutritionItem("Углеводы", "$carbs г")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onEditClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BigButtonHeight),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Изменить данные вручную", color = MaterialTheme.colorScheme.onSecondary)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onConfirmClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BigButtonHeight),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Подтвердить и сохранить")
            }
        }
    }
}

@Composable
private fun NutritionItem(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(
            value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun EditNutritionDialog(
    description: String,
    calories: String,
    protein: String,
    fats: String,
    carbs: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String) -> Unit
) {
    var editDesc by remember { mutableStateOf(description) }
    var editCal by remember { mutableStateOf(calories) }
    var editProt by remember { mutableStateOf(protein) }
    var editFats by remember { mutableStateOf(fats) }
    var editCarbs by remember { mutableStateOf(carbs) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редактировать данные") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = editDesc,
                    onValueChange = { editDesc = it },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = editCal,
                    onValueChange = { editCal = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Калории (ккал)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = editProt,
                    onValueChange = { editProt = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Белки (г)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = editFats,
                    onValueChange = { editFats = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Жиры (г)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = editCarbs,
                    onValueChange = { editCarbs = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Углеводы (г)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(editDesc, editCal, editProt, editFats, editCarbs)
            }) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

// Функция для декодирования Base64 в ImageBitmap (вызывается вне Composable)
private fun decodeBase64ToImageBitmap(base64String: String): ImageBitmap? {
    return try {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        bitmap?.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}