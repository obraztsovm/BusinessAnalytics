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
import com.businessanalytics.data.ContractorRow
import com.businessanalytics.data.ContractorSummary

@Composable
fun ContractorsScreen(
    contractorData: List<ContractorRow>?,
    contractorResult: List<ContractorSummary>?,
    onNewFile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "👷 Анализ подрядчиков",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

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
            // Заголовок с кнопкой
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📈 Анализ прибыльности подрядчиков",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                    Text(
                        text = "Найдено подрядчиков: ${contractorResult.size}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                Button(onClick = onNewFile) {
                    Text("📁 Загрузить другой файл")
                }
            }

            // ПРОСТАЯ ТАБЛИЦА (пока заглушка)
            if (contractorResult.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 4.dp,
                    backgroundColor = Color.White
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Заголовок таблицы
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Таблица подрядчиков",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "${contractorResult.size} записей",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Данные таблицы
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            contractorResult.forEach { contractor ->
                                ContractorCard(contractor = contractor)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContractorCard(contractor: ContractorSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp,
        backgroundColor = if (contractor.profit >= 0) Color(0xFFF1F8E9) else Color(0xFFFFEBEE)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = contractor.contractor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF212121)
                )
                Text(
                    text = "Вес: ${contractor.getFormattedWeight()} т | Выручка: ${contractor.getFormattedRevenue()} руб",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = contractor.getFormattedProfit(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (contractor.profit >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
                Text(
                    text = "Маржа: ${contractor.getFormattedMarginPercentage()}",
                    fontSize = 11.sp,
                    color = if (contractor.profit >= 0) Color(0xFF558B2F) else Color(0xFFD32F2F)
                )
            }
        }
    }
}