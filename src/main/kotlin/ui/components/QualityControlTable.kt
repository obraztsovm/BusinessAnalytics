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
import com.businessanalytics.data.QualityControlSummary
import com.businessanalytics.ui.theme.*

@Composable
fun QualityControlTable(qcResult: List<QualityControlSummary>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        elevation = 0.dp,
        backgroundColor = UzmkWhite,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Заголовок таблицы с фиолетовым градиентом
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp)
                    .background(
                        brush = horizontalGradient(
                            colors = listOf(Color(0xFF7B1FA2), Color(0xFF9C27B0))
                        )
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderCell("Сотрудник ОТК", 180, UzmkWhite)
                HeaderCell("Вес провер. (т)", 120, UzmkWhite)
                HeaderCell("Стоимость (руб)", 120, UzmkWhite)
                HeaderCell("Доля по весу", 100, UzmkWhite)
                HeaderCell("Доля по стоим.", 100, UzmkWhite)
                HeaderCell("Эффективность", 100, UzmkWhite)
            }

            // Данные таблицы
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .background(UzmkLightBg)
            ) {
                itemsIndexed(qcResult) { index, qcSummary ->
                    QualityControlTableRow(
                        qcSummary = qcSummary,
                        isEven = index % 2 == 0
                    )
                }
            }

            // Итоги
            val totals = calculateQualityControlTotals(qcResult)
            QualityControlTotalRow(totals)
        }
    }
}

@Composable
fun QualityControlTableRow(qcSummary: QualityControlSummary, isEven: Boolean) {
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
        DataCell(qcSummary.employeeName, 180, TextAlign.Start, UzmkDarkText)
        DataCell(qcSummary.getFormattedWeight(), 120, TextAlign.End, UzmkSteel)
        DataCell(qcSummary.getFormattedValue(), 120, TextAlign.End, UzmkDarkText)
        DataCell(
            qcSummary.getFormattedWeightShare(),
            100,
            TextAlign.Center,
            getShareColor(qcSummary.weightShare)
        )
        DataCell(
            qcSummary.getFormattedValueShare(),
            100,
            TextAlign.Center,
            getShareColor(qcSummary.valueShare)
        )
        DataCell(
            qcSummary.getFormattedProductivity() + " руб/т",
            100,
            TextAlign.End,
            getProductivityColor(qcSummary.productivity)
        )
    }
}

@Composable
fun QualityControlTotalRow(totals: QualityControlTotals) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFF9C27B0).copy(alpha = 0.08f))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DataCell("ИТОГО:", 180, TextAlign.Start, Color(0xFF9C27B0), FontWeight.Bold)
        DataCell("%,.2f".format(totals.totalWeight), 120, TextAlign.End, Color(0xFF9C27B0), FontWeight.Bold)
        DataCell("%,.2f".format(totals.totalValue), 120, TextAlign.End, Color(0xFF9C27B0), FontWeight.Bold)
        DataCell("100.0%", 100, TextAlign.Center, UzmkSilver, FontWeight.Bold)
        DataCell("100.0%", 100, TextAlign.Center, UzmkSilver, FontWeight.Bold)
        DataCell(
            if (totals.totalWeight > 0) "%,.0f руб/т".format(totals.totalValue / totals.totalWeight)
            else "0 руб/т",
            100,
            TextAlign.End,
            Color(0xFF9C27B0),
            FontWeight.Bold
        )
    }
}

data class QualityControlTotals(
    val totalWeight: Double,
    val totalValue: Double
)

private fun calculateQualityControlTotals(data: List<QualityControlSummary>): QualityControlTotals {
    return QualityControlTotals(
        totalWeight = data.sumOf { it.totalWeight },
        totalValue = data.sumOf { it.totalValue }
    )
}

private fun getShareColor(share: Double): androidx.compose.ui.graphics.Color {
    return when {
        share >= 30 -> Color(0xFF4CAF50)  // Зеленый - высокая доля
        share >= 15 -> UzmkGold           // Оранжевый - средняя доля
        else -> UzmkSilver               // Светлый - низкая доля
    }
}

private fun getProductivityColor(productivity: Double): androidx.compose.ui.graphics.Color {
    return when {
        productivity >= 100000 -> Color(0xFF4CAF50)  // Высокая эффективность
        productivity >= 50000 -> UzmkGold           // Средняя эффективность
        else -> UzmkSteel                          // Низкая эффективность
    }
}