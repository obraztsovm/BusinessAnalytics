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
import com.businessanalytics.ui.components.ClientBarChart
import com.businessanalytics.ui.components.KPICards
import com.businessanalytics.ui.components.ShipmentPaymentChart
import com.businessanalytics.ui.components.SimpleTable
import com.businessanalytics.ui.components.TransportTable
import com.businessanalytics.ui.theme.*

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
            .background(UzmkLightBg)
            .verticalScroll(rememberScrollState())
    ) {
        // Верхняя часть с заголовком
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "📊 Сводка по клиентам и транспортным услугам",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = UzmkDarkText,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Ключевые показатели и аналитика",
                fontSize = 14.sp,
                color = UzmkGrayText,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }

        if (excelData == null || analysisResult == null || transportResult == null) {
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
            // 1. СНАЧАЛА ТАБЛИЦА (она должна быть ПЕРВОЙ)
            SimpleTable(analysisResult = analysisResult)

            Spacer(modifier = Modifier.height(24.dp))

            // 2. ЗАТЕМ ГРАФИКИ
            AwesomeChartsPanel(
                clientSummaries = analysisResult,
                transportSummaries = transportResult,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. ТРАНСПОРТНАЯ ТАБЛИЦА
            if (transportResult.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "🚚 Транспортные услуги",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = UzmkDarkText,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Используйте ваш TransportTable или создайте простую
                    TransportTable(transportResult = transportResult)
                }
            }

            // 4. КНОПКА ЗАГРУЗКИ НОВОГО ФАЙЛА
            Button(
                onClick = onNewFile,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = UzmkBlue,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("📁 Загрузить другой файл")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

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
                Text(
                    text = "📊 Найдено клиентов: ${analysisResult.size}",
                    fontSize = 14.sp,
                    color = UzmkDarkText
                )
                Text(
                    text = "🚚 Транспортных компаний: ${transportResult?.size ?: 0}",
                    fontSize = 14.sp,
                    color = UzmkGrayText
                )
            }
            Button(
                onClick = onNewFile,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = UzmkBlue,
                    contentColor = UzmkWhite
                )
            ) {
                Text("📁 Загрузить другой файл")
            }
        }

        // ТАБЛИЦА КЛИЕНТОВ
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "📈 Аналитика по клиентам",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = UzmkDarkText,
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
                    Text("Нет данных по клиентам", color = UzmkGrayText)
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
                color = UzmkDarkText,
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
                    Text("🚛 Нет данных по транспортным услугам", color = UzmkGrayText)
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