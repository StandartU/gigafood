package com.example.gigafood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.gigafood.ui.navigation.AppNavigation
import com.example.gigafood.ui.theme.GigaFoodTheme

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