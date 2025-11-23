package com.example.gigafood.ui.screens.food

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gigafood.data.model.FoodItem
import com.example.gigafood.ui.components.GigaFoodButton
import com.example.gigafood.ui.components.GigaFoodCard
import com.example.gigafood.ui.components.ScreenHeader
import com.example.gigafood.ui.theme.GigaFoodDimens

@Composable
fun FoodEditScreen(
    foodItem: FoodItem,
    onSaveClick: (FoodItem) -> Unit,
    onBackClick: () -> Unit
) {
    var editedItem by remember { mutableStateOf(foodItem) }

    // Декодируем изображение заранее
    val decodedImage = remember(foodItem.imageData) {
        foodItem.imageData?.let {
            try {
                val decodedBytes = Base64.decode(it, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                bitmap?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(GigaFoodDimens.ScreenPadding)
        ) {
            Spacer(modifier = Modifier.height(44.dp))
            ScreenHeader(title = "Редактирование", onBackClick = onBackClick)

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                FoodImageCard(decodedImage = decodedImage)

                Spacer(modifier = Modifier.height(16.dp))

                GigaFoodCard {
                    FoodEditForm(
                        foodItem = editedItem,
                        onFoodItemChange = { editedItem = it }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    GigaFoodButton(
                        text = "Сохранить изменения",
                        onClick = { onSaveClick(editedItem) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun FoodImageCard(decodedImage: androidx.compose.ui.graphics.ImageBitmap?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = GigaFoodDimens.CardElevation)
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
                    contentDescription = "Фото блюда",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.White.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
private fun FoodEditForm(
    foodItem: FoodItem,
    onFoodItemChange: (FoodItem) -> Unit
) {
    OutlinedTextField(
        value = foodItem.description,
        onValueChange = { onFoodItemChange(foodItem.copy(description = it)) },
        label = { Text("Описание") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = foodItem.calories,
        onValueChange = {
            onFoodItemChange(foodItem.copy(calories = it.filter { ch -> ch.isDigit() }))
        },
        label = { Text("Калории (ккал)") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = foodItem.protein,
        onValueChange = {
            onFoodItemChange(foodItem.copy(protein = it.filter { ch -> ch.isDigit() }))
        },
        label = { Text("Белки (г)") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = foodItem.fats,
        onValueChange = {
            onFoodItemChange(foodItem.copy(fats = it.filter { ch -> ch.isDigit() }))
        },
        label = { Text("Жиры (г)") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = foodItem.carbs,
        onValueChange = {
            onFoodItemChange(foodItem.copy(carbs = it.filter { ch -> ch.isDigit() }))
        },
        label = { Text("Углеводы (г)") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}