package com.businessanalytics.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush.Companion.horizontalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.businessanalytics.data.ClientSummary
import com.businessanalytics.ui.theme.*

@Composable
fun SimpleTable(analysisResult: List<ClientSummary>) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        elevation = 0.dp,
        backgroundColor = UzmkWhite,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Заголовок таблицы с оранжевым градиентом
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp)
                    .background(
                        brush = horizontalGradient(
                            colors = listOf(UzmkBlue, UzmkGold)
                        )
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderCell("Клиент", 180, UzmkWhite)
                HeaderCell("Сумма отгр.", 120, UzmkWhite)
                HeaderCell("Вес отгр.", 100, UzmkWhite)
                HeaderCell("Сумма опл.", 120, UzmkWhite)
                HeaderCell("Оплата %", 80, UzmkWhite)
                HeaderCell("Доля отгр.", 80, UzmkWhite)
                HeaderCell("Доля опл.", 80, UzmkWhite)
            }

            // Данные таблицы
            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(analysisResult) { index, clientSummary ->
                    TableRow(
                        clientSummary = clientSummary,
                        isEven = index % 2 == 0
                    )
                }
            }

            // Итоги
            val totals = calculateTotals(analysisResult)
            TotalRow(totals)
        }
    }
}

@Composable
fun TableRow(clientSummary: ClientSummary, isEven: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(
                if (isEven) UzmkLightBg
                else UzmkWhite
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DataCell(clientSummary.client, 180, TextAlign.Start, UzmkDarkText)
        DataCell(clientSummary.getFormattedShipmentAmount(), 120, TextAlign.End, UzmkDarkText)
        DataCell(clientSummary.getFormattedShipmentWeight(), 100, TextAlign.End, UzmkSteel)
        DataCell(clientSummary.getFormattedPaymentAmount(), 120, TextAlign.End, UzmkDarkText)
        DataCell(
            clientSummary.getFormattedPaymentPercentage(),
            80,
            TextAlign.Center,
            getPercentageColor(clientSummary.paymentPercentage)
        )
        DataCell(clientSummary.getFormattedShipmentShare(), 80, TextAlign.Center, UzmkSilver)
        DataCell(clientSummary.getFormattedPaymentShare(), 80, TextAlign.Center, UzmkSilver)
    }
}

@Composable
fun TotalRow(totals: Totals) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(UzmkBlue.copy(alpha = 0.08f))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DataCell("ИТОГО:", 180, TextAlign.Start, UzmkBlue, FontWeight.Bold)
        DataCell("%,.2f".format(totals.totalShipmentAmount), 120, TextAlign.End, UzmkBlue, FontWeight.Bold)
        DataCell("%,.2f".format(totals.totalShipmentWeight), 100, TextAlign.End, UzmkSteel, FontWeight.Bold)
        DataCell("%,.2f".format(totals.totalPaymentAmount), 120, TextAlign.End, UzmkBlue, FontWeight.Bold)
        DataCell("%,.1f%%".format(totals.avgPaymentPercentage), 80, TextAlign.Center, UzmkGold, FontWeight.Bold)
        DataCell("100.0%", 80, TextAlign.Center, UzmkSilver, FontWeight.Bold)
        DataCell("100.0%", 80, TextAlign.Center, UzmkSilver, FontWeight.Bold)
    }
}

@Composable
fun HeaderCell(text: String, width: Int, textColor: Color = UzmkWhite) {
    Box(
        modifier = Modifier.width(width.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DataCell(
    text: String,
    width: Int,
    textAlign: TextAlign,
    color: Color = UzmkDarkText,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Box(
        modifier = Modifier.width(width.dp),
        contentAlignment = when (textAlign) {
            TextAlign.Start -> Alignment.CenterStart
            TextAlign.End -> Alignment.CenterEnd
            else -> Alignment.Center
        }
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 12.sp,
            fontWeight = fontWeight,
            textAlign = textAlign
        )
    }
}

data class Totals(
    val totalShipmentAmount: Double,
    val totalShipmentWeight: Double,
    val totalPaymentAmount: Double,
    val avgPaymentPercentage: Double
)

private fun calculateTotals(data: List<ClientSummary>): Totals {
    val totalShipmentAmount = data.sumOf { it.totalShipmentAmount }
    val totalShipmentWeight = data.sumOf { it.totalShipmentWeight }
    val totalPaymentAmount = data.sumOf { it.totalPaymentAmount }

    val avgPaymentPercentage = if (totalShipmentAmount > 0) {
        (totalPaymentAmount / totalShipmentAmount) * 100
    } else {
        0.0
    }

    return Totals(
        totalShipmentAmount = totalShipmentAmount,
        totalShipmentWeight = totalShipmentWeight,
        totalPaymentAmount = totalPaymentAmount,
        avgPaymentPercentage = avgPaymentPercentage
    )
}

private fun getPercentageColor(percentage: Double): Color {
    return when {
        percentage >= 90 -> SuccessGreen
        percentage >= 70 -> UzmkGold
        else -> ErrorRed
    }
}