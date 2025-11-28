package com.businessanalytics.services

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

                // Пропускаем заголовок и читаем данные
                for (i in 1..sheet.lastRowNum) {
                    val row = sheet.getRow(i) ?: continue

                    try {
                        // Читаем данные из нужных столбцов
                        val client = getCellValueAsString(row.getCell(14)) // Столбец O (индекс 14)
                        val amount = getCellValueAsDouble(row.getCell(52)) // Столбец BA (индекс 52)
                        val weight = getCellValueAsDouble(row.getCell(53)) // Столбец BB (индекс 53)

                        // Пропускаем полностью пустые строки
                        if (client.isBlank() && amount == 0.0 && weight == 0.0) {
                            continue
                        }

                        val excelRow = ExcelRow(
                            time = LocalDateTime.now(), // Временная заглушка для даты
                            client = if (client.isBlank()) "Неизвестный клиент" else client,
                            shipmentAmount = amount,
                            shipmentWeight = weight,
                            paymentDate = null,
                            paymentAmount = amount // Временно используем ту же сумму
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

        println("📈 Успешно прочитано строк: ${rows.size}")
        return rows
    }

    fun readTransportData(file: File): List<TransportRow> {
        val rows = mutableListOf<TransportRow>()

        println("🚚 Чтение транспортных данных из файла: ${file.name}")

        file.inputStream().use { inputStream ->
            WorkbookFactory.create(inputStream).use { workbook ->
                val sheet = workbook.getSheetAt(0)

                for (row in sheet) {
                    if (row.rowNum == 0) continue // Пропускаем заголовок

                    try {
                        val date = getCellValueAsDateTime(row.getCell(48)) // AW (0-based: 48)
                        val company = getCellValueAsString(row.getCell(42)) // AQ (0-based: 42)
                        val cost = getCellValueAsDouble(row.getCell(39)) // AN (0-based: 39)
                        val weight = getCellValueAsDouble(row.getCell(9)) // J (0-based: 9)
                        val vehicle = getCellValueAsString(row.getCell(100)) // ТС - временный индекс

                        // Пропускаем пустые строки
                        if (company.isBlank() && cost == 0.0 && weight == 0.0) {
                            continue
                        }

                        val transportRow = TransportRow(
                            date = date ?: LocalDateTime.now(), // Если дата null, используем текущую
                            transportCompany = company.ifBlank { "Неизвестная компания" },
                            cost = cost,
                            weight = weight,
                            vehicle = vehicle.ifBlank { "Неизвестное ТС" }
                        )

                        if (transportRow.isValid()) {
                            rows.add(transportRow)
                            if (rows.size <= 3) {
                                println("✅ Добавлена транспортная строка ${row.rowNum + 1}: '$company' - $cost руб, $weight т")
                            }
                        }
                    } catch (e: Exception) {
                        println("❌ Ошибка в транспортной строке ${row.rowNum + 1}: ${e.message}")
                    }
                }
            }
        }

        println("📊 Успешно прочитано транспортных строк: ${rows.size}")
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