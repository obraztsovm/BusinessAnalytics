package com.businessanalytics.services

import com.businessanalytics.data.ExcelRow
import com.businessanalytics.data.ClientSummary
import java.time.LocalDateTime

class AnalysisService {

    fun analyzeClientData(
        data: List<ExcelRow>,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<ClientSummary> {
        println("🔍 Начало анализа: ${data.size} строк, период: $startDate - $endDate")

        // Фильтруем данные по периоду (временно отключим для тестирования)
        val filteredData = data //.filter { row ->
        // row.time.isAfter(startDate.minusDays(1)) &&
        // row.time.isBefore(endDate.plusDays(1)) &&
        // row.isValid()
        // }

        println("📊 После фильтрации: ${filteredData.size} строк")

        if (filteredData.isEmpty()) {
            println("❌ Нет данных для анализа после фильтрации")
            return emptyList()
        }

        // Группируем по клиентам
        val groupedByClient = filteredData.groupBy { it.client }
        println("👥 Найдено клиентов: ${groupedByClient.size}")
        groupedByClient.forEach { (client, rows) ->
            println("   $client: ${rows.size} строк, сумма: ${rows.sumOf { it.shipmentAmount }}")
        }

        // Рассчитываем общие суммы для долей
        val totalShipmentAmount = filteredData.sumOf { it.shipmentAmount }
        val totalPaymentAmount = filteredData.sumOf { it.paymentAmount }

        println("💰 Общая сумма отгрузок: $totalShipmentAmount")
        println("💳 Общая сумма оплат: $totalPaymentAmount")

        // Создаем сводку по каждому клиенту
        val result = groupedByClient.map { (client, rows) ->
            val clientShipmentAmount = rows.sumOf { it.shipmentAmount }
            val clientShipmentWeight = rows.sumOf { it.shipmentWeight }
            val clientPaymentAmount = rows.sumOf { it.paymentAmount }

            println("📈 Клиент $client: отгрузки=$clientShipmentAmount, вес=$clientShipmentWeight, оплаты=$clientPaymentAmount")

            val paymentPercentage = if (clientShipmentAmount > 0) {
                (clientPaymentAmount / clientShipmentAmount) * 100
            } else {
                0.0
            }

            val shipmentShare = if (totalShipmentAmount > 0) {
                (clientShipmentAmount / totalShipmentAmount) * 100
            } else {
                0.0
            }

            val paymentShare = if (totalPaymentAmount > 0) {
                (clientPaymentAmount / totalPaymentAmount) * 100
            } else {
                0.0
            }

            ClientSummary(
                client = client,
                totalShipmentAmount = clientShipmentAmount,
                totalShipmentWeight = clientShipmentWeight,
                totalPaymentAmount = clientPaymentAmount,
                paymentPercentage = paymentPercentage,
                shipmentShare = shipmentShare,
                paymentShare = paymentShare
            )
        }.sortedByDescending { it.totalShipmentAmount }

        println("✅ Анализ завершен: ${result.size} клиентов")
        return result
    }
}