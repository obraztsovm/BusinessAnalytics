package com.businessanalytics.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.businessanalytics.ui.theme.*

@Composable
fun KPICards(
    clientSummaries: List<ClientSummary>,
    modifier: Modifier = Modifier
) {
    val totalShipment = clientSummaries.sumOf { it.totalShipmentAmount }
    val totalPayment = clientSummaries.sumOf { it.totalPaymentAmount }
    val paymentPercentage = if (totalShipment > 0) (totalPayment / totalShipment * 100) else 0.0
    val totalWeight = clientSummaries.sumOf { it.totalShipmentWeight }
    val clientCount = clientSummaries.size
    val avgCheck = if (clientCount > 0) totalShipment / clientCount else 0.0

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Первая строка: 2 карточки
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            KPICard(
                title = "Общая отгрузка",
                value = "%,.0f".format(totalShipment),
                unit = "руб",
                icon = "💰",
                color = UzmkGold,
                secondaryText = "Вес: ${"%,.1f".format(totalWeight)} т",
                trend = if (totalShipment > 0) "📈" else "",
                modifier = Modifier.weight(1f)
            )

            KPICard(
                title = "Оплачено",
                value = "%,.0f".format(totalPayment),
                unit = "руб",
                icon = "💳",
                color = if (paymentPercentage >= 70) SuccessGreen else WarningOrange,
                secondaryText = "${"%.1f".format(paymentPercentage)}% от отгрузки",
                trend = if (paymentPercentage >= 70) "✅" else "⚠️",
                modifier = Modifier.weight(1f)
            )
        }

        // Вторая строка: 2 карточки
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            KPICard(
                title = "Активных клиентов",
                value = clientCount.toString(),
                unit = "",
                icon = "👥",
                color = UzmkBlue,
                secondaryText = "с данными за период",
                trend = if (clientCount > 0) "👍" else "",
                modifier = Modifier.weight(1f)
            )

            KPICard(
                title = "Средний чек",
                value = "%,.0f".format(avgCheck),
                unit = "руб",
                icon = "📊",
                color = UzmkSteel,
                secondaryText = "на клиента",
                trend = if (avgCheck > 0) "📊" else "",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun KPICard(
    title: String,
    value: String,
    unit: String,
    icon: String,
    color: Color,
    secondaryText: String = "",
    trend: String = "",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = 4.dp,
        backgroundColor = UzmkWhite,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Верхняя строка: иконка и тренд
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    fontSize = 20.sp
                )
                if (trend.isNotEmpty()) {
                    Text(
                        text = trend,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Основное значение
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = value,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                if (unit.isNotEmpty()) {
                    Text(
                        text = " $unit",
                        fontSize = 12.sp,
                        color = UzmkGrayText,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            // Заголовок
            Text(
                text = title,
                fontSize = 12.sp,
                color = UzmkGrayText,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Вторичный текст (если есть)
            if (secondaryText.isNotEmpty()) {
                Text(
                    text = secondaryText,
                    fontSize = 10.sp,
                    color = UzmkSilver,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}