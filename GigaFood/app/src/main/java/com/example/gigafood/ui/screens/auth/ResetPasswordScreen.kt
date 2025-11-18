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
fun ResetPasswordScreen(
    onSendClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(GigaFoodDimens.ScreenPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Сброс пароля",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(18.dp))

            GigaFoodCard {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Электронная почта") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(18.dp))
                GigaFoodButton(
                    text = "Отправить инструкцию",
                    onClick = onSendClick
                )

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
