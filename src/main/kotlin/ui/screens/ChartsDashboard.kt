package com.businessanalytics.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.businessanalytics.data.ClientSummary
import com.businessanalytics.data.TransportSummary
import com.businessanalytics.ui.components.SimpleProgressBar
import com.businessanalytics.ui.theme.*

@Composable
fun ChartsDashboard(
    clientSummaries: List<ClientSummary>,
    transportSummaries: List<TransportSummary>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Заголовок
        Text(
            text = "📊 Визуальная аналитика",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = UzmkDarkText
        )

        // Первый ряд: Ключевые метрики
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Карточка 1: Лучший клиент
            Card(
                modifier = Modifier.weight(1f),
                elevation = 4.dp,
                backgroundColor = UzmkWhite
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    val topClient = clientSummaries
                        .maxByOrNull { it.totalShipmentAmount }

                    Text(
                        text = "🏆 Лучший клиент",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = UzmkDarkText,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (topClient != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        UzmkGold.copy(alpha = 0.2f),
                                        androidx.compose.foundation.shape.CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "👑",
                                    fontSize = 20.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = topClient.client.take(20) +
                                            if (topClient.client.length > 20) "..." else "",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = UzmkDarkText
                                )
                                Text(
                                    text = "%,.0f руб".format(topClient.totalShipmentAmount),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UzmkGold
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Нет данных",
                            fontSize = 14.sp,
                            color = UzmkGrayText
                        )
                    }
                }
            }

            // Карточка 2: Средняя оплата
            Card(
                modifier = Modifier.weight(1f),
                elevation = 4.dp,
                backgroundColor = UzmkWhite
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val avgPayment = clientSummaries
                        .map { it.paymentPercentage }
                        .average()
                        .takeIf { !it.isNaN() } ?: 0.0

                    Text(
                        text = "💰 Средняя оплата",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = UzmkDarkText,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    SimpleProgressBar(
                        percentage = (avgPayment / 100).toFloat(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "0%",
                            fontSize = 10.sp,
                            color = UzmkGrayText
                        )
                        Text(
                            text = "%.1f%%".format(avgPayment),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                avgPayment >= 90 -> SuccessGreen
                                avgPayment >= 70 -> UzmkGold
                                else -> ErrorRed
                            }
                        )
                        Text(
                            text = "100%",
                            fontSize = 10.sp,
                            color = UzmkGrayText
                        )
                    }
                }
            }
        }

        // Второй ряд: Транспортные услуги
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Карточка 3: Топ транспортных компаний
            Card(
                modifier = Modifier.weight(1f),
                elevation = 4.dp,
                backgroundColor = UzmkWhite
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🚚 Топ компаний",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = UzmkDarkText,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (transportSummaries.isNotEmpty()) {
                        transportSummaries
                            .sortedByDescending { it.totalCost }
                            .take(3)
                            .forEachIndexed { index, company ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Цветной индикатор
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                when (index) {
                                                    0 -> UzmkGold
                                                    1 -> UzmkBlue
                                                    else -> UzmkSteel
                                                }
                                            )
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Название компании
                                    Text(
                                        text = company.transportCompany.take(15) +
                                                if (company.transportCompany.length > 15) "..." else "",
                                        fontSize = 12.sp,
                                        color = UzmkDarkText,
                                        modifier = Modifier.weight(1f)
                                    )

                                    // Стоимость
                                    Text(
                                        text = "%,.0f руб".format(company.totalCost),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = UzmkSteel
                                    )
                                }
                            }
                    } else {
                        Text(
                            text = "Нет данных",
                            fontSize = 14.sp,
                            color = UzmkGrayText
                        )
                    }
                }
            }

            // Карточка 4: Статистика
            Card(
                modifier = Modifier.weight(1f),
                elevation = 4.dp,
                backgroundColor = UzmkWhite
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📈 Быстрая статистика",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = UzmkDarkText,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatRow(
                            icon = "👥",
                            label = "Клиентов",
                            value = clientSummaries.size.toString(),
                            color = UzmkBlue
                        )
                        StatRow(
                            icon = "🚚",
                            label = "Транспортных компаний",
                            value = transportSummaries.size.toString(),
                            color = UzmkGold
                        )
                        StatRow(
                            icon = "💰",
                            label = "Общая отгрузка",
                            value = "%,.0f руб".format(clientSummaries.sumOf { it.totalShipmentAmount }),
                            color = SuccessGreen
                        )
                        StatRow(
                            icon = "⚖️",
                            label = "Общий вес",
                            value = "%,.0f т".format(transportSummaries.sumOf { it.totalWeight }),
                            color = UzmkSteel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatRow(
    icon: String,
    label: String,
    value: String,
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
            Text(
                text = icon,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = UzmkGrayText
            )
        }
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}