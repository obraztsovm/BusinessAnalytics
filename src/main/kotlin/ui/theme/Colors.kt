package com.businessanalytics.ui.theme

import androidx.compose.ui.graphics.Color

// СИНЯЯ ПАЛИТРА
val UzmkBlue = Color(0xFF004080)       // Темно-синий - основной
val UzmkSteel = Color(0xFF66a3ff)      // Светло-синий - вторичный
val UzmkSilver = Color(0xFFBBDEFB)     // Очень светлый синий - акценты
val UzmkGold = Color(0xFF0059b3)       // Акцентный синий - премиум акценты
val UzmkLightBg = Color(0xFFF5F7FA)
val UzmkWhite = Color(0xFFFFFFFF)      // Чистый белый
val UzmkDarkText = Color(0xFF212121)   // Темный текст (почти черный)
val UzmkGrayText = Color(0xFF757575)   // Серый текст

// Дополнительные цвета для статусов
val SuccessGreen = Color(0xFF4CAF50)   // Зеленый для успеха
val ErrorRed = Color(0xFFF44336)       // Красный для ошибки
val WarningOrange = Color(0xFFFFAA00)  // Желтый для предупреждения

// Границы
val BorderLight = Color(0xFFEEEEEE)    // Светлая граница
val BorderMedium = Color(0xFFE0E0E0)   // Средняя граница

// ========== ДОБАВЬТЕ ЭТИ ЦВЕТА ==========
// Цвета для графиков
val ChartPurple = Color(0xFF9C27B0)    // Фиолетовый для графиков
val ChartTeal = Color(0xFF009688)      // Бирюзовый для графиков
val ChartBlueGray = Color(0xFF607D8B)  // Сине-серый для графиков
val ChartLightGray = Color(0xFFB0BEC5) // Светло-серый для "остальных"
val ChartDarkBlue = Color(0xFF1976D2)  // Темно-синий (альтернатива)
val ChartLightGreen = Color(0xFF8BC34A) // Светло-зеленый

// Альтернативная палитра (если нужны дополнительные цвета)
val ChartColors = listOf(
    UzmkGold,
    UzmkBlue,
    SuccessGreen,
    UzmkSteel,
    ChartPurple,
    ChartTeal,
    WarningOrange,
    ChartBlueGray
)