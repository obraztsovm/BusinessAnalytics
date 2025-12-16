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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.businessanalytics.data.TransportSummary
import com.businessanalytics.ui.theme.*

@Composable
fun TransportTable(transportResult: List<TransportSummary>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
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
                            colors = listOf(UzmkGold, UzmkBlue)
                        )
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderCell("Транспортная компания", 200, UzmkWhite)
                HeaderCell("Машины", 80, UzmkWhite)
                HeaderCell("Доля машин", 100, UzmkWhite)
                HeaderCell("Стоимость", 120, UzmkWhite)
                HeaderCell("Доля стоимости", 100, UzmkWhite)
                HeaderCell("Вес перевозок", 120, UzmkWhite)
            }

            // Данные таблицы
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .background(UzmkLightBg)
            ) {
                itemsIndexed(transportResult) { index, transportSummary ->
                    TransportTableRow(
                        transportSummary = transportSummary,
                        isEven = index % 2 == 0
                    )
                }
            }

            // Итоги
            val totals = calculateTransportTotals(transportResult)
            TransportTotalRow(totals)
        }
    }
}

@Composable
fun TransportTableRow(transportSummary: TransportSummary, isEven: Boolean) {
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
        DataCell(transportSummary.transportCompany, 200, TextAlign.Start, UzmkDarkText)
        DataCell(transportSummary.getFormattedVehicleCount(), 80, TextAlign.Center, UzmkSteel)
        DataCell(transportSummary.getFormattedVehicleShare(), 100, TextAlign.Center, UzmkSilver)
        DataCell(transportSummary.getFormattedTotalCost(), 120, TextAlign.End, UzmkDarkText)
        DataCell(transportSummary.getFormattedCostShare(), 100, TextAlign.Center, UzmkSilver)
        DataCell(transportSummary.getFormattedTotalWeight(), 120, TextAlign.End, UzmkSteel)
    }
}

@Composable
fun TransportTotalRow(totals: TransportTotals) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(UzmkGold.copy(alpha = 0.08f))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DataCell("ИТОГО:", 200, TextAlign.Start, UzmkGold, FontWeight.Bold)
        DataCell(totals.totalVehicles.toString(), 80, TextAlign.Center, UzmkGold, FontWeight.Bold)
        DataCell("100.0%", 100, TextAlign.Center, UzmkSilver, FontWeight.Bold)
        DataCell("%,.2f".format(totals.totalCost), 120, TextAlign.End, UzmkGold, FontWeight.Bold)
        DataCell("100.0%", 100, TextAlign.Center, UzmkSilver, FontWeight.Bold)
        DataCell("%,.2f".format(totals.totalWeight), 120, TextAlign.End, UzmkGold, FontWeight.Bold)
    }
}

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