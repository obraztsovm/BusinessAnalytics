package com.businessanalytics.data

import java.time.LocalDateTime

// Сырые данные по подрядчикам из Excel
data class ContractorRow(
    val date: LocalDateTime,           // Столбец BI - дата/время
    val contractor: String,            // Столбец AD - подрядчик
    val weight: Double,                // Столбец J - вес продукции
    val revenue: Double,               // Столбец BA - выручка
    val materialsCost: Double,         // Столбец AC - стоимость материалов
    val contractorCost: Double,        // Столбец BJ - затраты на подрядчика
    val productType: String = ""       // Тип продукции (если нужно)
) {
    fun isValid(): Boolean {
        return contractor.isNotBlank() && weight >= 0 && revenue >= 0
    }
}

// Итоговая сводка по подрядчику
data class ContractorSummary(
    val contractor: String,                    // Подрядчик
    val totalWeight: Double,                   // Вес произведенной продукции
    val totalRevenue: Double,                  // Выручка
    val totalMaterialsCost: Double,            // Материалы
    val totalContractorCost: Double,           // Оплата подрядчику
    val transportCost: Double = 0.0,           // Транспортные расходы (пока 0)
    val profitShare: Double = 0.0              // Доля в прибыли (рассчитается в сервисе)
) {
    // Рассчитанные показатели

    // Прибыль = выручка - материалы - подрядчик - транспорт
    val profit: Double
        get() = totalRevenue - totalMaterialsCost - totalContractorCost - transportCost

    // Маржа на тонну = прибыль / вес
    val marginPerTon: Double
        get() = if (totalWeight > 0) profit / totalWeight else 0.0

    // Процент маржи = прибыль / выручка * 100
    val marginPercentage: Double
        get() = if (totalRevenue > 0) (profit / totalRevenue) * 100 else 0.0

    // Форматированные значения для таблицы
    fun getFormattedWeight(): String = "%,.2f".format(totalWeight)
    fun getFormattedRevenue(): String = "%,.2f".format(totalRevenue)
    fun getFormattedMaterials(): String = "%,.2f".format(totalMaterialsCost)
    fun getFormattedContractorCost(): String = "%,.2f".format(totalContractorCost)
    fun getFormattedTransport(): String = "%,.2f".format(transportCost)
    fun getFormattedProfit(): String = "%,.2f".format(profit)
    fun getFormattedMarginPerTon(): String = "%,.2f".format(marginPerTon)
    fun getFormattedProfitShare(): String = "%.1f%%".format(profitShare)
    fun getFormattedMarginPercentage(): String = "%.1f%%".format(marginPercentage)
}