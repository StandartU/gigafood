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
import com.example.gigafood.api.ApiClient
import com.example.gigafood.api.ApiRepository
import com.example.gigafood.api.ApiService
import com.example.gigafood.api.services.AuthService
import com.example.gigafood.ui.components.GigaFoodButton
import com.example.gigafood.ui.components.GigaFoodCard
import com.example.gigafood.ui.theme.GigaFoodDimens
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

@Composable
fun RegisterScreen(
    onRegisterClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val api = ApiClient.retrofit.create(ApiService::class.java)
    val repo = ApiRepository(api)
    val authService = AuthService(repo)

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(GigaFoodDimens.ScreenPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Регистрация",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(18.dp))

            GigaFoodCard {
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

                if (!errorMsg.isNullOrEmpty()) {
                    Text(errorMsg!!, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                GigaFoodButton(
                    text = if (isLoading) "Регистрация..." else "Зарегистрироваться",
                    onClick = {
                        if (password != confirm) {
                            errorMsg = "Пароли не совпадают"
                            return@GigaFoodButton
                        }
                        errorMsg = null
                        isLoading = true

                        // --- Вызов API singup ---
                        val headers = emptyMap<String, String>() // добавь токены/заголовки, если нужно
                        authService.signup(mapOf(
                            "username" to login,
                            "password" to password
                        )).enqueue(object : retrofit2.Callback<ResponseBody> {
                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                isLoading = false
                                if (response.isSuccessful) {
                                    onRegisterClick()
                                } else {
                                    errorMsg = "Ошибка регистрации: ${response.code()}"
                                }
                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                isLoading = false
                                errorMsg = "Ошибка: ${t.localizedMessage}"
                            }
                        })
                    }
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