package com.businessanalytics.data

import java.time.LocalDateTime

// Строка данных контроля качества
data class QualityControlRow(
    val checkDate: LocalDateTime,      // Столбец AW - дата проверки
    val employeeName: String,          // Столбец AS - сотрудник ОТК
    val weight: Double,                // Столбец J - вес продукции (тонны)
    val value: Double                  // Столбец BA - стоимость продукции (руб)
) {
    fun isValid(): Boolean {
        return employeeName.isNotBlank() && weight >= 0 && value >= 0
    }
}

// Результат анализа по сотруднику ОТК
data class QualityControlSummary(
    val employeeName: String,          // ФИО сотрудника
    val totalWeight: Double,           // Общий вес проверенной продукции (т)
    val totalValue: Double,            // Общая стоимость проверенной продукции (руб)
    val weightShare: Double = 0.0,     // Доля в общем объеме (%)
    val valueShare: Double = 0.0       // Доля в общей стоимости (%)
) {
    // Форматированные значения
    fun getFormattedWeight(): String = "%,.2f".format(totalWeight)
    fun getFormattedValue(): String = "%,.2f".format(totalValue)
    fun getFormattedWeightShare(): String = "%.1f%%".format(weightShare)
    fun getFormattedValueShare(): String = "%.1f%%".format(valueShare)

    // Производительность (тонн на сотрудника)
    val productivity: Double
        get() = if (totalWeight > 0) totalValue / totalWeight else 0.0

    fun getFormattedProductivity(): String = "%,.0f".format(productivity)
}