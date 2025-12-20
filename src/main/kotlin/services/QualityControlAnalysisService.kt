package com.businessanalytics.services

import com.businessanalytics.data.QualityControlRow
import com.businessanalytics.data.QualityControlSummary
import java.time.LocalDateTime

class QualityControlAnalysisService {

    fun analyzeQualityControl(
        data: List<QualityControlRow>,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<QualityControlSummary> {
        println("🔬 Начало анализа ОТК: ${data.size} записей, период: $startDate - $endDate")

        // Фильтрация по дате проверки (столбец AW)
        val filteredData = data.filter { row ->
            row.checkDate.isAfter(startDate.minusDays(1)) &&
                    row.checkDate.isBefore(endDate.plusDays(1)) &&
                    row.isValid()
        }

        println("📊 После фильтрации: ${filteredData.size} записей")

        if (filteredData.isEmpty()) {
            println("❌ Нет данных по контролю качества после фильтрации")
            return emptyList()
        }

        // Группируем по сотруднику ОТК (столбец AS)
        val groupedByEmployee = filteredData.groupBy { it.employeeName }
        println("👥 Найдено сотрудников ОТК: ${groupedByEmployee.size}")

        groupedByEmployee.forEach { (employee, rows) ->
            println("   $employee: ${rows.size} проверок, вес: ${rows.sumOf { it.weight }} т")
        }

        // Общие суммы для расчета долей
        val totalWeight = filteredData.sumOf { it.weight }
        val totalValue = filteredData.sumOf { it.value }

        println("⚖️ Общий проверенный вес: $totalWeight т")
        println("💰 Общая проверенная стоимость: $totalValue руб")

        // Создаем сводку по каждому сотруднику
        val result = groupedByEmployee.map { (employee, rows) ->
            val employeeWeight = rows.sumOf { it.weight }
            val employeeValue = rows.sumOf { it.value }

            println("📈 Сотрудник '$employee': вес=$employeeWeight т, стоимость=$employeeValue руб")

            // Расчет долей
            val weightShare = if (totalWeight > 0) {
                (employeeWeight / totalWeight) * 100
            } else {
                0.0
            }

            val valueShare = if (totalValue > 0) {
                (employeeValue / totalValue) * 100
            } else {
                0.0
            }

            QualityControlSummary(
                employeeName = employee,
                totalWeight = employeeWeight,
                totalValue = employeeValue,
                weightShare = weightShare,
                valueShare = valueShare
            )
        }.sortedByDescending { it.totalWeight } // Сортируем по объему проверок

        println("✅ Анализ ОТК завершен: ${result.size} сотрудников")
        return result
    }

    // Дополнительный анализ: эффективность по дням
    fun analyzeDailyProductivity(data: List<QualityControlRow>): Map<String, Double> {
        return data.groupBy { it.checkDate.toLocalDate().toString() }
            .mapValues { (_, rows) -> rows.sumOf { it.weight } }
            .toList()
            .sortedByDescending { it.second }
            .toMap()
    }
}