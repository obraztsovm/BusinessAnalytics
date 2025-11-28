package com.businessanalytics.data

import java.time.LocalDateTime

// Модель для строки транспортных услуг из Excel
data class TransportRow(
    val date: LocalDateTime,           // Столбец AW - дата перевозки
    val transportCompany: String,      // Столбец AQ - транспортная компания
    val cost: Double,                  // Столбец AN - стоимость перевозки
    val weight: Double,                // Столбец J - вес перевозки
    val vehicle: String                // Столбец ТС - транспортное средство
) {
    fun isValid(): Boolean {
        return transportCompany.isNotBlank() && cost >= 0 && weight >= 0
    }
}

// Модель для результата анализа по транспортным компаниям
data class TransportSummary(
    val transportCompany: String,      // Транспортная компания
    val vehicleCount: Int,             // Количество машин
    val vehicleShare: Double,          // Доля в количестве машин (%)
    val totalCost: Double,             // Общая стоимость услуг
    val costShare: Double,             // Доля в стоимости услуг (%)
    val totalWeight: Double            // Общий вес перевозок
) {
    // Форматированные значения для отображения
    fun getFormattedVehicleCount(): String = vehicleCount.toString()
    fun getFormattedVehicleShare(): String = "%.1f%%".format(vehicleShare)
    fun getFormattedTotalCost(): String = "%,.2f".format(totalCost)
    fun getFormattedCostShare(): String = "%.1f%%".format(costShare)
    fun getFormattedTotalWeight(): String = "%,.2f".format(totalWeight)
}