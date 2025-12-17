package com.businessanalytics.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

data class DonutSegment(
    val value: Double,
    val color: Color,
    val label: String = ""
)

@Composable
fun SimpleDonutChart(
    modifier: Modifier = Modifier,
    segments: List<DonutSegment>,
    title: String = "",
    showPercentage: Boolean = true
) {
    val total = segments.sumOf { it.value }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = UzmkDarkText,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            // Кольцевая диаграмма
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val outerRadius = size.minDimension / 2
                val innerRadius = outerRadius * 0.5f

                var startAngle = -90f

                segments.forEach { segment ->
                    val sweepAngle = if (total > 0) {
                        (segment.value / total * 360).toFloat()
                    } else {
                        0f
                    }

                    // Рисуем дугу
                    drawArc(
                        color = segment.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                        size = Size(outerRadius * 2, outerRadius * 2),
                        style = Stroke(width = 30f)
                    )

                    startAngle += sweepAngle
                }
            }

            // Текст поверх (вне Canvas)
            if (showPercentage && segments.isNotEmpty() && total > 0) {
                val firstPercentage = (segments.first().value / total * 100).toInt()

                Text(
                    text = "$firstPercentage%",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = UzmkDarkText
                )
            }
        }

        // Простая легенда
        if (segments.size > 1) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                segments.forEachIndexed { index, segment ->
                    if (index < 3) { // Показываем только первые 3
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(segment.color)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = segment.label.take(15) +
                                        if (segment.label.length > 15) "..." else "",
                                fontSize = 10.sp,
                                color = UzmkGrayText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${((segment.value / total) * 100).toInt()}%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = UzmkDarkText
                            )
                        }
                    }
                }
            }
        }
    }
}