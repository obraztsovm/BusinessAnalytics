package com.businessanalytics.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
// ДОБАВЬ В НАЧАЛО TransportTable.kt:
import com.businessanalytics.ui.components.HeaderCell
import com.businessanalytics.ui.components.DataCell
import com.businessanalytics.data.TransportSummary

@Composable
fun TransportTable(transportResult: List<TransportSummary>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp) // ВРЕМЕННО ФИКСИРОВАННАЯ ВЫСОТА
            .background(Color.Red) // ЯРКИЙ ФОН ДЛЯ ТЕСТА
            .border(4.dp, Color.Red) // ЯРКАЯ ГРАНИЦА
            .padding(8.dp),
        elevation = 4.dp,
        backgroundColor = Color.Yellow
    ) {
        println("🎯 TransportTable вызван с ${transportResult.size} компаниями")
        Column(modifier = Modifier.fillMaxWidth()) {
            // Заголовок таблицы
            Row(
                modifier = Modifier
                    .background(Color(0xFFFF9800))
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                HeaderCell("Транспортная компания", 200)
                HeaderCell("Машины", 80)
                HeaderCell("Доля машин", 100)
                HeaderCell("Стоимость", 120)
                HeaderCell("Доля стоимости", 100)
                HeaderCell("Вес перевозок", 120)
            }

            // Данные таблицы
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .background(Color.Green) // ЗЕЛЕНЫЙ ФОН ДАННЫХ
            ) {
                items(transportResult) { transportSummary ->
                    TransportTableRow(transportSummary)
                }
            }

            // Итоги
            val totals = calculateTransportTotals(transportResult)
            TransportTotalRow(totals)
        }
    }
}

@Composable
fun TransportTableRow(transportSummary: TransportSummary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        DataCell(transportSummary.transportCompany, 200, TextAlign.Start, Color(0xFF424242))
        DataCell(transportSummary.getFormattedVehicleCount(), 80, TextAlign.Center, Color(0xFF1976D2))
        DataCell(transportSummary.getFormattedVehicleShare(), 100, TextAlign.Center, Color(0xFF7B1FA2))
        DataCell(transportSummary.getFormattedTotalCost(), 120, TextAlign.End, Color(0xFF2E7D32))
        DataCell(transportSummary.getFormattedCostShare(), 100, TextAlign.Center, Color(0xFFC2185B))
        DataCell(transportSummary.getFormattedTotalWeight(), 120, TextAlign.End, Color(0xFFF57C00))
    }
}

@Composable
fun TransportTotalRow(totals: TransportTotals) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(vertical = 12.dp, horizontal = 12.dp)
    ) {
        DataCell("ИТОГО:", 200, TextAlign.Start, Color(0xFF212121))
        DataCell(totals.totalVehicles.toString(), 80, TextAlign.Center, Color(0xFF0D47A1))
        DataCell("100.0%", 100, TextAlign.Center, Color(0xFF6A1B9A))
        DataCell("%,.2f".format(totals.totalCost), 120, TextAlign.End, Color(0xFF1B5E20))
        DataCell("100.0%", 100, TextAlign.Center, Color(0xFFAD1457))
        DataCell("%,.2f".format(totals.totalWeight), 120, TextAlign.End, Color(0xFFE65100))
    }
}




// Вспомогательные классы и функции
data class TransportTotals(
    val totalVehicles: Int,
    val totalCost: Double,
    val totalWeight: Double
)

private fun calculateTransportTotals(data: List<TransportSummary>): TransportTotals {
    val totalVehicles = data.sumOf { it.vehicleCount }
    val totalCost = data.sumOf { it.totalCost }
    val totalWeight = data.sumOf { it.totalWeight }

    return TransportTotals(
        totalVehicles = totalVehicles,
        totalCost = totalCost,
        totalWeight = totalWeight
    )
}