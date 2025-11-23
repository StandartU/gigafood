package com.example.gigafood.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object GigaFoodColors {
    val Primary = Color(0xFF4C9AFF)
    val Secondary = Color(0xFF00C48C)
    val Background = Color(0xFFF7FAFF)
    val Surface = Color.White
    val OnSurface = Color(0xFF111827)
    val OnPrimary = Color.White
    val OnSecondary = Color.White
}

object GigaFoodDimens {
    val ScreenPadding = 16.dp
    val CardElevation = 6.dp
    val BigButtonHeight = 52.dp
    val CardCornerRadius = 12.dp
}

val CardShape = RoundedCornerShape(GigaFoodDimens.CardCornerRadius)

@Composable
fun GigaFoodTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = GigaFoodColors.Primary,
            onPrimary = GigaFoodColors.OnPrimary,
            secondary = GigaFoodColors.Secondary,
            onSecondary = GigaFoodColors.OnSecondary,
            background = GigaFoodColors.Background,
            surface = GigaFoodColors.Surface,
            onSurface = GigaFoodColors.OnSurface
        ),
        typography = Typography(),
        content = content
    )
}