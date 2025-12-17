package com.businessanalytics.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.businessanalytics.ui.theme.*
import kotlin.math.max

data class BarChartItem(
    val label: String,
    val value: Double,
    val maxValue: Double,
    val gradientColors: List<Color> = listOf(UzmkBlue, UzmkGold)
)

@Composable
fun HorizontalBarChart(
    modifier: Modifier = Modifier,
    items: List<BarChartItem>,
    title: String = "",
    barHeight: Dp = 24.dp,
    spacing: Dp = 12.dp,
    showValues: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = UzmkDarkText
            )
        }

        items.forEachIndexed { index, item ->
            BarChartRow(
                item = item,
                barHeight = barHeight,
                showValue = showValues,
                modifier = Modifier.fillMaxWidth()
            )

            if (index < items.lastIndex) {
                Spacer(modifier = Modifier.height(spacing))
            }
        }
    }
}

@Composable
fun BarChartRow(
    modifier: Modifier = Modifier,
    item: BarChartItem,
    barHeight: Dp = 24.dp,
    showValue: Boolean = true
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.label,
                fontSize = 12.sp,
                color = UzmkDarkText,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )

            if (showValue) {
                Text(
                    text = "%,.0f".format(item.value),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = UzmkSteel
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width

                // Используем max с Double, конвертируем результат в Float
                val percentage = (item.value / max(item.maxValue, 1.0)).toFloat()
                val barWidth = width * percentage
                val barHeightPx = barHeight.toPx()

                // Фон бара
                drawRect(
                    color = UzmkLightBg,
                    topLeft = Offset(0f, 0f),
                    size = Size(width, barHeightPx)
                )

                // Градиентный бар
                val gradientBrush = Brush.horizontalGradient(
                    colors = item.gradientColors,
                    startX = 0f,
                    endX = barWidth
                )

                // Рисуем скругленный прямоугольник
                drawRoundRect(
                    brush = gradientBrush,
                    topLeft = Offset(0f, 0f),
                    size = Size(barWidth, barHeightPx),
                    cornerRadius = CornerRadius(barHeightPx / 2)
                )
            }
        }
    }
}