package com.example.gigafood.ui.navigation

import androidx.compose.runtime.*
import com.example.gigafood.data.model.FoodItem
import com.example.gigafood.ui.screens.auth.*
import com.example.gigafood.ui.screens.camera.CameraScreen
import com.example.gigafood.ui.screens.camera.PhotoPreviewScreen
import com.example.gigafood.ui.screens.food.FoodEditScreen
import com.example.gigafood.ui.screens.main.MainScreen
import com.example.gigafood.ui.screens.profile.ProfileScreen
import com.example.gigafood.ui.screens.recommendations.RecommendationsScreen
import com.example.gigafood.ui.screens.report.WeeklyReportScreen

enum class Screen {
    Login, Register, ResetPassword, NewPassword,
    Main, Profile, Recommendations, Camera,
    PhotoPreview, WeeklyReport, FoodEdit
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
            onManualFoodAdded = { newFoodItem ->
                foodItems = foodItems + newFoodItem
            },
            onGalleryPhotoSelected = { imageData ->
                // Фото из галереи выбрано - переходим к предпросмотру
                capturedImageData = imageData
                currentScreen = Screen.PhotoPreview
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
            onBackClick = {
                // Возвращаемся на главный экран вместо камеры
                currentScreen = Screen.Main
            },
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
                    foodItems = foodItems.map {
                        if (it.id == updatedFoodItem.id) updatedFoodItem else it
                    }
                    currentScreen = Screen.Main
                },
                onBackClick = { currentScreen = Screen.Main }
            )
        }
    }
}