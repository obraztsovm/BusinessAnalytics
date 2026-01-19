package com.businessanalytics.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.businessanalytics.data.ContractorRow
import com.businessanalytics.data.ContractorSummary
import com.businessanalytics.ui.theme.*

@Composable
fun ContractorsScreen(
    contractorData: List<ContractorRow>?,
    contractorResult: List<ContractorSummary>?,
    onNewFile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(UzmkLightBg)
    ) {
        // Заголовок
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "👷 Анализ подрядчиков",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Прибыльность и эффективность подрядчиков",
                fontSize = 14.sp,
                color = UzmkGrayText,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }

        if (contractorData == null || contractorResult == null) {
            // Нет данных
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FA)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "📊 Данные не загружены",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Вернитесь на главную страницу для загрузки файла",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            // Содержимое с прокруткой
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                // Кнопка и статистика
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "📊 Найдено подрядчиков: ${contractorResult.size}",
                            fontSize = 14.sp
                        )
                        val profitable = contractorResult.count { it.profit >= 0 }
                        Text(
                            text = "✅ Прибыльных: $profitable",
                            fontSize = 14.sp,
                            color = SuccessGreen
                        )
                    }
                    Button(onClick = onNewFile) {
                        Text("📁 Загрузить другой файл")
                    }
                }

                // ГРАФИКИ ПОДРЯДЧИКОВ - НОВЫЙ БЛОК
                if (contractorResult.isNotEmpty()) {
                    Text(
                        text = "📈 Визуальная аналитика",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)
                    )

                    // Контейнер для графиков
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Первый ряд: два графика по горизонтали
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // График 1: Доля в прибыли
                            ContractorProfitShareChart(
                                contractors = contractorResult,
                                title = "💰 Доля в прибыли",
                                modifier = Modifier.weight(1f)
                            )

                            // График 2: Маржа на тонну
                            ContractorMarginChart(
                                contractors = contractorResult,
                                title = "⚖️ Маржа на тонну",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Примечание под графиками
                        Text(
                            text = "💡 Графики построены на основе данных таблицы ниже",
                            fontSize = 12.sp,
                            color = UzmkGrayText,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                // ПРОСТАЯ ТАБЛИЦА
                Text(
                    text = "📋 Таблица подрядчиков",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                SimpleContractorsTable(contractors = contractorResult)

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// ПРОСТАЯ ТАБЛИЦА В ВИДЕ Column + Row
@Composable
fun SimpleContractorsTable(contractors: List<ContractorSummary>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp,
        backgroundColor = UzmkWhite
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Заголовок таблицы
            TableHeaderRow()

            // Разделитель
            Divider(color = UzmkLightBg, thickness = 1.dp)

            // Данные таблицы
            TableDataRows(contractors = contractors)

            // Итоговая строка
            Divider(color = UzmkLightBg, thickness = 1.dp, modifier = Modifier.padding(top = 8.dp))

            TableTotalRow(contractors = contractors)
        }
    }
}

@Composable
fun TableHeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        // Используйте Text с weight в Row, а не Box
        Text(
            text = "Подрядчик",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = UzmkDarkText,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1.5f)
        )
        Text(
            text = "Вес (т)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = UzmkDarkText,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Выручка",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = UzmkDarkText,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Материалы",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = UzmkDarkText,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Оплата",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = UzmkDarkText,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Транспорт",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = UzmkDarkText,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Прибыль",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = UzmkDarkText,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Доля %",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = UzmkDarkText,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(0.8f)
        )
        Text(
            text = "Маржа/т",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = UzmkDarkText,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TableDataRows(contractors: List<ContractorSummary>) {
    val totalProfit = contractors.sumOf { it.profit }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        contractors.forEach { contractor ->
            ContractorTableRow(contractor = contractor, totalProfit = totalProfit)
        }
    }
}

@Composable
fun ContractorTableRow(contractor: ContractorSummary, totalProfit: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Подрядчик
        Box(modifier = Modifier.weight(1.5f)) {
            Text(
                text = contractor.contractor.take(15) + if (contractor.contractor.length > 15) ".." else "",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = UzmkDarkText,
                textAlign = TextAlign.Start
            )
        }

        // 2. Вес
        Box(modifier = Modifier.weight(1f)) {
            Text(
                text = contractor.getFormattedWeight(),
                fontSize = 12.sp,
                color = UzmkSteel,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 3. Выручка
        Box(modifier = Modifier.weight(1f)) {
            Text(
                text = contractor.getFormattedRevenue(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = UzmkDarkText,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 4. Материалы
        Box(modifier = Modifier.weight(1f)) {
            Text(
                text = contractor.getFormattedMaterials(),
                fontSize = 12.sp,
                color = UzmkSteel,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 5. Оплата подрядчику
        Box(modifier = Modifier.weight(1f)) {
            Text(
                text = contractor.getFormattedContractorCost(),
                fontSize = 12.sp,
                color = UzmkSteel,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 6. Транспорт
        Box(modifier = Modifier.weight(1f)) {
            Text(
                text = contractor.getFormattedTransport(),
                fontSize = 12.sp,
                color = UzmkSteel,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 7. Прибыль
        Box(modifier = Modifier.weight(1f)) {
            Text(
                text = contractor.getFormattedProfit(),
                fontSize = 12.sp,
                fontWeight = if (contractor.profit >= 0) FontWeight.Bold else FontWeight.Normal,
                color = if (contractor.profit >= 0) SuccessGreen else ErrorRed,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 8. Доля в прибыли
        val profitShare = if (totalProfit > 0) (contractor.profit / totalProfit * 100) else 0.0
        Box(modifier = Modifier.weight(0.8f)) {
            Text(
                text = "%.1f%%".format(profitShare),
                fontSize = 12.sp,
                color = UzmkSilver,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 9. Маржа на тонну
        Box(modifier = Modifier.weight(1f)) {
            Text(
                text = contractor.getFormattedMarginPerTon(),
                fontSize = 12.sp,
                color = if (contractor.marginPerTon >= 0) Color(0xFF2E7D32) else Color(0xFFC62828),
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun TableTotalRow(contractors: List<ContractorSummary>) {
    val totals = calculateSimpleTotals(contractors)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
    ) {
        // ИТОГО
        Box(modifier = Modifier.weight(1.5f)) {
            Text(
                text = "ИТОГО:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = UzmkBlue,
                textAlign = TextAlign.Start
            )
        }

        // Вес
        Box(modifier = Modifier.weight(1f)) {
            Text(
                text = "%,.1f".format(totals.totalWeight),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = UzmkSteel,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Выручка
        Box(modifier = Modifier.weight(1f)) {
            Text(
                text = "%,.0f".format(totals.totalRevenue),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = UzmkBlue,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Материалы
        Box(modifier = Modifier.weight(1f)) {
            Text(
                text = "%,.0f".format(totals.totalMaterials),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = UzmkSteel,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Оплата подрядчику
        Box(modifier = Modifier.weight(1f)) {
            Text(
                text = "%,.0f".format(totals.totalContractorCost),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = UzmkSteel,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Транспорт
        Box(modifier = Modifier.weight(1f)) {
            Text(
                text = "%,.0f".format(totals.totalTransport),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = UzmkSteel,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Прибыль
        Box(modifier = Modifier.weight(1f)) {
            Text(
                text = "%,.0f".format(totals.totalProfit),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (totals.totalProfit >= 0) SuccessGreen else ErrorRed,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Доля в прибыли (всегда 100%)
        Box(modifier = Modifier.weight(0.8f)) {
            Text(
                text = "100.0%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = UzmkSilver,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Средняя маржа на тонну
        Box(modifier = Modifier.weight(1f)) {
            Text(
                text = "%,.1f".format(totals.avgMarginPerTon),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (totals.avgMarginPerTon >= 0) Color(0xFF2E7D32) else Color(0xFFC62828),
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

data class SimpleContractorTotals(
    val totalWeight: Double,
    val totalRevenue: Double,
    val totalMaterials: Double,
    val totalContractorCost: Double,
    val totalTransport: Double,
    val totalProfit: Double,
    val avgMarginPerTon: Double
)

private fun calculateSimpleTotals(data: List<ContractorSummary>): SimpleContractorTotals {
    return SimpleContractorTotals(
        totalWeight = data.sumOf { it.totalWeight },
        totalRevenue = data.sumOf { it.totalRevenue },
        totalMaterials = data.sumOf { it.totalMaterialsCost },
        totalContractorCost = data.sumOf { it.totalContractorCost },
        totalTransport = data.sumOf { it.transportCost },
        totalProfit = data.sumOf { it.profit },
        avgMarginPerTon = if (data.isNotEmpty()) data.sumOf { it.marginPerTon } / data.size else 0.0
    )
}