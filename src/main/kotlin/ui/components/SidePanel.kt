package com.businessanalytics.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.businessanalytics.ui.theme.*

@Composable
fun SidePanel(
    modifier: Modifier = Modifier,
    selectedScreen: String = "Главная",
    onItemSelected: (String) -> Unit = {}
) {
    Column(
        modifier = modifier
            .background(UzmkWhite)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Заголовок со стрелкой вверх в скругленном квадрате
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Скругленный квадрат с графиком
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(UzmkBlue, UzmkGold)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Минималистичный график с осями
                Canvas(modifier = Modifier.size(32.dp)) {
                    val strokeWidth = 2f
                    val padding = 4f

                    // Ось X (горизонтальная)
                    drawLine(
                        color = UzmkWhite.copy(alpha = 0.7f),
                        start = Offset(padding, size.height - padding),
                        end = Offset(size.width - padding, size.height - padding),
                        strokeWidth = 1f
                    )

                    // Ось Y (вертикальная)
                    drawLine(
                        color = UzmkWhite.copy(alpha = 0.7f),
                        start = Offset(padding, padding),
                        end = Offset(padding, size.height - padding),
                        strokeWidth = 1f
                    )

                    // Ломаная линия графика
                    val path = Path().apply {
                        // Начальная точка (слева внизу)
                        moveTo(padding, size.height - padding)

                        // Точки для ломаной линии роста
                        lineTo(size.width * 0.3f, size.height * 0.7f)
                        lineTo(size.width * 0.5f, size.height * 0.4f)
                        lineTo(size.width * 0.7f, size.height * 0.5f)
                        lineTo(size.width - padding, padding)
                    }

                    // Рисуем линию
                    drawPath(
                        path = path,
                        color = UzmkWhite,
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )

                    // Точки на графике
                    val points = listOf(
                        Offset(padding, size.height - padding),
                        Offset(size.width * 0.3f, size.height * 0.7f),
                        Offset(size.width * 0.5f, size.height * 0.4f),
                        Offset(size.width * 0.7f, size.height * 0.5f),
                        Offset(size.width - padding, padding)
                    )

                    points.forEach { point ->
                        drawCircle(
                            color = UzmkWhite,
                            radius = 1.5f,
                            center = point
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "УЗСМК",
                    color = UzmkBlue,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Text(
                    text = "Аналитика",
                    color = UzmkSteel,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Пункты меню
        SideBarItem("Главная", selectedScreen == "Главная", onClick = { onItemSelected("Главная") })
        SideBarItem("Сводка", selectedScreen == "Сводка", onClick = { onItemSelected("Сводка") })
        SideBarItem("Финансы", selectedScreen == "Финансы", onClick = { onItemSelected("Финансы") })
        SideBarItem("Производство", selectedScreen == "Производство", onClick = { onItemSelected("Производство") })
        SideBarItem("Продажи", selectedScreen == "Продажи", onClick = { onItemSelected("Продажи") })
        SideBarItem("Логистика", selectedScreen == "Логистика", onClick = { onItemSelected("Логистика") })
        SideBarItem("Качество", selectedScreen == "Качество", onClick = { onItemSelected("Качество") })
        SideBarItem("Закупки", selectedScreen == "Закупки", onClick = { onItemSelected("Закупки") })
        SideBarItem("Подрядчики", selectedScreen == "Подрядчики", onClick = { onItemSelected("Подрядчики") })
    }
}

@Composable
fun SideBarItem(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) UzmkBlue.copy(alpha = 0.15f)
                else Color.Transparent
            )
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) UzmkBlue else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Убираем Row с точкой, оставляем только текст
        Text(
            text = title,
            color = if (isSelected) UzmkBlue else UzmkDarkText,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}