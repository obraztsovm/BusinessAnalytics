package com.businessanalytics.services

import com.businessanalytics.data.ContractorRow
import com.businessanalytics.data.ContractorSummary
import java.time.LocalDateTime

class ContractorAnalysisService {

    fun analyzeContractors(
        data: List<ContractorRow>,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<ContractorSummary> {
        println("👷 Начало анализа подрядчиков: ${data.size} строк, период: $startDate - $endDate")

        // Фильтрация по периоду (столбец BI - date)
        val filteredData = data.filter { row ->
            row.date.isAfter(startDate.minusDays(1)) &&
                    row.date.isBefore(endDate.plusDays(1)) &&
                    row.isValid()
        }

        println("📊 После фильтрации: ${filteredData.size} строк")

        if (filteredData.isEmpty()) {
            println("❌ Нет данных по подрядчикам после фильтрации")
            return emptyList()
        }

        // Группируем по подрядчику (столбец AD)
        val groupedByContractor = filteredData.groupBy { it.contractor }
        println("👥 Найдено подрядчиков: ${groupedByContractor.size}")

        groupedByContractor.forEach { (contractor, rows) ->
            println("   $contractor: ${rows.size} строк, вес: ${rows.sumOf { it.weight }} т")
        }

        // Рассчитываем общую прибыль для долей
        val totalProfit = groupedByContractor.values.sumOf { rows ->
            val revenue = rows.sumOf { it.revenue }
            val materials = rows.sumOf { it.materialsCost }
            val contractorCost = rows.sumOf { it.contractorCost }
            revenue - materials - contractorCost
        }

        println("💰 Общая прибыль по всем подрядчикам: $totalProfit")

        // Создаем сводку по каждому подрядчику
        val result = groupedByContractor.map { (contractor, rows) ->
            val totalWeight = rows.sumOf { it.weight }
            val totalRevenue = rows.sumOf { it.revenue }
            val totalMaterials = rows.sumOf { it.materialsCost }
            val totalContractorCost = rows.sumOf { it.contractorCost }

            // Пока транспортные расходы = 0, добавим позже
            val transportCost = 0.0

            val profit = totalRevenue - totalMaterials - totalContractorCost - transportCost
            val profitShare = if (totalProfit > 0) (profit / totalProfit) * 100 else 0.0

            println("📈 Подрядчик '$contractor':")
            println("   Вес: $totalWeight т, Выручка: $totalRevenue руб")
            println("   Материалы: $totalMaterials руб, Оплата: $totalContractorCost руб")
            println("   Прибыль: $profit руб, Доля: ${"%.1f".format(profitShare)}%")

            ContractorSummary(
                contractor = contractor,
                totalWeight = totalWeight,
                totalRevenue = totalRevenue,
                totalMaterialsCost = totalMaterials,
                totalContractorCost = totalContractorCost,
                transportCost = transportCost,
                profitShare = profitShare
            )
        }.sortedByDescending { it.profit } // Сортируем по прибыли

        println("✅ Анализ подрядчиков завершен: ${result.size} подрядчиков")
        return result
    }

    // Метод для обновления транспортных расходов (добавим позже)
    fun updateTransportCosts(
        contractors: List<ContractorSummary>,
        transportData: List<Any> // Пока Any, уточним структуру позже
    ): List<ContractorSummary> {
        // TODO: Логика сопоставления подрядчик → транспортные расходы
        return contractors
    }
}