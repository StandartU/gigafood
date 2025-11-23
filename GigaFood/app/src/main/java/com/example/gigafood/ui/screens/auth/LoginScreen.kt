package com.example.gigafood.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gigafood.ui.components.GigaFoodButton
import com.example.gigafood.ui.components.GigaFoodCard
import com.example.gigafood.ui.theme.GigaFoodDimens

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
                .padding(GigaFoodDimens.ScreenPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "GigaFood",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary

            )

            Spacer(modifier = Modifier.height(20.dp))

            GigaFoodCard {
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
                GigaFoodButton(
                    text = "Войти",
                    onClick = onLoginClick
                )
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