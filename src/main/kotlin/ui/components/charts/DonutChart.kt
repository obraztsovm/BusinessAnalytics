// ui/charts/DonutChart.kt
package com.businessanalytics.ui.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.businessanalytics.ui.theme.*

@Composable
fun DonutChart(
    segments: List<ChartSegment>,
    title: String = "",
    modifier: Modifier = Modifier
) {
    val total = segments.sumOf { it.value }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = UzmkDarkText,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            // Кольцевая диаграмма
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val outerRadius = size.minDimension / 2
                val innerRadius = outerRadius * 0.5f

                var startAngle = -90f // Начинаем сверху

                segments.forEach { segment ->
                    val sweepAngle = if (total > 0) {
                        (segment.value / total * 360).toFloat()
                    } else 0f

                    // Рисуем дугу для сегмента
                    drawArc(
                        color = segment.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                        size = Size(outerRadius * 2, outerRadius * 2),
                        style = Stroke(width = 40f, cap = StrokeCap.Round)
                    )

                    startAngle += sweepAngle
                }

                // Центральный круг
                drawCircle(
                    color = UzmkLightBg,
                    center = center,
                    radius = innerRadius
                )
            }

            // Текст в центре
            if (segments.isNotEmpty() && total > 0) {
                val mainValue = segments.first().value
                val percentage = (mainValue / total * 100).toInt()

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$percentage%",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = UzmkDarkText
                    )
                    Text(
                        text = "доля",
                        fontSize = 12.sp,
                        color = UzmkGrayText
                    )
                }
            }
        }

        // Легенда
        if (segments.size > 1) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                segments.forEach { segment ->
                    LegendItem(segment = segment, total = total)
                }
            }
        }
    }
}

@Composable
fun LegendItem(segment: ChartSegment, total: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        segment.color,
                        androidx.compose.foundation.shape.CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = segment.label,
                fontSize = 12.sp,
                color = UzmkDarkText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        val percentage = if (total > 0) (segment.value / total * 100) else 0.0
        Text(
            text = "%.1f%%".format(percentage),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = UzmkSteel
        )
    }
}
