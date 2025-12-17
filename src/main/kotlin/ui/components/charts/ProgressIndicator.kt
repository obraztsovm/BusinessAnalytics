// ProgressIndicatorFixed.kt
package com.businessanalytics.ui.components.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.businessanalytics.ui.theme.*

@Composable
fun SimpleProgressIndicator(
    value: Double, // Ожидается от 0.0 до 100.0
    title: String = "",
    barHeight: Dp = 8.dp
) {
    // Нормализуем значение и определяем цвет
    val normalizedValue = value.coerceIn(0.0, 100.0)
    val percentage = normalizedValue / 100.0
    val color = when {
        normalizedValue >= 90 -> SuccessGreen
        normalizedValue >= 70 -> UzmkGold
        else -> ErrorRed
    }

    Column {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = UzmkGrayText,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        // Сам прогресс-бар
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .background(UzmkLightBg)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage.toFloat()) // Ключевое исправление
                    .background(color)
            )
        }
        // Подпись со значением
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Прогресс",
                fontSize = 10.sp,
                color = UzmkGrayText
            )
            Text(
                text = "${normalizedValue.toInt()}%",
                fontSize = 12.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                color = color
            )
        }
    }
}