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
import com.businessanalytics.data.SupplierSummary
import com.businessanalytics.ui.theme.*

@Composable
fun SupplierTable(supplierResult: List<SupplierSummary>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        elevation = 0.dp,
        backgroundColor = UzmkWhite,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Заголовок таблицы с зеленым градиентом
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp)
                    .background(
                        brush = horizontalGradient(
                            colors = listOf(UzmkGold, Color(0xFF4CAF50))
                        )
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderCell("Поставщик", 180, UzmkWhite)
                HeaderCell("Вес (т)", 100, UzmkWhite)
                HeaderCell("Стоимость", 120, UzmkWhite)
                HeaderCell("Доля по кол-ву", 120, UzmkWhite)
                HeaderCell("Доля по стоим.", 120, UzmkWhite)
                HeaderCell("Ср. цена/т", 100, UzmkWhite)
            }

            // Данные таблицы
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .background(UzmkLightBg)
            ) {
                itemsIndexed(supplierResult) { index, supplierSummary ->
                    SupplierTableRow(
                        supplierSummary = supplierSummary,
                        isEven = index % 2 == 0
                    )
                }
            }

            // Итоги
            val totals = calculateSupplierTotals(supplierResult)
            SupplierTotalRow(totals)
        }
    }
}

@Composable
fun SupplierTableRow(supplierSummary: SupplierSummary, isEven: Boolean) {
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
        DataCell(supplierSummary.supplier, 180, TextAlign.Start, UzmkDarkText)
        DataCell(supplierSummary.getFormattedWeight(), 100, TextAlign.End, UzmkSteel)
        DataCell(supplierSummary.getFormattedCost(), 120, TextAlign.End, UzmkDarkText)
        DataCell(
            supplierSummary.getFormattedQuantityShare(),
            120,
            TextAlign.Center,
            getShareColor(supplierSummary.quantityShare)
        )
        DataCell(
            supplierSummary.getFormattedCostShare(),
            120,
            TextAlign.Center,
            getShareColor(supplierSummary.costShare)
        )
        DataCell(
            supplierSummary.getFormattedAvgCostPerTon(),
            100,
            TextAlign.End,
            if (supplierSummary.avgCostPerTon > 0) UzmkDarkText else UzmkGrayText
        )
    }
}

@Composable
fun SupplierTotalRow(totals: SupplierTotals) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFF4CAF50).copy(alpha = 0.08f))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DataCell("ИТОГО:", 180, TextAlign.Start, Color(0xFF4CAF50), FontWeight.Bold)
        DataCell("%,.2f".format(totals.totalWeight), 100, TextAlign.End, Color(0xFF4CAF50), FontWeight.Bold)
        DataCell("%,.2f".format(totals.totalCost), 120, TextAlign.End, Color(0xFF4CAF50), FontWeight.Bold)
        DataCell("100.0%", 120, TextAlign.Center, UzmkSilver, FontWeight.Bold)
        DataCell("100.0%", 120, TextAlign.Center, UzmkSilver, FontWeight.Bold)
        DataCell(
            if (totals.totalWeight > 0) "%,.1f".format(totals.totalCost / totals.totalWeight)
            else "0.0",
            100,
            TextAlign.End,
            Color(0xFF4CAF50),
            FontWeight.Bold
        )
    }
}

data class SupplierTotals(
    val totalWeight: Double,
    val totalCost: Double
)

private fun calculateSupplierTotals(data: List<SupplierSummary>): SupplierTotals {
    val totalWeight = data.sumOf { it.totalWeight }
    val totalCost = data.sumOf { it.totalCost }

    return SupplierTotals(
        totalWeight = totalWeight,
        totalCost = totalCost
    )
}

private fun getShareColor(share: Double): androidx.compose.ui.graphics.Color {
    return when {
        share >= 40 -> Color(0xFF4CAF50) // Зеленый для крупных поставщиков
        share >= 20 -> UzmkGold           // Оранжевый для средних
        else -> UzmkSilver               // Светлый для мелких
    }
}