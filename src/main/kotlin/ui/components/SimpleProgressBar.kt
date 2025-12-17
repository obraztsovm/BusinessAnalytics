package com.businessanalytics.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.businessanalytics.ui.theme.*

@Composable
fun SimpleProgressBar(
    percentage: Float, // от 0.0 до 1.0
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    backgroundColor: Color = UzmkLightBg,
    progressColor: Color = UzmkBlue
) {
    Box(
        modifier = modifier.height(height)
    ) {
        // Фон
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        )

        // Прогресс
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(percentage)
                .background(progressColor)
        )
    }
}