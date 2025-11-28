package com.businessanalytics.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import com.businessanalytics.data.TransportRow // ДОБАВЬ ЭТОТ ИМПОРТ
import com.businessanalytics.data.TransportSummary
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import com.businessanalytics.services.TransportAnalysisService
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.businessanalytics.ui.components.SidePanel
import com.businessanalytics.ui.screens.SummaryScreen
import com.businessanalytics.ui.components.FileDropZone
import com.businessanalytics.data.ExcelRow
import com.businessanalytics.data.ClientSummary
import com.businessanalytics.services.AnalysisService
import com.businessanalytics.services.ExcelReader
import java.io.File
import java.time.LocalDateTime

@Composable
fun MainScreen() {
    var selectedScreen by remember { mutableStateOf("Главная") }
    var excelData by remember { mutableStateOf<List<ExcelRow>?>(null) }
    var analysisResult by remember { mutableStateOf<List<ClientSummary>?>(null) }
    var transportData by remember { mutableStateOf<List<TransportRow>?>(null) } // РАСКОММЕНТИРУЕМ
    var transportResult by remember { mutableStateOf<List<TransportSummary>?>(null) } // РАСКОММЕНТИРУЕМ

    val analysisService = remember { AnalysisService() }
    val transportAnalysisService = remember { TransportAnalysisService() } // РАСКОММЕНТИРУЕМ
    val excelReader = remember { ExcelReader() }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Боковая панель
        SidePanel(
            modifier = Modifier
                .width(250.dp)
                .fillMaxHeight(),
            onItemSelected = { screen -> selectedScreen = screen }
        )

        // Основная область контента
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            when (selectedScreen) {
                "Сводка" -> SummaryScreen(
                    excelData = excelData,
                    analysisResult = analysisResult,
                    transportResult = transportResult, // ПЕРЕДАЕМ РЕАЛЬНЫЕ ДАННЫЕ
                    onNewFile = {
                        excelData = null
                        analysisResult = null
                        transportData = null // РАСКОММЕНТИРУЕМ
                        transportResult = null // РАСКОММЕНТИРУЕМ
                    }
                )
                "Главная" -> MainContent(
                    onFileSelected = { file ->
                        try {
                            // Читаем основные данные
                            val data = excelReader.readExcelFile(file)
                            excelData = data

                            // Читаем транспортные данные - РАСКОММЕНТИРУЕМ
                            val transportDataRead = excelReader.readTransportData(file) // ИЗМЕНЯЕМ ИМЯ ПЕРЕМЕННОЙ
                            transportData = transportDataRead

                            // Анализируем основные данные
                            val endDate = LocalDateTime.now()
                            val startDate = endDate.minusDays(30)
                            val result = analysisService.analyzeClientData(data, startDate, endDate)
                            analysisResult = result

                            // Анализируем транспортные данные - РАСКОММЕНТИРУЕМ
                            val transportResultAnalyzed = transportAnalysisService.analyzeTransportData(
                                transportDataRead, // ИСПОЛЬЗУЕМ transportDataRead
                                startDate,
                                endDate
                            )
                            transportResult = transportResultAnalyzed

                            println("✅ Основные данные: ${result.size} клиентов")
                            println("✅ Транспортные данные: ${transportResultAnalyzed.size} компаний")

                            // Автоматически переключаемся на сводку
                            selectedScreen = "Сводка"
                        } catch (e: Exception) {
                            println("❌ Ошибка при загрузке файла: ${e.message}")
                            e.printStackTrace()
                        }
                    },
                    hasData = excelData != null
                )
                else -> DefaultContent(selectedScreen)
            }
        }
    }
}

@Composable
fun MainContent(
    onFileSelected: (File) -> Unit,
    hasData: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Приветствие
        Text(
            text = "Добро пожаловать в Business Analytics!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C3E50),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Аналитика бизнес-показателей в одном месте",
            fontSize = 16.sp,
            color = Color(0xFF7F8C8D),
            modifier = Modifier.padding(bottom = 40.dp)
        )

        if (hasData) {
            // Если данные уже загружены
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8F5E9))
                    .padding(24.dp)
                    .border(2.dp, Color(0xFF4CAF50), androidx.compose.foundation.shape.RoundedCornerShape(12.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "✅ Данные загружены!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Перейдите в раздел 'Сводка' для просмотра аналитики",
                    fontSize = 14.sp,
                    color = Color(0xFF424242),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Drag and Drop зона
        FileDropZone(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            onFileSelected = onFileSelected
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Быстрый старт
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE3F2FD))
                .padding(20.dp)
                .border(1.dp, Color(0xFF90CAF9), androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
        ) {
            Text(
                text = "🚀 Быстрый старт",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StepItem("1", "Загрузите Excel файл", "Перетащите файл в зону выше")
                StepItem("2", "Перейдите в 'Сводка'", "Автоматический переход после загрузки")
                StepItem("3", "Анализируйте данные", "Смотрите статистику по клиентам")
            }
        }
    }
}

@Composable
fun StepItem(number: String, title: String, description: String) {
    Column(
        modifier = Modifier.width(180.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF2196F3), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF424242),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = description,
            fontSize = 12.sp,
            color = Color(0xFF757575),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun DefaultContent(screenName: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Экран: $screenName\n(в разработке)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}