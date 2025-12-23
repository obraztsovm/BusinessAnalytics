package com.businessanalytics.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.businessanalytics.data.ClientSummary
import com.businessanalytics.ui.theme.*

@Composable
fun ClientBarChart(
    clients: List<ClientSummary>,
    title: String = "📊 Топ клиентов по отгрузкам",
    modifier: Modifier = Modifier
) {
    val topClients = clients
        .sortedByDescending { it.totalShipmentAmount }
        .take(5)

    if (topClients.isEmpty()) return

    val maxAmount = topClients.maxOf { it.totalShipmentAmount }

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

            // График с осями
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

                    // Ось Y (вертикальная)
                    drawLine(
                        color = UzmkGrayText.copy(alpha = 0.5f),
                        start = Offset(paddingLeft, paddingTop),
                        end = Offset(paddingLeft, paddingTop + graphHeight),
                        strokeWidth = 2f
                    )

                    // Ось X (горизонтальная)
                    drawLine(
                        color = UzmkGrayText.copy(alpha = 0.5f),
                        start = Offset(paddingLeft, paddingTop + graphHeight),
                        end = Offset(paddingLeft + graphWidth, paddingTop + graphHeight),
                        strokeWidth = 2f
                    )

                    // Метки на оси Y
                    val ySteps = 5
                    for (i in 0..ySteps) {
                        val y = paddingTop + graphHeight * (1 - i.toFloat() / ySteps)

                        // Линия сетки
                        drawLine(
                            color = UzmkGrayText.copy(alpha = 0.2f),
                            start = Offset(paddingLeft, y),
                            end = Offset(paddingLeft + graphWidth, y),
                            strokeWidth = 1f
                        )
                    }

                    // Столбцы клиентов
                    val barWidth = graphWidth / (topClients.size * 1.5f)
                    val spacing = barWidth * 0.5f

                    topClients.forEachIndexed { index, client ->
                        val x = paddingLeft + spacing + index * (barWidth + spacing)
                        val barHeight = graphHeight * (client.totalShipmentAmount / maxAmount).toFloat()
                        val barY = paddingTop + graphHeight - barHeight

                        // Столбец с градиентом
                        drawRect(
                            color = UzmkBlue,
                            topLeft = Offset(x, barY),
                            size = Size(barWidth, barHeight)
                        )

                        // Темная полоска сверху
                        drawRect(
                            color = UzmkBlue.copy(alpha = 0.3f),
                            topLeft = Offset(x, barY),
                            size = Size(barWidth, 2f)
                        )
                    }
                }

                // Подписи значений на оси Y
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

                // Подписи клиентов снизу
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
            }

            // Легенда
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🏆 ${topClients.first().client} - лидер отгрузок",
                    fontSize = 12.sp,
                    color = UzmkGold,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}