package com.businessanalytics.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.businessanalytics.data.ClientSummary
import com.businessanalytics.data.ExcelRow
import com.businessanalytics.data.TransportSummary
import com.businessanalytics.ui.components.SimpleTable
import com.businessanalytics.ui.components.TransportTable

@Composable
fun SummaryScreen(
    excelData: List<ExcelRow>?,
    analysisResult: List<ClientSummary>?,
    transportResult: List<TransportSummary>?,
    onNewFile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Заголовок
        Text(
            text = "Сводка по клиентам и транспортным услугам",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        if (excelData == null || analysisResult == null) {
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
            // Показываем аналитику
            AnalysisResults(
                analysisResult = analysisResult,
                transportResult = transportResult,
                onNewFile = onNewFile
            )
        }
    }
}

@Composable
fun AnalysisResults(
    analysisResult: List<ClientSummary>,
    transportResult: List<TransportSummary>?,
    onNewFile: () -> Unit
) {
    println("🔍 AnalysisResults вызван: клиентов=${analysisResult.size}, транспортных=${transportResult?.size}")

    Column(modifier = Modifier.fillMaxSize()) {
        // Статистика и кнопка (без изменений)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("📊 Найдено клиентов: ${analysisResult.size}", fontSize = 14.sp)
                Text("🚚 Транспортных компаний: ${transportResult?.size ?: 0}", fontSize = 14.sp)
            }
            Button(onClick = onNewFile) {
                Text("📁 Загрузить другой файл")
            }
        }

        // ПЕРВАЯ ТАБЛИЦА КЛИЕНТОВ С ВЕСОМ
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "📈 Аналитика по клиентам",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (analysisResult.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Нет данных по клиентам")
                }
            } else {
                SimpleTable(analysisResult = analysisResult)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ВТОРАЯ ТАБЛИЦА ТРАНСПОРТА С ВЕСОМ
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "🚚 Транспортные услуги",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (transportResult.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color(0xFFFFF3E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🚛 Нет данных по транспортным услугам")
                }
            } else {
                TransportTable(transportResult = transportResult)
            }
        }
    }
}