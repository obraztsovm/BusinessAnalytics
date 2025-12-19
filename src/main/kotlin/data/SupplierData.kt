package com.businessanalytics.data

import java.time.LocalDateTime

// Строка Excel с данными поставщика материалов
data class SupplierRow(
    val date: LocalDateTime,           // Столбец АА - дата/время поставки
    val supplier: String,              // Столбец У - поставщик материалов
    val materialCost: Double,          // Столбец АС - стоимость материалов (руб)
    val materialWeight: Double = 0.0   // Тоннаж (пока нет в данных, placeholder)
) {
    fun isValid(): Boolean {
        return supplier.isNotBlank() && materialCost >= 0 && materialWeight >= 0
    }
}

// Итоговая сводка по поставщику
data class SupplierSummary(
    val supplier: String,              // Название поставщика
    val totalWeight: Double,           // Общий объем поставок (тонны)
    val totalCost: Double,             // Общая стоимость поставок (руб)
    val quantityShare: Double = 0.0,   // Доля по количеству среди поставщиков (%)
    val costShare: Double = 0.0,       // Доля по стоимости среди поставщиков (%)
    val avgCostPerTon: Double = 0.0    // Средняя стоимость тонны (руб/т)
) {
    // Форматированные значения для таблицы
    fun getFormattedWeight(): String = "%,.2f".format(totalWeight)
    fun getFormattedCost(): String = "%,.2f".format(totalCost)
    fun getFormattedQuantityShare(): String = "%.1f%%".format(quantityShare)
    fun getFormattedCostShare(): String = "%.1f%%".format(costShare)
    fun getFormattedAvgCostPerTon(): String = "%,.1f".format(avgCostPerTon)
}