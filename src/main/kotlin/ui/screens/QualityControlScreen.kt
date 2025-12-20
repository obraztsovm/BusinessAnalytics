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
import com.businessanalytics.data.QualityControlRow
import com.businessanalytics.data.QualityControlSummary
import com.businessanalytics.ui.components.QualityControlTable
import com.businessanalytics.ui.theme.*

@Composable
fun QualityControlScreen(
    qcData: List<QualityControlRow>?,
    qcResult: List<QualityControlSummary>?,
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
                text = "🔬 Контроль качества (ОТК)",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Эффективность работы сотрудников отдела технического контроля",
                fontSize = 14.sp,
                color = UzmkGrayText,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }

        if (qcData == null || qcResult == null) {
            // Нет данных
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FA)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "📊 Данные ОТК не загружены",
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
                            text = "👥 Сотрудников ОТК: ${qcResult.size}",
                            fontSize = 14.sp
                        )
                        Text(
                            text = "📅 Записей проверок: ${qcData.size}",
                            fontSize = 14.sp,
                            color = UzmkGrayText
                        )
                    }
                    Button(onClick = onNewFile) {
                        Text("📁 Загрузить другой файл")
                    }
                }

                // Карточки с ключевыми метриками
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    QCMetricCard(
                        title = "Общий проверенный вес",
                        value = "%,.1f т".format(qcResult.sumOf { it.totalWeight }),
                        icon = "⚖️",
                        color = Color(0xFF7B1FA2)
                    )
                    QCMetricCard(
                        title = "Общая стоимость",
                        value = "%,.0f руб".format(qcResult.sumOf { it.totalValue }),
                        icon = "💰",
                        color = Color(0xFF9C27B0)
                    )
                    QCMetricCard(
                        title = "Средняя эффективность",
                        value = {
                            val totalWeight = qcResult.sumOf { it.totalWeight }
                            val totalValue = qcResult.sumOf { it.totalValue }
                            if (totalWeight > 0) "%,.0f руб/т".format(totalValue / totalWeight)
                            else "0 руб/т"
                        },
                        icon = "📈",
                        color = UzmkGold
                    )
                }

                // ТАБЛИЦА ОТК
                Text(
                    text = "📋 Эффективность сотрудников ОТК",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (qcResult.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(Color(0xFFF3E5F5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔬 Нет данных по контролю качества")
                    }
                } else {
                    // Обертка для таблицы
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 300.dp, max = 500.dp)
                    ) {
                        QualityControlTable(qcResult = qcResult)
                    }
                }

                // Дополнительная аналитика
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp, bottom = 32.dp),
                    elevation = 2.dp,
                    backgroundColor = Color(0xFFF5F5F5)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "📊 Анализ эффективности",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Топ-3 сотрудника
                        val topEmployees = qcResult.sortedByDescending { it.totalWeight }.take(3)
                        if (topEmployees.isNotEmpty()) {
                            Text(
                                text = "🏆 Лучшие сотрудники по объему проверок:",
                                fontSize = 12.sp,
                                color = UzmkGrayText,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            topEmployees.forEachIndexed { index, employee ->
                                Text(
                                    text = "${index + 1}. ${employee.employeeName} - ${employee.getFormattedWeight()} т",
                                    fontSize = 12.sp,
                                    color = UzmkDarkText,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Используемые столбцы
                        Text(
                            text = "📝 Используемые столбцы:",
                            fontSize = 12.sp,
                            color = UzmkGrayText
                        )
                        Text(
                            text = "• AW - дата проверки",
                            fontSize = 11.sp,
                            color = UzmkGrayText
                        )
                        Text(
                            text = "• AS - сотрудник ОТК",
                            fontSize = 11.sp,
                            color = UzmkGrayText
                        )
                        Text(
                            text = "• J - вес продукции (т)",
                            fontSize = 11.sp,
                            color = UzmkGrayText
                        )
                        Text(
                            text = "• BA - стоимость продукции (руб)",
                            fontSize = 11.sp,
                            color = UzmkGrayText
                        )
                    }
                }

                // Отступ в конце
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun QCMetricCard(
    title: String,
    value: String,
    icon: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp,
        backgroundColor = UzmkWhite
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = UzmkGrayText,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun QCMetricCard(
    title: String,
    value: () -> String,
    icon: String,
    color: Color
) {
    QCMetricCard(
        title = title,
        value = value(),
        icon = icon,
        color = color
    )
}