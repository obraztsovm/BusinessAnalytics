package com.businessanalytics.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.businessanalytics.data.ClientSummary
import com.businessanalytics.ui.theme.*

/**
 * Интерактивная 3D столбчатая диаграмма для отображения топ-клиентов
 */
@Composable
fun ThreeDBarChart(
    clients: List<ClientSummary>,
    title: String = "🏆 3D Топ клиентов по отгрузкам",
    modifier: Modifier = Modifier
) {
    // Берем топ-6 клиентов
    val topClients = remember(clients) {
        clients
            .sortedByDescending { it.totalShipmentAmount }
            .take(6)
    }

    // Состояние для наведения мыши
    var hoveredIndex by remember { mutableStateOf(-1) }
    var hoveredValue by remember { mutableStateOf(0.0) }
    var hoveredClientName by remember { mutableStateOf("") }
    var mousePosition by remember { mutableStateOf(Offset.Zero) }

    // Цветовая схема
    // Более контрастная цветовая схема для лучшего 3D
    val colors = remember {
        listOf(
            Color(0xFFD4AF37),  // Золотой металлик
            Color(0xFF1976D2),  // Яркий синий
            Color(0xFF4CAF50),  // Яркий зеленый
            Color(0xFF607D8B),  // Серо-синий
            Color(0xFF9C27B0),  // Фиолетовый
            Color(0xFFFF9800)   // Оранжевый
        )
    }

    // Максимальное значение для масштабирования
    val maxAmount = remember(topClients) {
        topClients.maxOfOrNull { it.totalShipmentAmount } ?: 1.0
    }

    // Для отрисовки текста
    val textMeasurer = rememberTextMeasurer()

    Card(
        modifier = modifier,
        elevation = 8.dp,
        backgroundColor = UzmkWhite,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Заголовок графика
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = UzmkDarkText,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Область графика с обработкой мыши
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(UzmkLightBg, RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp)
                    .pointerInput(Unit) {
                        // Обработка движений мыши
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                val position = event.changes.first().position
                                mousePosition = position

                                // Определяем, над каким столбцом находится мышь
                                val padding = 50f
                                val graphWidth = size.width - padding * 2
                                val graphHeight = size.height - padding * 2

                                var newHoveredIndex = -1

                                if (topClients.isNotEmpty() && maxAmount > 0) {
                                    val barSpacing = graphWidth / (topClients.size * 3.5f)
                                    val barDepth = 16f
                                    val barWidth = (graphWidth - barSpacing * (topClients.size + 1)) / topClients.size

                                    topClients.forEachIndexed { index, client ->
                                        val barHeight = (client.totalShipmentAmount / maxAmount).toFloat() * graphHeight * 0.7f
                                        val x = padding + barSpacing + index * (barWidth + barSpacing)
                                        val y = padding + graphHeight - barHeight

                                        // Проверяем, находится ли мышь над столбцом
                                        // Увеличиваем область наведения для удобства
                                        val hitArea = Rect(
                                            left = x - 10f,
                                            top = y - 10f,
                                            right = x + barWidth + 10f + barDepth,
                                            bottom = y + barHeight + 10f
                                        )

                                        if (position.x >= hitArea.left &&
                                            position.x <= hitArea.right &&
                                            position.y >= hitArea.top &&
                                            position.y <= hitArea.bottom) {
                                            newHoveredIndex = index
                                        }
                                    }
                                }

                                if (newHoveredIndex != hoveredIndex) {
                                    hoveredIndex = newHoveredIndex
                                    if (hoveredIndex >= 0) {
                                        hoveredValue = topClients[hoveredIndex].totalShipmentAmount
                                        hoveredClientName = topClients[hoveredIndex].client
                                    } else {
                                        hoveredValue = 0.0
                                        hoveredClientName = ""
                                    }
                                }
                            }
                        }
                    }
            ) {
                // Сам график
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val padding = 50f
                    val graphWidth = size.width - padding * 2
                    val graphHeight = size.height - padding * 2

                    // Рисуем 3D сетку
                    draw3DGrid(padding, graphWidth, graphHeight)

                    if (topClients.isNotEmpty()) {
                        val barSpacing = graphWidth / (topClients.size * 3.5f)
                        val barDepth = 16f // Глубина 3D эффекта
                        val barWidth = (graphWidth - barSpacing * (topClients.size + 1)) / topClients.size

                        // Рисуем столбцы
                        topClients.forEachIndexed { index, client ->
                            val value = client.totalShipmentAmount
                            val barHeight = (value / maxAmount).toFloat() * graphHeight * 0.7f
                            val x = padding + barSpacing + index * (barWidth + barSpacing)
                            val y = padding + graphHeight - barHeight

                            val color = colors.getOrElse(index) { UzmkGrayText }
                            val isHovered = hoveredIndex == index

                            // Рисуем 3D столбец
                            draw3DBar(
                                position = Offset(x, y),
                                width = barWidth,
                                height = barHeight,
                                depth = barDepth,
                                color = color,
                                isHovered = isHovered,
                                textMeasurer = textMeasurer,
                                value = value
                            )

                            // Подпись клиента (сокращенная)
                            if (graphWidth > 300) {
                                val label = client.client.takeFirstLetters()
                                drawText(
                                    textMeasurer = textMeasurer,
                                    text = label,
                                    topLeft = Offset(x + barWidth / 2 - 15, padding + graphHeight + 10),
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        color = UzmkDarkText,
                                        textAlign = TextAlign.Center
                                    ),
                                    maxLines = 1,
                                    size = Size(30f, 20f)
                                )
                            }
                        }

                        // Рисуем значения на оси Y
                        drawYAxisLabels(padding, graphHeight, maxAmount, textMeasurer)
                    }
                }

                // Всплывающая подсказка при наведении
                if (hoveredIndex >= 0) {
                    TooltipBox(
                        value = hoveredValue,
                        clientName = hoveredClientName,
                        color = colors.getOrElse(hoveredIndex) { UzmkGrayText },
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                }
            }

            // Легенда внизу
            if (topClients.isNotEmpty()) {
                Legend3D(
                    clients = topClients,
                    colors = colors,
                    hoveredIndex = hoveredIndex,
                    onHover = { index ->
                        hoveredIndex = index
                        if (index >= 0) {
                            hoveredValue = topClients[index].totalShipmentAmount
                            hoveredClientName = topClients[index].client
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
            }
        }
    }
}

/**
 * Всплывающая подсказка при наведении
 */
@Composable
private fun TooltipBox(
    value: Double,
    clientName: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(end = 16.dp, top = 16.dp)
            .width(180.dp),
        elevation = 12.dp,
        backgroundColor = UzmkDarkText.copy(alpha = 0.92f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(color, androidx.compose.foundation.shape.CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = clientName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
            }

            Divider3D()

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Отгрузки:",
                        fontSize = 11.sp,
                        color = UzmkLightBg
                    )
                    Text(
                        text = "%,.0f руб".format(value),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = UzmkGold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Доля:",
                        fontSize = 11.sp,
                        color = UzmkLightBg
                    )
                    Text(
                        text = "%.1f%%".format(value / maxOf(value * 10, 1.0) * 10),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = SuccessGreen
                    )
                }
            }
        }
    }
}

/**
 * 3D Легенда
 */
@Composable
private fun Legend3D(
    clients: List<ClientSummary>,
    colors: List<Color>,
    hoveredIndex: Int,
    onHover: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Легенда:",
            fontSize = 12.sp,
            color = UzmkGrayText,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            clients.forEachIndexed { index, client ->
                LegendItem3D(
                    index = index,
                    client = client,
                    color = colors.getOrElse(index) { UzmkGrayText },
                    isHovered = hoveredIndex == index,
                    onHover = { onHover(index) },
                    onHoverEnd = { onHover(-1) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun LegendItem3D(
    index: Int,
    client: ClientSummary,
    color: Color,
    isHovered: Boolean,
    onHover: () -> Unit,
    onHoverEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 4.dp)
            .background(
                if (isHovered) color.copy(alpha = 0.1f)
                else Color.Transparent,
                RoundedCornerShape(6.dp)
            )
            .padding(vertical = 6.dp, horizontal = 4.dp)
            .pointerInput(Unit) {
                // Обработка наведения для легенды
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Enter,
                            PointerEventType.Move -> {
                                onHover()
                            }
                            PointerEventType.Exit -> {
                                onHoverEnd()
                            }
                            else -> {}
                        }
                    }
                }
            }
    ) {
        // 3D квадратик в легенде
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            color,
                            color.copy(alpha = 0.7f)
                        )
                    ),
                    shape = RoundedCornerShape(4.dp)
                )
                .graphicsLayer {
                    rotationX = -10f
                    rotationY = 5f
                    shadowElevation = if (isHovered) 8.dp.toPx() else 4.dp.toPx()
                }
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "#${index + 1}",
            fontSize = 10.sp,
            fontWeight = if (isHovered) FontWeight.Bold else FontWeight.Medium,
            color = if (isHovered) UzmkBlue else UzmkGrayText
        )

        Text(
            text = client.client.takeFirstLetters(),
            fontSize = 9.sp,
            maxLines = 1,
            color = UzmkDarkText
        )

        Text(
            text = "%,.0f".format(client.totalShipmentAmount / 1000) + "K",
            fontSize = 8.sp,
            color = UzmkSteel
        )
    }
}

/**
 * Разделитель для тултипа
 */
@Composable
private fun Divider3D() {
    Spacer(modifier = Modifier.height(8.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        UzmkGold.copy(alpha = 0.5f),
                        Color.Transparent
                    )
                )
            )
    )
    Spacer(modifier = Modifier.height(8.dp))
}

// ========== ФУНКЦИИ ДЛЯ РИСОВАНИЯ ==========

/**
 * Рисует 3D столбец (улучшенный вариант)
 */
private fun DrawScope.draw3DBar(
    position: Offset,
    width: Float,
    height: Float,
    depth: Float,
    color: Color,
    isHovered: Boolean,
    textMeasurer: TextMeasurer,
    value: Double
) {
    // Создаем темный контур
    val outlineColor = Color(
        red = (color.red * 0.6f).coerceAtMost(1f),
        green = (color.green * 0.6f).coerceAtMost(1f),
        blue = (color.blue * 0.6f).coerceAtMost(1f),
        alpha = color.alpha
    )

    val hoveredOutlineColor = Color(
        red = (color.red * 0.8f).coerceAtMost(1f),
        green = (color.green * 0.8f).coerceAtMost(1f),
        blue = (color.blue * 0.8f).coerceAtMost(1f),
        alpha = color.alpha
    )

    val mainColor = if (isHovered) color.copy(alpha = 1f) else color

    val topColor = if (isHovered) {
        Color(
            red = (color.red * 255 * 1.3f).toInt().coerceIn(0, 255) / 255f,
            green = (color.green * 255 * 1.3f).toInt().coerceIn(0, 255) / 255f,
            blue = (color.blue * 255 * 1.3f).toInt().coerceIn(0, 255) / 255f,
            alpha = color.alpha
        )
    } else {
        Color(
            red = (color.red * 255 * 1.15f).toInt().coerceIn(0, 255) / 255f,
            green = (color.green * 255 * 1.15f).toInt().coerceIn(0, 255) / 255f,
            blue = (color.blue * 255 * 1.15f).toInt().coerceIn(0, 255) / 255f,
            alpha = color.alpha
        )
    }

    val sideColor = if (isHovered) {
        Color(
            red = (color.red * 255 * 0.6f).toInt().coerceIn(0, 255) / 255f,
            green = (color.green * 255 * 0.6f).toInt().coerceIn(0, 255) / 255f,
            blue = (color.blue * 255 * 0.6f).toInt().coerceIn(0, 255) / 255f,
            alpha = color.alpha
        )
    } else {
        Color(
            red = (color.red * 255 * 0.7f).toInt().coerceIn(0, 255) / 255f,
            green = (color.green * 255 * 0.7f).toInt().coerceIn(0, 255) / 255f,
            blue = (color.blue * 255 * 0.7f).toInt().coerceIn(0, 255) / 255f,
            alpha = color.alpha
        )
    }

    val enhancedDepth = depth * 1.5f

    // 1. Боковая грань
    val sideFace = Path().apply {
        moveTo(position.x + width, position.y)
        lineTo(position.x + width + enhancedDepth * 0.7f, position.y - enhancedDepth * 0.5f)
        lineTo(position.x + width + enhancedDepth * 0.7f, position.y + height - enhancedDepth * 0.5f)
        lineTo(position.x + width, position.y + height)
        close()
    }

    // 2. Верхняя грань
    val topFace = Path().apply {
        moveTo(position.x, position.y)
        lineTo(position.x + width, position.y)
        lineTo(position.x + width + enhancedDepth * 0.7f, position.y - enhancedDepth * 0.5f)
        lineTo(position.x + enhancedDepth * 0.3f, position.y - enhancedDepth * 0.5f)
        close()
    }

    // 3. Передняя грань (основная) - БЕЗ СКРУГЛЕНИЯ!
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                mainColor.copy(alpha = 0.9f),
                mainColor,
                mainColor.copy(alpha = 0.8f)
            ),
            startY = position.y,
            endY = position.y + height
        ),
        topLeft = position,
        size = Size(width, height)
    )

    // 4. Боковая грань
    drawPath(
        path = sideFace,
        brush = Brush.verticalGradient(
            colors = listOf(
                sideColor.copy(alpha = 0.8f),
                sideColor,
                sideColor.copy(alpha = 0.9f)
            ),
            startY = position.y,
            endY = position.y + height
        ),
        style = Fill
    )

    // 5. Верхняя грань
    drawPath(
        path = topFace,
        brush = Brush.horizontalGradient(
            colors = listOf(
                topColor.copy(alpha = 0.9f),
                topColor,
                topColor.copy(alpha = 0.8f)
            ),
            startX = position.x,
            endX = position.x + width + enhancedDepth
        ),
        style = Fill
    )

    // 6. Обводка при наведении
    if (isHovered) {
        // Обводка передней грани - темный контур
        drawRect(
            color = hoveredOutlineColor,
            topLeft = position,
            size = Size(width, height),
            style = Stroke(width = 3f)
        )

        // Обводка боковой грани - темный контур
        drawPath(
            path = sideFace,
            color = hoveredOutlineColor.copy(alpha = 0.8f),
            style = Stroke(width = 2f)
        )

        // Обводка верхней грани - темный контур
        drawPath(
            path = topFace,
            color = hoveredOutlineColor.copy(alpha = 0.9f),
            style = Stroke(width = 2f)
        )

        // Внутренняя подсветка (опционально)
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.15f),
                    Color.Transparent
                ),
                center = Offset(position.x + width * 0.7f, position.y + height * 0.3f),
                radius = width * 0.8f
            ),
            topLeft = position,
            size = Size(width, height)
        )
    } else {
        // Тонкая темная обводка даже без наведения
        drawRect(
            color = outlineColor,
            topLeft = position,
            size = Size(width, height),
            style = Stroke(width = 1f)
        )
    }

    // 7. Контуры для объема
    drawPath(
        path = sideFace,
        color = outlineColor.copy(alpha = 0.2f),
        style = Stroke(width = 0.8f)
    )

    drawPath(
        path = topFace,
        color = outlineColor.copy(alpha = 0.2f),
        style = Stroke(width = 0.8f)
    )

    // 8. Тень под столбцом
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Black.copy(alpha = 0.2f),
                Color.Transparent
            ),
            startY = position.y + height,
            endY = position.y + height + 10f
        ),
        topLeft = Offset(
            position.x - enhancedDepth * 0.3f,
            position.y + height
        ),
        size = Size(width + enhancedDepth * 1.2f, 10f)
    )
}
/**
 * Рисует 3D сетку
 */
private fun DrawScope.draw3DGrid(
    padding: Float,
    graphWidth: Float,
    graphHeight: Float
) {
    val depth = 16f

    // Фон графика
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                UzmkLightBg.copy(alpha = 0.3f),
                UzmkLightBg.copy(alpha = 0.1f)
            )
        ),
        topLeft = Offset(padding - depth * 0.3f, padding - depth * 0.5f),
        size = Size(
            width = graphWidth + depth * 0.6f,
            height = graphHeight + depth
        )
    )

    // Линии сетки
    for (i in 1..4) {
        val y = padding + (graphHeight / 4) * i

        // Горизонтальные линии (с 3D перспективой)
        drawLine(
            color = UzmkGrayText.copy(alpha = 0.15f),
            start = Offset(padding - depth * 0.3f, y - depth * 0.5f),
            end = Offset(padding + graphWidth, y),
            strokeWidth = 0.8f
        )
    }

    // Оси координат
    // Ось X (с 3D эффектом)
    drawLine(
        color = UzmkGrayText.copy(alpha = 0.6f),
        start = Offset(padding - depth * 0.3f, padding + graphHeight - depth * 0.5f),
        end = Offset(padding + graphWidth, padding + graphHeight),
        strokeWidth = 2f,
        cap = StrokeCap.Round
    )

    // Ось Y
    drawLine(
        color = UzmkGrayText.copy(alpha = 0.6f),
        start = Offset(padding, padding + graphHeight),
        end = Offset(padding, padding),
        strokeWidth = 2f,
        cap = StrokeCap.Round
    )

    // Ось Z (для 3D)
    drawLine(
        color = UzmkGrayText.copy(alpha = 0.4f),
        start = Offset(padding, padding + graphHeight),
        end = Offset(padding - depth * 0.3f, padding + graphHeight - depth * 0.5f),
        strokeWidth = 1.5f,
        cap = StrokeCap.Round
    )
}

/**
 * Рисует подписи на оси Y
 */
private fun DrawScope.drawYAxisLabels(
    padding: Float,
    graphHeight: Float,
    maxAmount: Double,
    textMeasurer: TextMeasurer
) {
    for (i in 0..4) {
        val y = padding + (graphHeight / 4) * i
        val value = (maxAmount * (1 - i / 4f)).toInt()

        val formattedValue = when {
            value >= 1_000_000 -> "${value / 1_000_000}M"
            value >= 10_000 -> "${value / 1_000}K"
            else -> value.toString()
        }

        drawText(
            textMeasurer = textMeasurer,
            text = formattedValue,
            topLeft = Offset(padding - 40, y - 8),
            style = TextStyle(
                fontSize = 10.sp,
                color = UzmkGrayText,
                textAlign = TextAlign.Right
            ),
            size = Size(35f, 16f)
        )

        // Маленькие отметки на оси
        drawCircle(
            color = UzmkGrayText.copy(alpha = 0.4f),
            center = Offset(padding, y),
            radius = 2.5f
        )
    }
}

/**
 * Форматирует значение для отображения
 */
private fun formatValue(value: Int): String {
    return when {
        value >= 1_000_000 -> "${value / 1_000_000}M"
        value >= 10_000 -> "${value / 1_000}K"
        else -> value.toString()
    }
}

/**
 * Функция для получения первых букв имени (из вашего кода)
 */
private fun String.takeFirstLetters(): String {
    val words = this.split(" ", "-", "_", ".", ",")
    return when {
        words.size >= 2 -> {
            val first = words[0].firstOrNull()?.uppercaseChar() ?: 'X'
            val second = words[1].firstOrNull()?.uppercaseChar() ?: 'X'
            "$first$second"
        }
        this.length >= 2 -> {
            val first = this[0].uppercaseChar()
            val second = this[1].uppercaseChar()
            "$first$second"
        }
        this.isNotEmpty() -> this.take(2).uppercase()
        else -> "XX"
    }
}
