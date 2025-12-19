package com.businessanalytics.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.businessanalytics.data.*
import com.businessanalytics.services.*
import com.businessanalytics.ui.components.FileDropZone
import com.businessanalytics.ui.components.SidePanel
import com.businessanalytics.ui.screens.ContractorsScreen
import com.businessanalytics.ui.screens.SummaryScreen
import com.businessanalytics.ui.screens.SuppliersScreen
import com.businessanalytics.ui.theme.*
import java.io.File
import java.time.LocalDateTime

@Composable
fun MainContent(
    onFileSelected: (File) -> Unit,
    hasData: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Text(
            text = "УЗСМК Аналитика",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = UzmkDarkText,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Панель управления бизнес-показателями",
            fontSize = 16.sp,
            color = UzmkGrayText,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        if (hasData) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                elevation = 8.dp,
                backgroundColor = UzmkWhite
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(UzmkGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✓",
                            color = UzmkWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Данные загружены",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = UzmkDarkText
                        )
                        Text(
                            text = "Перейдите в раздел «Сводка» для анализа",
                            fontSize = 14.sp,
                            color = UzmkGrayText
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            elevation = 8.dp,
            backgroundColor = UzmkWhite
        ) {
            FileDropZone(
                modifier = Modifier.fillMaxSize(),
                onFileSelected = onFileSelected
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Быстрый старт",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = UzmkDarkText,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StepCard("1", "Загрузите файл", "Excel файл с данными")
            StepCard("2", "Анализируйте", "Автоматический переход в сводку")
            StepCard("3", "Изучайте метрики", "Детальная аналитика по разделам")
        }
    }
}

@Composable
fun StepCard(number: String, title: String, description: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = 4.dp,
        backgroundColor = UzmkWhite
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(UzmkBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number,
                    color = UzmkWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = UzmkDarkText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                fontSize = 12.sp,
                color = UzmkGrayText,
                textAlign = TextAlign.Center
            )
        }
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
            color = UzmkGrayText,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MainScreen() {
    var selectedScreen by remember { mutableStateOf("Главная") }
    var excelData by remember { mutableStateOf<List<ExcelRow>?>(null) }
    var analysisResult by remember { mutableStateOf<List<ClientSummary>?>(null) }
    var transportData by remember { mutableStateOf<List<TransportRow>?>(null) }
    var transportResult by remember { mutableStateOf<List<TransportSummary>?>(null) }
    var contractorData by remember { mutableStateOf<List<ContractorRow>?>(null) }
    var contractorResult by remember { mutableStateOf<List<ContractorSummary>?>(null) }

    // ДОБАВЛЯЕМ ДАННЫЕ ПОСТАВЩИКОВ
    var supplierData by remember { mutableStateOf<List<SupplierRow>?>(null) }
    var supplierResult by remember { mutableStateOf<List<SupplierSummary>?>(null) }

    val analysisService = remember { AnalysisService() }
    val transportAnalysisService = remember { TransportAnalysisService() }
    val contractorAnalysisService = remember { ContractorAnalysisService() }
    val excelReader = remember { ExcelReader() }

    // ДОБАВЛЯЕМ СЕРВИС ПОСТАВЩИКОВ
    val supplierAnalysisService = remember { SupplierAnalysisService() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(UzmkLightBg)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            SidePanel(
                modifier = Modifier
                    .width(250.dp)
                    .fillMaxHeight(),
                selectedScreen = selectedScreen,
                onItemSelected = { screen -> selectedScreen = screen }
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(UzmkLightBg)
            ) {
                when (selectedScreen) {
                    "Сводка" -> SummaryScreen(
                        excelData = excelData,
                        analysisResult = analysisResult,
                        transportResult = transportResult,
                        onNewFile = {
                            // Очистка всех данных
                            excelData = null
                            analysisResult = null
                            transportData = null
                            transportResult = null
                            contractorData = null
                            contractorResult = null
                            supplierData = null // Очищаем данные поставщиков
                            supplierResult = null
                        }
                    )
                    "Поставщики" -> SuppliersScreen( // НОВЫЙ ЭКРАН
                        supplierData = supplierData,
                        supplierResult = supplierResult,
                        onNewFile = {
                            // Очистка всех данных
                            excelData = null
                            analysisResult = null
                            transportData = null
                            transportResult = null
                            contractorData = null
                            contractorResult = null
                            supplierData = null
                            supplierResult = null
                        }
                    )
                    "Главная" -> MainContent(
                        onFileSelected = { file ->
                            try {
                                // 1. Читаем основные данные
                                val data = excelReader.readExcelFile(file)
                                excelData = data

                                // 2. Читаем транспортные данные
                                val transportDataRead = excelReader.readTransportData(file)
                                transportData = transportDataRead

                                // 3. Читаем данные подрядчиков
                                val contractorDataRead = excelReader.readContractorData(file)
                                contractorData = contractorDataRead

                                // 4. Читаем данные поставщиков (НОВОЕ)
                                val supplierDataRead = excelReader.readSupplierData(file)
                                supplierData = supplierDataRead
                                println("🏭 Прочитано данных поставщиков: ${supplierDataRead.size} строк")

                                // 5. Анализируем данные
                                val endDate = LocalDateTime.now()
                                val startDate = endDate.minusDays(30)

                                // Анализ клиентов
                                val result = analysisService.analyzeClientData(data, startDate, endDate)
                                analysisResult = result

                                // Анализ транспорта
                                val transportResultAnalyzed = transportAnalysisService.analyzeTransportData(
                                    transportDataRead,
                                    startDate,
                                    endDate
                                )
                                transportResult = transportResultAnalyzed

                                // Анализ подрядчиков
                                val contractorResultAnalyzed = contractorAnalysisService.analyzeContractors(
                                    contractorDataRead,
                                    startDate,
                                    endDate
                                )
                                contractorResult = contractorResultAnalyzed

                                // Анализ поставщиков (НОВОЕ)
                                val supplierResultAnalyzed = supplierAnalysisService.analyzeSuppliers(
                                    supplierDataRead,
                                    startDate,
                                    endDate
                                )
                                supplierResult = supplierResultAnalyzed

                                // Логи
                                println("==================================")
                                println("✅ Основные данные: ${result.size} клиентов")
                                println("✅ Транспортные данные: ${transportResultAnalyzed.size} компаний")
                                println("✅ Данные подрядчиков: ${contractorResultAnalyzed.size} подрядчиков")
                                println("✅ Данные поставщиков: ${supplierResultAnalyzed.size} поставщиков")
                                println("==================================")

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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = "by Developer",
                color = UzmkDarkText.copy(alpha = 0.3f),
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic
            )
        }
    }
}