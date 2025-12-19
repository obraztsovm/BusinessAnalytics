package com.businessanalytics.services

import com.businessanalytics.data.ContractorRow
import com.businessanalytics.data.ExcelRow
import com.businessanalytics.data.TransportRow
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId

class ExcelReader {

    fun readExcelFile(file: File): List<ExcelRow> {
        val rows = mutableListOf<ExcelRow>()

        println("🔍 Начинаем чтение файла: ${file.name}")

        file.inputStream().use { inputStream ->
            WorkbookFactory.create(inputStream).use { workbook ->
                val sheet = workbook.getSheetAt(0)
                println("📊 Лист: '${sheet.sheetName}', всего строк: ${sheet.lastRowNum + 1}")

                // ОТЛАДКА: выведем заголовки первых 5 строк для проверки
                println("📋 Заголовки первых 5 строк (начиная с 0):")
                for (i in 0..4) {
                    val testRow = sheet.getRow(i)
                    if (testRow != null) {
                        println("  Строка $i: первые 3 ячейки: '${
                            getCellValueAsString(testRow.getCell(0))}' | '${
                            getCellValueAsString(testRow.getCell(1))}' | '${
                            getCellValueAsString(testRow.getCell(2))}'")
                    }
                }

                // ИЗМЕНЕНИЕ 1: начинаем с 4-ой строки (индекс 3, т.к. 0-based)
                for (i in 3..sheet.lastRowNum) {
                    val row = sheet.getRow(i) ?: continue

                    try {
                        // ИЗМЕНЕНИЕ 2: добавим отладку для первых строк
                        if (i <= 7) { // покажем первые 5 строк данных (с 4 по 8)
                            println("🔍 Строка ${i+1} (индекс $i): клиент='${
                                getCellValueAsString(row.getCell(14))}', сумма=${
                                getCellValueAsDouble(row.getCell(52))}'")
                        }

                        // Читаем данные из нужных столбцов (индексы остаются прежними)
                        val client = getCellValueAsString(row.getCell(14)) // Столбец O (индекс 14)
                        val amount = getCellValueAsDouble(row.getCell(52)) // Столбец BA (индекс 52)
                        val weight = getCellValueAsDouble(row.getCell(53)) // Столбец BB (индекс 53)

                        // Пропускаем полностью пустые строки
                        if (client.isBlank() && amount == 0.0 && weight == 0.0) {
                            continue
                        }

                        val excelRow = ExcelRow(
                            time = LocalDateTime.now(),
                            client = if (client.isBlank()) "Неизвестный клиент" else client,
                            shipmentAmount = amount,
                            shipmentWeight = weight,
                            paymentDate = null,
                            paymentAmount = amount
                        )

                        if (excelRow.isValid()) {
                            rows.add(excelRow)
                            if (rows.size <= 3) {
                                println("✅ Добавлена строка ${i + 1}: '$client' - $amount руб, $weight т")
                            }
                        }
                    } catch (e: Exception) {
                        println("❌ Ошибка в строке ${i + 1}: ${e.message}")
                    }
                }
            }
        }

        println("📈 Успешно прочитано строк (начиная с 4-ой): ${rows.size}")
        return rows
    }

    fun readTransportData(file: File): List<TransportRow> {
        val rows = mutableListOf<TransportRow>()

        println("🚚 Чтение транспортных данных из файла: ${file.name}")

        file.inputStream().use { inputStream ->
            WorkbookFactory.create(inputStream).use { workbook ->
                val sheet = workbook.getSheetAt(0)

                // ИЗМЕНЕНИЕ: начинаем с 4-ой строки
                for (rowNum in 3..sheet.lastRowNum) {
                    val row = sheet.getRow(rowNum) ?: continue

                    // Старая логика с for (row in sheet) и if (row.rowNum == 0) удаляется

                    try {
                        // ИЗМЕНЕНИЕ: добавим отладку
                        if (rowNum <= 7) {
                            println("🚚 Строка ${rowNum+1}: компания='${
                                getCellValueAsString(row.getCell(42))}', стоимость=${
                                getCellValueAsDouble(row.getCell(39))}'")
                        }

                        val date = getCellValueAsDateTime(row.getCell(48))
                        val company = getCellValueAsString(row.getCell(42))
                        val cost = getCellValueAsDouble(row.getCell(39))
                        val weight = getCellValueAsDouble(row.getCell(9))
                        val vehicle = getCellValueAsString(row.getCell(100))

                        // Пропускаем пустые строки
                        if (company.isBlank() && cost == 0.0 && weight == 0.0) {
                            continue
                        }

                        val transportRow = TransportRow(
                            date = date ?: LocalDateTime.now(),
                            transportCompany = company.ifBlank { "Неизвестная компания" },
                            cost = cost,
                            weight = weight,
                            vehicle = vehicle.ifBlank { "Неизвестное ТС" }
                        )

                        if (transportRow.isValid()) {
                            rows.add(transportRow)
                            if (rows.size <= 3) {
                                println("✅ Добавлена транспортная строка ${rowNum + 1}: '$company' - $cost руб, $weight т")
                            }
                        }
                    } catch (e: Exception) {
                        println("❌ Ошибка в транспортной строке ${rowNum + 1}: ${e.message}")
                    }
                }
            }
        }

        println("📊 Успешно прочитано транспортных строк (начиная с 4-ой): ${rows.size}")
        return rows
    }

    fun readContractorData(file: File): List<ContractorRow> {
        val rows = mutableListOf<ContractorRow>()

        println("👷 Чтение данных по подрядчикам из файла: ${file.name}")

        file.inputStream().use { inputStream ->
            WorkbookFactory.create(inputStream).use { workbook ->
                val sheet = workbook.getSheetAt(0)
                println("📊 Лист: '${sheet.sheetName}', всего строк: ${sheet.lastRowNum + 1}")

                // ОТЛАДКА: проверим индексы столбцов
                if (sheet.lastRowNum >= 3) {
                    val testRow = sheet.getRow(3)
                    if (testRow != null) {
                        println("🔍 Тест строки 4 (индексы столбцов 0-based):")
                        println("  BI(61?)='${getCellValueAsString(testRow.getCell(61))}' - дата/время")
                        println("  AD(29?)='${getCellValueAsString(testRow.getCell(29))}' - подрядчик")
                        println("  J(9?)='${getCellValueAsDouble(testRow.getCell(9))}' - вес")
                        println("  BA(52?)='${getCellValueAsDouble(testRow.getCell(52))}' - выручка")
                        println("  AC(28?)='${getCellValueAsDouble(testRow.getCell(28))}' - материалы")
                        println("  BJ(61?)='${getCellValueAsDouble(testRow.getCell(61))}' - затраты на подрядчика")
                    }
                }

                // Начинаем с 4-ой строки (индекс 3)
                for (i in 3..sheet.lastRowNum) {
                    val row = sheet.getRow(i) ?: continue

                    try {
                        // Читаем данные из нужных столбцов (индексы пока предположительные)
                        val date = getCellValueAsDateTime(row.getCell(61)) // Столбец BI (0-based: 61?)
                        val contractor = getCellValueAsString(row.getCell(29)) // Столбец AD (0-based: 29?)
                        val weight = getCellValueAsDouble(row.getCell(9)) // Столбец J (0-based: 9)
                        val revenue = getCellValueAsDouble(row.getCell(52)) // Столбец BA (0-based: 52)
                        val materials = getCellValueAsDouble(row.getCell(28)) // Столбец AC (0-based: 28)
                        val contractorCost = getCellValueAsDouble(row.getCell(61)) // Столбец BJ (0-based: 61?)

                        // Пропускаем пустые строки
                        if (contractor.isBlank() && weight == 0.0 && revenue == 0.0) {
                            continue
                        }

                        val contractorRow = ContractorRow(
                            date = date ?: LocalDateTime.now(),
                            contractor = if (contractor.isBlank()) "Неизвестный подрядчик" else contractor,
                            weight = weight,
                            revenue = revenue,
                            materialsCost = materials,
                            contractorCost = contractorCost
                        )

                        if (contractorRow.isValid()) {
                            rows.add(contractorRow)
                            if (rows.size <= 3) {
                                println("✅ Добавлена строка подрядчика ${i + 1}: '$contractor' - $weight т, $revenue руб")
                            }
                        }
                    } catch (e: Exception) {
                        println("❌ Ошибка в строке подрядчика ${i + 1}: ${e.message}")
                    }
                }
            }
        }

        println("📈 Успешно прочитано строк подрядчиков: ${rows.size}")
        return rows
    }

    private fun getCellValueAsString(cell: org.apache.poi.ss.usermodel.Cell?): String {
        if (cell == null) return ""

        return try {
            when (cell.cellType) {
                org.apache.poi.ss.usermodel.CellType.STRING -> cell.stringCellValue.trim()
                org.apache.poi.ss.usermodel.CellType.NUMERIC -> {
                    if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                        cell.dateCellValue.toString()
                    } else {
                        // Форматируем число без лишних нулей
                        val value = cell.numericCellValue
                        if (value == value.toLong().toDouble()) {
                            value.toLong().toString()
                        } else {
                            value.toString()
                        }
                    }
                }
                org.apache.poi.ss.usermodel.CellType.BOOLEAN -> cell.booleanCellValue.toString()
                org.apache.poi.ss.usermodel.CellType.FORMULA -> {
                    when (cell.cachedFormulaResultType) {
                        org.apache.poi.ss.usermodel.CellType.STRING -> cell.stringCellValue
                        org.apache.poi.ss.usermodel.CellType.NUMERIC -> cell.numericCellValue.toString()
                        else -> ""
                    }
                }
                else -> ""
            }
        } catch (e: Exception) {
            ""
        }
    }

    private fun getCellValueAsDouble(cell: org.apache.poi.ss.usermodel.Cell?): Double {
        if (cell == null) return 0.0

        return try {
            when (cell.cellType) {
                org.apache.poi.ss.usermodel.CellType.NUMERIC -> cell.numericCellValue
                org.apache.poi.ss.usermodel.CellType.STRING -> {
                    val stringValue = cell.stringCellValue.trim()
                    // Убираем все не-цифровые символы кроме точки и минуса
                    stringValue.replace("[^\\d.-]".toRegex(), "").toDoubleOrNull() ?: 0.0
                }
                org.apache.poi.ss.usermodel.CellType.FORMULA -> {
                    when (cell.cachedFormulaResultType) {
                        org.apache.poi.ss.usermodel.CellType.NUMERIC -> cell.numericCellValue
                        org.apache.poi.ss.usermodel.CellType.STRING ->
                            cell.stringCellValue.replace("[^\\d.-]".toRegex(), "").toDoubleOrNull() ?: 0.0
                        else -> 0.0
                    }
                }
                else -> 0.0
            }
        } catch (e: Exception) {
            0.0
        }
    }

    // ДОБАВЛЯЕМ ЭТОТ МЕТОД ДЛЯ ЧТЕНИЯ ДАТ
    private fun getCellValueAsDateTime(cell: org.apache.poi.ss.usermodel.Cell?): LocalDateTime? {
        if (cell == null) return null

        return try {
            when (cell.cellType) {
                org.apache.poi.ss.usermodel.CellType.NUMERIC -> {
                    if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                        cell.dateCellValue.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
                    } else {
                        // Если это число, а не дата, возвращаем null
                        null
                    }
                }
                org.apache.poi.ss.usermodel.CellType.STRING -> {
                    // Пробуем распарсить строку как дату
                    val stringValue = cell.stringCellValue.trim()
                    parseDateTime(stringValue)
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    // Вспомогательный метод для парсинга дат из строки
    private fun parseDateTime(dateString: String): LocalDateTime? {
        return try {
            // Простой парсинг дат - можно расширить при необходимости
            // Пока возвращаем текущую дату как заглушку
            LocalDateTime.now()
        } catch (e: Exception) {
            null
        }
    }
}