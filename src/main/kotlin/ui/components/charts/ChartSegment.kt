// ui/charts/ChartSegment.kt
package com.businessanalytics.ui.charts

import androidx.compose.ui.graphics.Color
import com.businessanalytics.ui.theme.*

data class ChartSegment(
    val label: String,
    val value: Double,
    val color: Color = UzmkBlue
)

data class BarData(
    val label: String,
    val value: Double,
    val maxValue: Double,
    val gradientStart: Color = UzmkBlue,
    val gradientEnd: Color = UzmkGold
)