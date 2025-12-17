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
            .background(Color(0xFFF8F9FA))
    ) {
        // Верхняя часть с заголовком
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Сводка по клиентам и транспортным услугам",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }

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
            // Содержимое с прокруткой
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                // Панель графиков
                analysisResult.let { clients ->
                    transportResult?.let { transport ->
                        if (clients.isNotEmpty() && transport.isNotEmpty()) {
                            AwesomeChartsPanel(
                                clientSummaries = clients,
                                transportSummaries = transport,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 32.dp)
                            )
                        }
                    }
                }

                // Показываем аналитику
                AnalysisResults(
                    analysisResult = analysisResult,
                    transportResult = transportResult,
                    onNewFile = onNewFile,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// ФУНКЦИЯ AnalysisResults ДОЛЖНА БЫТЬ В ЭТОМ ЖЕ ФАЙЛЕ
@Composable
fun AnalysisResults(
    analysisResult: List<ClientSummary>,
    transportResult: List<TransportSummary>?,
    onNewFile: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Статистика и кнопка
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
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

        // ТАБЛИЦА КЛИЕНТОВ
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "📈 Аналитика по клиентам",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
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
                // Обертка для таблицы с фиксированной высотой
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 300.dp, max = 500.dp)
                ) {
                    SimpleTable(analysisResult = analysisResult)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ТАБЛИЦА ТРАНСПОРТА
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "🚚 Транспортные услуги",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
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
                // Обертка для таблицы с фиксированной высотой
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 300.dp, max = 500.dp)
                ) {
                    TransportTable(transportResult = transportResult)
                }
            }
        }

        // Отступ в конце для удобства прокрутки
        Spacer(modifier = Modifier.height(32.dp))
    }
}