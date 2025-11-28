package com.businessanalytics.services

import com.businessanalytics.data.TransportRow
import com.businessanalytics.data.TransportSummary
import java.time.LocalDateTime

class TransportAnalysisService {

    fun analyzeTransportData(
        data: List<TransportRow>,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransportSummary> {
        println("🚚 Начало анализа транспортных услуг: ${data.size} строк, период: $startDate - $endDate")

        if (data.isEmpty()) {
            println("❌ Нет данных для анализа транспортных услуг")
            return emptyList()
        }

        // Выводим информацию о первых нескольких строках для отладки
        println("📋 Первые 5 транспортных строк:")
        data.take(5).forEachIndexed { index, row ->
            println("   $index: Компания='${row.transportCompany}', Стоимость=${row.cost}, Вес=${row.weight}, ТС='${row.vehicle}'")
        }

        // Временно отключаем фильтрацию по дате для тестирования
        val filteredData = data

        println("📊 После фильтрации: ${filteredData.size} строк")

        if (filteredData.isEmpty()) {
            println("❌ Нет данных для анализа транспортных услуг после фильтрации")
            return emptyList()
        }

        // Группируем по транспортным компаниям
        val groupedByCompany = filteredData.groupBy { it.transportCompany }
        println("🏢 Найдено транспортных компаний: ${groupedByCompany.size}")

        groupedByCompany.forEach { (company, rows) ->
            println("   '$company': ${rows.size} строк")
        }

        // Рассчитываем общие суммы для долей
        val totalVehicles = filteredData.map { it.vehicle }.distinct().count()
        val totalCost = filteredData.sumOf { it.cost }

        println("🚛 Всего уникальных машин: $totalVehicles")
        println("💰 Общая стоимость услуг: $totalCost")

        // Создаем сводку по каждой транспортной компании
        val result = groupedByCompany.map { (company, rows) ->
            val companyVehicles = rows.map { it.vehicle }.distinct().count()
            val companyCost = rows.sumOf { it.cost }
            val companyWeight = rows.sumOf { it.weight }

            println("📈 Компания '$company': машины=$companyVehicles, стоимость=$companyCost, вес=$companyWeight")

            val vehicleShare = if (totalVehicles > 0) {
                (companyVehicles.toDouble() / totalVehicles) * 100
            } else {
                0.0
            }

            val costShare = if (totalCost > 0) {
                (companyCost / totalCost) * 100
            } else {
                0.0
            }

            TransportSummary(
                transportCompany = company,
                vehicleCount = companyVehicles,
                vehicleShare = vehicleShare,
                totalCost = companyCost,
                costShare = costShare,
                totalWeight = companyWeight
            )
        }.sortedByDescending { it.totalCost } // Сортируем по стоимости

        println("✅ Анализ транспортных услуг завершен: ${result.size} компаний")
        return result
    }
}