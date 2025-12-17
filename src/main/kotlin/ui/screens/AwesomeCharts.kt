package com.businessanalytics.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.businessanalytics.data.ClientSummary
import com.businessanalytics.data.TransportSummary
import com.businessanalytics.ui.theme.*

// ========== СТИЛЬНЫЙ ГРАФИК ТОП-КЛИЕНТОВ ==========
@Composable
fun TopClientsBarChart(
    clients: List<ClientSummary>,
    title: String = "🏆 Топ клиентов по отгрузкам",
    modifier: Modifier = Modifier
) {
    // ... (весь код остаётся как был, без изменений)
    // Оставляю тот же код столбчатого графика
}

// ========== КРУГОВАЯ ДИАГРАММА ДЛЯ ТРАНСПОРТА ==========
@Composable
fun TransportDonutChart(
    transportCompanies: List<TransportSummary>,
    title: String = "🚚 Доли транспортных компаний",
    modifier: Modifier = Modifier
) {
    // ... (весь код остаётся как был, без изменений)
    // Оставляю тот же код круговой диаграммы транспорта
}

// ========== КРУГОВАЯ ДИАГРАММА ДОЛЕЙ КЛИЕНТОВ ==========
@Composable
fun ClientsShareDonutChart(
    clients: List<ClientSummary>,
    title: String = "📊 Доли клиентов по отгрузкам",
    modifier: Modifier = Modifier
) {
    val topClients = clients
        .sortedByDescending { it.totalShipmentAmount }
        .take(5)

    val others = clients
        .sortedByDescending { it.totalShipmentAmount }
        .drop(5)

    val totalAmount = clients.sumOf { it.totalShipmentAmount }

    Card(
        modifier = modifier,
        elevation = 8.dp,
        backgroundColor = UzmkWhite,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = UzmkDarkText,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.minDimension / 2
                    val innerRadius = radius * 0.5f

                    var startAngle = -90f

                    val colors = listOf(UzmkGold, UzmkBlue, SuccessGreen, UzmkSteel, Color(0xFF9C27B0), Color(0xFF607D8B))

                    // Рисуем топ-5 клиентов
                    topClients.forEachIndexed { index, client ->
                        val sweepAngle = if (totalAmount > 0) {
                            (client.totalShipmentAmount / totalAmount * 360).toFloat()
                        } else 0f

                        val color = colors.getOrElse(index) { UzmkGrayText }

                        drawArc(
                            color = color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = 35f, cap = StrokeCap.Round)
                        )

                        startAngle += sweepAngle
                    }

                    // Рисуем сегмент "Остальные"
                    if (others.isNotEmpty() && totalAmount > 0) {
                        val othersAmount = others.sumOf { it.totalShipmentAmount }
                        val sweepAngle = (othersAmount / totalAmount * 360).toFloat()

                        drawArc(
                            color = Color(0xFFB0BEC5),
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = 35f, cap = StrokeCap.Round)
                        )
                    }

                    // Центральный круг
                    drawCircle(
                        color = UzmkLightBg,
                        center = center,
                        radius = innerRadius
                    )
                }

                // Центральный текст
                if (topClients.isNotEmpty() && totalAmount > 0) {
                    val topClientShare = (topClients.first().totalShipmentAmount / totalAmount * 100).toInt()
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$topClientShare%",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = UzmkDarkText
                        )
                        Text(
                            text = "лидер",
                            fontSize = 10.sp,
                            color = UzmkGrayText
                        )
                    }
                }
            }

            // Легенда
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                topClients.forEachIndexed { index, client ->
                    val color = listOf(UzmkGold, UzmkBlue, SuccessGreen, UzmkSteel, Color(0xFF9C27B0))
                        .getOrElse(index) { UzmkGrayText }

                    val percentage = if (totalAmount > 0)
                        (client.totalShipmentAmount / totalAmount * 100) else 0.0

                    LegendItem(
                        label = client.client.take(12) + if (client.client.length > 12) ".." else "",
                        value = "%,.0f руб".format(client.totalShipmentAmount),
                        percentage = "%.1f%%".format(percentage),
                        color = color
                    )
                }

                if (others.isNotEmpty() && totalAmount > 0) {
                    val othersAmount = others.sumOf { it.totalShipmentAmount }
                    val othersPercentage = (othersAmount / totalAmount * 100)

                    LegendItem(
                        label = "Остальные (${others.size})",
                        value = "%,.0f руб".format(othersAmount),
                        percentage = "%.1f%%".format(othersPercentage),
                        color = Color(0xFFB0BEC5)
                    )
                }
            }
        }
    }
}

// ========== ГРАФИК ОТГРУЗКИ VS ОПЛАТЫ ==========
@Composable
fun ShipmentVsPaymentChart(
    clients: List<ClientSummary>,
    title: String = "💰 Отгрузки vs Оплаты (топ-5)",
    modifier: Modifier = Modifier
) {
    val topClients = clients
        .sortedByDescending { it.totalShipmentAmount }
        .take(5)

    val maxAmount = topClients.maxOfOrNull { it.totalShipmentAmount } ?: 0.0
    val maxPayment = topClients.maxOfOrNull { it.totalPaymentAmount } ?: 0.0
    val maxValue = maxOf(maxAmount, maxPayment)

    Card(
        modifier = modifier,
        elevation = 8.dp,
        backgroundColor = UzmkWhite,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = UzmkDarkText,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Легенда
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendDot(color = UzmkBlue, label = "Отгрузки")
                Spacer(modifier = Modifier.width(24.dp))
                LegendDot(color = UzmkGold, label = "Оплаты")
            }

            // График
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val paddingLeft = 80f
                    val paddingRight = 20f
                    val paddingTop = 30f
                    val paddingBottom = 40f

                    val graphWidth = size.width - paddingLeft - paddingRight
                    val graphHeight = size.height - paddingTop - paddingBottom

                    // Ось Y
                    drawLine(
                        color = UzmkGrayText.copy(alpha = 0.5f),
                        start = Offset(paddingLeft, paddingTop),
                        end = Offset(paddingLeft, paddingTop + graphHeight),
                        strokeWidth = 1.5f
                    )

                    // Ось X
                    drawLine(
                        color = UzmkGrayText.copy(alpha = 0.5f),
                        start = Offset(paddingLeft, paddingTop + graphHeight),
                        end = Offset(paddingLeft + graphWidth, paddingTop + graphHeight),
                        strokeWidth = 1.5f
                    )

                    // Столбцы
                    if (topClients.isNotEmpty()) {
                        val barWidth = graphWidth / (topClients.size * 3f)
                        val spacing = barWidth * 1.5f

                        topClients.forEachIndexed { index, client ->
                            val x = paddingLeft + spacing + index * (barWidth * 2 + spacing)

                            // Столбец отгрузок
                            val shipmentHeight = graphHeight * (client.totalShipmentAmount / maxValue).toFloat()
                            drawRoundRect(
                                color = UzmkBlue,
                                topLeft = Offset(x, paddingTop + graphHeight - shipmentHeight),
                                size = Size(barWidth, shipmentHeight),
                                cornerRadius = CornerRadius(barWidth / 4, barWidth / 4)
                            )

                            // Столбец оплат
                            val paymentHeight = graphHeight * (client.totalPaymentAmount / maxValue).toFloat()
                            drawRoundRect(
                                color = UzmkGold,
                                topLeft = Offset(x + barWidth + 2, paddingTop + graphHeight - paymentHeight),
                                size = Size(barWidth, paymentHeight),
                                cornerRadius = CornerRadius(barWidth / 4, barWidth / 4)
                            )

                            // Линия разницы (если оплаты меньше отгрузок)
                            if (client.totalPaymentAmount < client.totalShipmentAmount) {
                                val diffY = paddingTop + graphHeight - paymentHeight
                                drawLine(
                                    color = ErrorRed,
                                    start = Offset(x, diffY),
                                    end = Offset(x + barWidth * 2 + 2, diffY),
                                    strokeWidth = 1.5f,
                                    cap = StrokeCap.Round
                                )
                            }
                        }
                    }
                }

                // Подписи клиентов
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 100.dp, top = 180.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        topClients.forEach { client ->
                            Text(
                                text = client.client.takeFirstLetters(),
                                fontSize = 10.sp,
                                color = UzmkDarkText,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Статистика по разнице
            val totalShipment = topClients.sumOf { it.totalShipmentAmount }
            val totalPayment = topClients.sumOf { it.totalPaymentAmount }
            val totalDifference = totalShipment - totalPayment
            val paymentPercentage = if (totalShipment > 0) (totalPayment / totalShipment * 100) else 0.0

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = "%,.0f".format(totalShipment),
                    label = "Всего отгружено",
                    color = UzmkBlue
                )
                StatItem(
                    value = "%,.0f".format(totalPayment),
                    label = "Всего оплачено",
                    color = UzmkGold
                )
                StatItem(
                    value = "%.1f%%".format(paymentPercentage),
                    label = "Процент оплаты",
                    color = if (paymentPercentage >= 70) SuccessGreen else ErrorRed
                )
            }
        }
    }
}

// ========== ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ ==========
@Composable
fun LegendItem(
    label: String,
    value: String,
    percentage: String,
    color: Color
) {
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
                    .size(10.dp)
                    .background(color, androidx.compose.foundation.shape.CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = UzmkDarkText
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = value,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = UzmkSteel
            )
            Text(
                text = percentage,
                fontSize = 10.sp,
                color = UzmkGrayText
            )
        }
    }
}

@Composable
fun LegendDot(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, androidx.compose.foundation.shape.CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = UzmkDarkText
        )
    }
}

// Функция для получения первых букв имени клиента
private fun String.takeFirstLetters(): String {
    val words = this.split(" ", "-", "_")
    return when {
        words.size >= 2 -> "${words[0].first()}${words[1].first()}"
        this.length >= 2 -> "${this[0]}${this[1]}"
        else -> this.take(2)
    }.uppercase()
}

// ========== ОБНОВЛЁННАЯ ГЛАВНАЯ ПАНЕЛЬ ==========
@Composable
fun AwesomeChartsPanel(
    clientSummaries: List<ClientSummary>,
    transportSummaries: List<TransportSummary>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "📊 Визуальная аналитика",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = UzmkDarkText,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Первый ряд: два графика по клиентам
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            TopClientsBarChart(
                clients = clientSummaries,
                modifier = Modifier.weight(1f)
            )

            ClientsShareDonutChart(
                clients = clientSummaries,
                modifier = Modifier.weight(1f)
            )
        }

        // Второй ряд: два графика
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ShipmentVsPaymentChart(
                clients = clientSummaries,
                modifier = Modifier.weight(1f)
            )

            TransportDonutChart(
                transportCompanies = transportSummaries,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// StatItem остаётся без изменений
@Composable
fun StatItem(
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = UzmkGrayText
        )
    }
}