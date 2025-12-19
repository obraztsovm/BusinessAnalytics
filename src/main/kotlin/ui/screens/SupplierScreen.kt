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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.businessanalytics.data.SupplierRow
import com.businessanalytics.data.SupplierSummary
import com.businessanalytics.ui.components.SupplierTable
import com.businessanalytics.ui.theme.*

@Composable
fun SuppliersScreen(
    supplierData: List<SupplierRow>?,
    supplierResult: List<SupplierSummary>?,
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
                text = "🏭 Анализ поставщиков материалов",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Стоимость материалов, объемы поставок и средние цены",
                fontSize = 14.sp,
                color = UzmkGrayText,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }

        if (supplierData == null || supplierResult == null) {
            // Нет данных
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FA)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "📊 Данные поставщиков не загружены",
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
                            text = "📊 Найдено поставщиков: ${supplierResult.size}",
                            fontSize = 14.sp
                        )
                        val activeSuppliers = supplierResult.count { it.totalCost > 0 }
                        Text(
                            text = "✅ Активных поставщиков: $activeSuppliers",
                            fontSize = 14.sp,
                            color = SuccessGreen
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
                    MetricCard(
                        title = "Общий вес",
                        value = "%,.1f т".format(supplierResult.sumOf { it.totalWeight }),
                        icon = "⚖️",
                        color = UzmkBlue
                    )
                    MetricCard(
                        title = "Общая стоимость",
                        value = "%,.0f руб".format(supplierResult.sumOf { it.totalCost }),
                        icon = "💰",
                        color = UzmkGold
                    )
                    MetricCard(
                        title = "Средняя цена/т",
                        value = {
                            val totalWeight = supplierResult.sumOf { it.totalWeight }
                            val totalCost = supplierResult.sumOf { it.totalCost }
                            if (totalWeight > 0) "%,.0f руб/т".format(totalCost / totalWeight)
                            else "0 руб/т"
                        },
                        icon = "📈",
                        color = SuccessGreen
                    )
                }

                // ТАБЛИЦА ПОСТАВЩИКОВ
                Text(
                    text = "📋 Таблица поставщиков материалов",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (supplierResult.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏭 Нет данных по поставщикам материалов")
                    }
                } else {
                    // Обертка для таблицы
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 300.dp, max = 500.dp)
                    ) {
                        SupplierTable(supplierResult = supplierResult)
                    }
                }

                // Информация о данных
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
                            text = "📝 Примечания по данным",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "• Вес материалов пока не указан в исходных данных",
                            fontSize = 12.sp,
                            color = UzmkGrayText
                        )
                        Text(
                            text = "• При появлении данных о тоннаже расчеты обновятся автоматически",
                            fontSize = 12.sp,
                            color = UzmkGrayText,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = "• Столбцы: АА (дата), У (поставщик), АС (стоимость)",
                            fontSize = 12.sp,
                            color = UzmkGrayText,
                            modifier = Modifier.padding(top = 4.dp)
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
fun MetricCard(
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
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: () -> String,
    icon: String,
    color: Color
) {
    MetricCard(
        title = title,
        value = value(),
        icon = icon,
        color = color
    )
}