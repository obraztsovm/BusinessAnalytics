package com.businessanalytics.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.businessanalytics.data.ClientSummary

@Composable
fun SimpleTable(analysisResult: List<ClientSummary>) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        elevation = 4.dp,
        backgroundColor = Color.White
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Заголовок таблицы
            Row(
                modifier = Modifier
                    .background(Color(0xFF2196F3))
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                HeaderCell("Клиент", 180)
                HeaderCell("Сумма отгр.", 120)
                HeaderCell("Вес отгр.", 100)
                HeaderCell("Сумма опл.", 120)
                HeaderCell("Оплата %", 80)
                HeaderCell("Доля отгр.", 80)
                HeaderCell("Доля опл.", 80)
            }

            // Данные таблицы
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(analysisResult) { clientSummary ->
                    TableRow(clientSummary)
                }
            }

            // Итоги
            val totals = calculateTotals(analysisResult)
            TotalRow(totals)
        }
    }
}

@Composable
fun TableRow(clientSummary: ClientSummary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        DataCell(clientSummary.client, 180, TextAlign.Start, Color(0xFF424242))
        DataCell(clientSummary.getFormattedShipmentAmount(), 120, TextAlign.End, Color(0xFF2E7D32))
        DataCell(clientSummary.getFormattedShipmentWeight(), 100, TextAlign.End, Color(0xFF1976D2))
        DataCell(clientSummary.getFormattedPaymentAmount(), 120, TextAlign.End, Color(0xFFF57C00))
        DataCell(clientSummary.getFormattedPaymentPercentage(), 80, TextAlign.End, getPercentageColor(clientSummary.paymentPercentage))
        DataCell(clientSummary.getFormattedShipmentShare(), 80, TextAlign.End, Color(0xFF7B1FA2))
        DataCell(clientSummary.getFormattedPaymentShare(), 80, TextAlign.End, Color(0xFFC2185B))
    }
}

@Composable
fun TotalRow(totals: Totals) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(vertical = 12.dp, horizontal = 12.dp)
    ) {
        DataCell("ИТОГО:", 180, TextAlign.Start, Color(0xFF212121))
        DataCell("%,.2f".format(totals.totalShipmentAmount), 120, TextAlign.End, Color(0xFF1B5E20))
        DataCell("%,.2f".format(totals.totalShipmentWeight), 100, TextAlign.End, Color(0xFF0D47A1))
        DataCell("%,.2f".format(totals.totalPaymentAmount), 120, TextAlign.End, Color(0xFFE65100))
        DataCell("%,.1f%%".format(totals.avgPaymentPercentage), 80, TextAlign.End, Color(0xFF4A148C))
        DataCell("100.0%", 80, TextAlign.End, Color(0xFF6A1B9A))
        DataCell("100.0%", 80, TextAlign.End, Color(0xFFAD1457))
    }
}

// Вспомогательные классы и функции
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
        percentage >= 90 -> Color(0xFF2E7D32)
        percentage >= 70 -> Color(0xFFF57C00)
        else -> Color(0xFFC62828)
    }
}