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
import com.businessanalytics.ui.screens.QualityControlScreen
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

    // Существующие данные
    var excelData by remember { mutableStateOf<List<ExcelRow>?>(null) }
    var analysisResult by remember { mutableStateOf<List<ClientSummary>?>(null) }
    var transportData by remember { mutableStateOf<List<TransportRow>?>(null) }
    var transportResult by remember { mutableStateOf<List<TransportSummary>?>(null) }
    var contractorData by remember { mutableStateOf<List<ContractorRow>?>(null) }
    var contractorResult by remember { mutableStateOf<List<ContractorSummary>?>(null) }
    var supplierData by remember { mutableStateOf<List<SupplierRow>?>(null) }
    var supplierResult by remember { mutableStateOf<List<SupplierSummary>?>(null) }

    // НОВЫЕ ДАННЫЕ ОТК
    var qcData by remember { mutableStateOf<List<QualityControlRow>?>(null) }
    var qcResult by remember { mutableStateOf<List<QualityControlSummary>?>(null) }

    // Сервисы
    val analysisService = remember { AnalysisService() }
    val transportAnalysisService = remember { TransportAnalysisService() }
    val contractorAnalysisService = remember { ContractorAnalysisService() }
    val supplierAnalysisService = remember { SupplierAnalysisService() }
    val qualityControlAnalysisService = remember { QualityControlAnalysisService() }
    val excelReader = remember { ExcelReader() }

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
                        onNewFile = ::resetAllData
                    )
                    "Подрядчики" -> ContractorsScreen(
                        contractorData = contractorData,
                        contractorResult = contractorResult,
                        onNewFile = ::resetAllData
                    )
                    "Поставщики" -> SuppliersScreen(
                        supplierData = supplierData,
                        supplierResult = supplierResult,
                        onNewFile = ::resetAllData
                    )
                    "Качество" -> QualityControlScreen( // НОВЫЙ ЭКРАН
                        qcData = qcData,
                        qcResult = qcResult,
                        onNewFile = ::resetAllData
                    )
                    "Главная" -> MainContent(
                        onFileSelected = { file ->
                            loadAllData(
                                file = file,
                                excelReader = excelReader,
                                analysisService = analysisService,
                                transportAnalysisService = transportAnalysisService,
                                contractorAnalysisService = contractorAnalysisService,
                                supplierAnalysisService = supplierAnalysisService,
                                qualityControlAnalysisService = qualityControlAnalysisService,
                                onDataLoaded = {
                                    excelData = it.excelData
                                    analysisResult = it.analysisResult
                                    transportData = it.transportData
                                    transportResult = it.transportResult
                                    contractorData = it.contractorData
                                    contractorResult = it.contractorResult
                                    supplierData = it.supplierData
                                    supplierResult = it.supplierResult
                                    qcData = it.qcData
                                    qcResult = it.qcResult
                                },
                                onError = { error ->
                                    println("❌ Ошибка загрузки: $error")
                                }
                            )
                            selectedScreen = "Сводка"
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

// Функция сброса всех данных
private fun resetAllData() {
    // В реальном коде это будет в ViewModel
    println("🔄 Сброс всех данных")
}

// Функция загрузки всех данных
private fun loadAllData(
    file: File,
    excelReader: ExcelReader,
    analysisService: AnalysisService,
    transportAnalysisService: TransportAnalysisService,
    contractorAnalysisService: ContractorAnalysisService,
    supplierAnalysisService: SupplierAnalysisService,
    qualityControlAnalysisService: QualityControlAnalysisService,
    onDataLoaded: (AllData) -> Unit,
    onError: (String) -> Unit
) {
    try {
        println("📂 Начало загрузки файла: ${file.name}")

        // 1. Чтение всех данных
        val excelData = excelReader.readExcelFile(file)
        val transportData = excelReader.readTransportData(file)
        val contractorData = excelReader.readContractorData(file)
        val supplierData = excelReader.readSupplierData(file)
        val qcData = excelReader.readQualityControlData(file)

        println("✅ Данные прочитаны:")
        println("   Клиенты: ${excelData.size} строк")
        println("   Транспорт: ${transportData.size} строк")
        println("   Подрядчики: ${contractorData.size} строк")
        println("   Поставщики: ${supplierData.size} строк")
        println("   ОТК: ${qcData.size} строк")

        // 2. Анализ данных
        val endDate = LocalDateTime.now()
        val startDate = endDate.minusDays(30)

        val analysisResult = analysisService.analyzeClientData(excelData, startDate, endDate)
        val transportResult = transportAnalysisService.analyzeTransportData(transportData, startDate, endDate)
        val contractorResult = contractorAnalysisService.analyzeContractors(contractorData, startDate, endDate)
        val supplierResult = supplierAnalysisService.analyzeSuppliers(supplierData, startDate, endDate)
        val qcResult = qualityControlAnalysisService.analyzeQualityControl(qcData, startDate, endDate)

        println("✅ Анализ завершен:")
        println("   Клиентов: ${analysisResult.size}")
        println("   Транспортных компаний: ${transportResult.size}")
        println("   Подрядчиков: ${contractorResult.size}")
        println("   Поставщиков: ${supplierResult.size}")
        println("   Сотрудников ОТК: ${qcResult.size}")

        // 3. Возврат результатов
        onDataLoaded(
            AllData(
                excelData = excelData,
                analysisResult = analysisResult,
                transportData = transportData,
                transportResult = transportResult,
                contractorData = contractorData,
                contractorResult = contractorResult,
                supplierData = supplierData,
                supplierResult = supplierResult,
                qcData = qcData,
                qcResult = qcResult
            )
        )

    } catch (e: Exception) {
        onError("Ошибка загрузки: ${e.message}")
        e.printStackTrace()
    }
}

// Класс для хранения всех данных
data class AllData(
    val excelData: List<ExcelRow>,
    val analysisResult: List<ClientSummary>,
    val transportData: List<TransportRow>,
    val transportResult: List<TransportSummary>,
    val contractorData: List<ContractorRow>,
    val contractorResult: List<ContractorSummary>,
    val supplierData: List<SupplierRow>,
    val supplierResult: List<SupplierSummary>,
    val qcData: List<QualityControlRow>,
    val qcResult: List<QualityControlSummary>
)