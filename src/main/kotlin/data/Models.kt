package com.businessanalytics.data

import java.time.LocalDateTime

// Модель для строки из Excel файла
data class ExcelRow(
    val time: LocalDateTime,          // Столбец AZ - время
    val client: String,               // Столбец O - клиент
    val shipmentAmount: Double,       // Столбец BA - сумма отгрузки
    val shipmentWeight: Double,       // Столбец BB - вес отгрузки (тонны)
    val paymentDate: LocalDateTime?,  // Столбец BG - дата оплаты
    val paymentAmount: Double         // Столбец BA - сумма оплаты
) {
    // Валидация данных
    fun isValid(): Boolean {
        return client.isNotBlank() && shipmentAmount >= 0 && shipmentWeight >= 0
    }
}

// Модель для результата анализа по клиенту
data class ClientSummary(
    val client: String,               // Клиент
    val totalShipmentAmount: Double,  // Сумма отгрузок
    val totalShipmentWeight: Double,  // Вес отгрузок (тонны)
    val totalPaymentAmount: Double,   // Сумма оплаты
    val paymentPercentage: Double,    // Процент оплаты отгрузок
    val shipmentShare: Double,        // Доля отгрузок (%)
    val paymentShare: Double          // Доля оплат (%)
) {
    // Форматированные значения для отображения
    fun getFormattedShipmentAmount(): String = "%,.2f".format(totalShipmentAmount)
    fun getFormattedShipmentWeight(): String = "%,.2f".format(totalShipmentWeight)
    fun getFormattedPaymentAmount(): String = "%,.2f".format(totalPaymentAmount)
    fun getFormattedPaymentPercentage(): String = "%.1f%%".format(paymentPercentage)
    fun getFormattedShipmentShare(): String = "%.1f%%".format(shipmentShare)
    fun getFormattedPaymentShare(): String = "%.1f%%".format(paymentShare)
}