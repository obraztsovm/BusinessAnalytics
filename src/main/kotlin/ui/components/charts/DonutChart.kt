// ui/charts/DonutChart.kt
package com.businessanalytics.ui.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.businessanalytics.ui.theme.*


@Composable
fun LegendItem(segment: ChartSegment, total: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        segment.color,
                        androidx.compose.foundation.shape.CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = segment.label,
                fontSize = 12.sp,
                color = UzmkDarkText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        val percentage = if (total > 0) (segment.value / total * 100) else 0.0
        Text(
            text = "%.1f%%".format(percentage),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = UzmkSteel
        )
    }
}
