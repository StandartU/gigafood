package com.example.gigafood.ui.components
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gigafood.ui.theme.GigaFoodDimens

@Composable
fun ScreenHeader(
    title: String,
    onBackClick: (() -> Unit)? = null,
    backButtonText: String = "Назад"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.primary
        )

        onBackClick?.let {
            Button(
                onClick = it,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(backButtonText, color = MaterialTheme.colorScheme.onSecondary)
            }
        }
    }
}

@Composable
fun GigaFoodButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(GigaFoodDimens.BigButtonHeight),
        shape = RoundedCornerShape(10.dp),
        enabled = enabled
    ) {
        Text(text)
    }
}

@Composable
fun GigaFoodCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(GigaFoodDimens.CardCornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = GigaFoodDimens.CardElevation)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}
