// ui/charts/ChartSegment.kt
package com.businessanalytics.ui.charts

import androidx.compose.ui.graphics.Color
import com.businessanalytics.ui.theme.*

data class ChartSegment(
    val label: String,
    val value: Double,
    val color: Color = UzmkBlue
)

