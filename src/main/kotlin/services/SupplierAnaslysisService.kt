package com.businessanalytics.services

import com.businessanalytics.data.SupplierRow
import com.businessanalytics.data.SupplierSummary
import java.time.LocalDateTime

class SupplierAnalysisService {

    fun analyzeSuppliers(
        data: List<SupplierRow>,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<SupplierSummary> {
        println("🏭 Начало анализа поставщиков: ${data.size} строк, период: $startDate - $endDate")

        // Фильтрация по дате (столбец АА)
        val filteredData = data.filter { row ->
            row.date.isAfter(startDate.minusDays(1)) &&
                    row.date.isBefore(endDate.plusDays(1)) &&
                    row.isValid()
        }

        println("📊 После фильтрации: ${filteredData.size} строк")

        if (filteredData.isEmpty()) {
            println("❌ Нет данных по поставщикам после фильтрации")
            return emptyList()
        }

        // Группируем по поставщику (столбец У)
        val groupedBySupplier = filteredData.groupBy { it.supplier }
        println("🏢 Найдено поставщиков: ${groupedBySupplier.size}")

        groupedBySupplier.forEach { (supplier, rows) ->
            println("   $supplier: ${rows.size} строк, сумма: ${rows.sumOf { it.materialCost }} руб")
        }

        // Рассчитываем общие суммы для долей
        val totalWeight = filteredData.sumOf { it.materialWeight }
        val totalCost = filteredData.sumOf { it.materialCost }

        println("⚖️ Общий вес поставок: $totalWeight т")
        println("💰 Общая стоимость поставок: $totalCost руб")

        // Создаем сводку по каждому поставщику
        val result = groupedBySupplier.map { (supplier, rows) ->
            val supplierWeight = rows.sumOf { it.materialWeight }
            val supplierCost = rows.sumOf { it.materialCost }

            println("📈 Поставщик '$supplier': вес=$supplierWeight т, стоимость=$supplierCost руб")

            // Расчет показателей
            val quantityShare = if (totalWeight > 0) {
                (supplierWeight / totalWeight) * 100
            } else {
                0.0
            }

            val costShare = if (totalCost > 0) {
                (supplierCost / totalCost) * 100
            } else {
                0.0
            }

            val avgCostPerTon = if (supplierWeight > 0) {
                supplierCost / supplierWeight
            } else {
                0.0
            }

            SupplierSummary(
                supplier = supplier,
                totalWeight = supplierWeight,
                totalCost = supplierCost,
                quantityShare = quantityShare,
                costShare = costShare,
                avgCostPerTon = avgCostPerTon
            )
        }.sortedByDescending { it.totalCost } // Сортируем по стоимости

        println("✅ Анализ поставщиков завершен: ${result.size} поставщиков")
        return result
    }
}