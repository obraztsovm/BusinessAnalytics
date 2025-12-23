package com.businessanalytics.ui.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.businessanalytics.data.ClientSummary
import com.businessanalytics.ui.theme.*

@Composable
fun ShipmentPaymentChart(
    clients: List<ClientSummary>,
    title: String = "💰 Отгрузки vs Оплаты",
    modifier: Modifier = Modifier
) {
    val topClients = clients
        .sortedByDescending { it.totalShipmentAmount }
        .take(5)

    if (topClients.isEmpty()) return

    val maxAmount = topClients.maxOf { maxOf(it.totalShipmentAmount, it.totalPaymentAmount) }

    Card(
        modifier = modifier,
        elevation = 4.dp,
        backgroundColor = UzmkWhite,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Заголовок
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
                LegendDot(color = UzmkGold, label = "Отгрузки")
                Spacer(modifier = Modifier.width(24.dp))
                LegendDot(color = UzmkBlue, label = "Оплаты")
            }

            // График
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val paddingLeft = 60f
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
                        strokeWidth = 2f
                    )

                    // Ось X
                    drawLine(
                        color = UzmkGrayText.copy(alpha = 0.5f),
                        start = Offset(paddingLeft, paddingTop + graphHeight),
                        end = Offset(paddingLeft + graphWidth, paddingTop + graphHeight),
                        strokeWidth = 2f
                    )

                    // Сетка
                    for (i in 0..5) {
                        val y = paddingTop + graphHeight * (1 - i.toFloat() / 5)
                        drawLine(
                            color = UzmkGrayText.copy(alpha = 0.1f),
                            start = Offset(paddingLeft, y),
                            end = Offset(paddingLeft + graphWidth, y),
                            strokeWidth = 1f
                        )
                    }

                    // Столбцы
                    val barWidth = graphWidth / (topClients.size * 3f)
                    val spacing = barWidth * 0.5f

                    topClients.forEachIndexed { index, client ->
                        val x = paddingLeft + spacing + index * (barWidth * 2 + spacing)

                        // Столбец отгрузок
                        val shipmentHeight = graphHeight * (client.totalShipmentAmount / maxAmount).toFloat()
                        drawRect(
                            color = UzmkGold,
                            topLeft = Offset(x, paddingTop + graphHeight - shipmentHeight),
                            size = Size(barWidth, shipmentHeight)
                        )

                        // Столбец оплат
                        val paymentHeight = graphHeight * (client.totalPaymentAmount / maxAmount).toFloat()
                        drawRect(
                            color = UzmkBlue,
                            topLeft = Offset(x + barWidth + 2, paddingTop + graphHeight - paymentHeight),
                            size = Size(barWidth, paymentHeight)
                        )

                        // Линия если оплаты меньше
                        if (client.totalPaymentAmount < client.totalShipmentAmount) {
                            val diffY = paddingTop + graphHeight - paymentHeight
                            drawLine(
                                color = ErrorRed,
                                start = Offset(x, diffY),
                                end = Offset(x + barWidth * 2 + 2, diffY),
                                strokeWidth = 1.5f
                            )
                        }
                    }
                }

                // Подписи клиентов
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 60.dp, top = 160.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        topClients.forEach { client ->
                            Text(
                                text = client.client.take(3),
                                fontSize = 10.sp,
                                color = UzmkDarkText,
                                maxLines = 1,
                                modifier = Modifier.width(30.dp)
                            )
                        }
                    }
                }

                // Значения на оси Y
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    for (i in 0..5) {
                        val value = (maxAmount * i / 5).toInt()
                        val yOffset = 30.dp + (200.dp - 40.dp) * (1 - i.toFloat() / 5)

                        Text(
                            text = "%,d".format(value),
                            fontSize = 10.sp,
                            color = UzmkGrayText,
                            modifier = Modifier
                                .padding(start = 45.dp, top = yOffset - 10.dp)
                        )
                    }
                }
            }

            // Статистика
            val totalShipment = topClients.sumOf { it.totalShipmentAmount }
            val totalPayment = topClients.sumOf { it.totalPaymentAmount }
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
                    color = UzmkGold
                )
                StatItem(
                    value = "%,.0f".format(totalPayment),
                    label = "Всего оплачено",
                    color = UzmkBlue
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
            fontSize = 14.sp,
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