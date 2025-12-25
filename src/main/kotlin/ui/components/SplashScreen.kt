package com.businessanalytics.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onAnimationComplete: () -> Unit
) {
    var isVisible by remember { mutableStateOf(true) }

    // Состояния анимации
    var axesVisible by remember { mutableStateOf(false) }
    var lineProgress by remember { mutableStateOf(0f) }
    var pointsVisible by remember { mutableStateOf(false) }
    var logoTextVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Начальная задержка
        delay(400)

        // 1. Появление осей
        axesVisible = true
        delay(600)

        // 2. Постепенное рисование линии
        lineProgress = 1f
        delay(1800)

        // 3. Появление точек
        pointsVisible = true
        delay(500)

        // 4. Появление текста "УЗСМК АНАЛИТИКА"
        logoTextVisible = true
        delay(1200) // Пауза для показа текста

        // Исчезновение заставки
        isVisible = false
        delay(500)

        // Завершение
        onAnimationComplete()
    }

    // Анимируем lineProgress плавно
    val animatedLineProgress by animateFloatAsState(
        targetValue = lineProgress,
        animationSpec = tween(
            durationMillis = 1800,
            easing = LinearEasing
        )
    )

    if (isVisible) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF004080)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Контейнер для графика
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .background(Color.White, RoundedCornerShape(28.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedGraph(
                        axesVisible = axesVisible,
                        lineProgress = animatedLineProgress,
                        pointsVisible = pointsVisible
                    )
                }

                // Текст "УЗСМК АНАЛИТИКА" с анимацией (единый блок)
                AnimatedVisibility(
                    visible = logoTextVisible,
                    enter = fadeIn(animationSpec = tween(800)) +
                            scaleIn(
                                initialScale = 0.8f,
                                animationSpec = tween(700, easing = EaseOutBack)
                            )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 40.dp)
                    ) {
                        // "УЗСМК" - основной текст
                        Text(
                            text = "УЗСМК",
                            fontSize = 44.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )

                        // "АНАЛИТИКА" - подчеркивающий текст
                        Text(
                            text = "АНАЛИТИКА",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f),
                            letterSpacing = 4.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        // Декоративная линия под текстом
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(2.dp)
                                .background(Color.White.copy(alpha = 0.3f))
                        )
                    }
                }

                // Простой индикатор (опционально)
                AnimatedVisibility(
                    visible = axesVisible && !logoTextVisible,
                    enter = fadeIn(animationSpec = tween(400))
                ) {
                    // Три точки, которые мигают до появления текста
                    Row(
                        modifier = Modifier.padding(top = 60.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(3) { index ->
                            val dotAlpha by animateFloatAsState(
                                targetValue = if (axesVisible) {
                                    // Разные фазы для каждого индекса
                                    val phase = (System.currentTimeMillis() / 400 + index * 200) % 1000
                                    if (phase < 500) (phase / 500f) else 1f - ((phase - 500) / 500f)
                                } else 0f,
                                animationSpec = tween(durationMillis = 500)
                            )

                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color.White.copy(alpha = dotAlpha), CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedGraph(
    axesVisible: Boolean,
    lineProgress: Float,
    pointsVisible: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(100.dp)) { // Увеличили размер
        val padding = 8f
        val strokeWidth = 3f // Утолщили линии

        // Ось X (горизонтальная) - более толстая
        if (axesVisible) {
            drawLine(
                color = Color(0xFF004080).copy(alpha = 0.8f), // Чуть ярче
                start = Offset(padding, size.height - padding),
                end = Offset(size.width - padding, size.height - padding),
                strokeWidth = 2f
            )
        }

        // Ось Y (вертикальная) - более толстая
        if (axesVisible) {
            drawLine(
                color = Color(0xFF004080).copy(alpha = 0.8f),
                start = Offset(padding, padding),
                end = Offset(padding, size.height - padding),
                strokeWidth = 2f
            )
        }

        // Ломаная линия графика с более плавной анимацией
        if (lineProgress > 0) {
            // Более плавные точки графика
            val points = listOf(
                Offset(padding, size.height - padding),          // Начало
                Offset(size.width * 0.25f, size.height * 0.6f),  // Точка 1 (ниже)
                Offset(size.width * 0.45f, size.height * 0.3f),  // Точка 2 (выше)
                Offset(size.width * 0.65f, size.height * 0.4f),  // Точка 3 (средне)
                Offset(size.width * 0.85f, size.height * 0.2f),  // Точка 4 (еще выше)
                Offset(size.width - padding, padding * 2)        // Конец (не в самом углу)
            )

            // Увеличиваем количество сегментов для плавности
            val segments = points.size - 1 // 6 точек = 5 сегментов
            val segmentsToDraw = (segments * lineProgress).toInt()

            // Рисуем полные сегменты
            for (i in 0 until segmentsToDraw) {
                if (i + 1 < points.size) {
                    drawLine(
                        color = Color(0xFF004080),
                        start = points[i],
                        end = points[i + 1],
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }

            // Частичный последний сегмент с плавным переходом
            if (segmentsToDraw < segments && lineProgress > 0) {
                val partialProgress = (lineProgress * segments) - segmentsToDraw
                val startIndex = segmentsToDraw
                if (startIndex + 1 < points.size) {
                    val startPoint = points[startIndex]
                    val endPoint = points[startIndex + 1]

                    // Плавная интерполяция
                    val easedProgress = sin(partialProgress * Math.PI / 2).toFloat().coerceIn(0f, 1f)

                    val partialEndPoint = Offset(
                        startPoint.x + (endPoint.x - startPoint.x) * easedProgress,
                        startPoint.y + (endPoint.y - startPoint.y) * easedProgress
                    )

                    drawLine(
                        color = Color(0xFF004080),
                        start = startPoint,
                        end = partialEndPoint,
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        // Точки на графике - больше
        if (pointsVisible && lineProgress > 0) {
            val points = listOf(
                Offset(padding, size.height - padding),
                Offset(size.width * 0.25f, size.height * 0.6f),
                Offset(size.width * 0.45f, size.height * 0.3f),
                Offset(size.width * 0.65f, size.height * 0.4f),
                Offset(size.width * 0.85f, size.height * 0.2f),
                Offset(size.width - padding, padding * 2)
            )

            // Показываем точки постепенно
            val segments = points.size - 1
            val segmentsToDraw = (segments * lineProgress).toInt()
            val pointsToShow = minOf(points.size, segmentsToDraw + 1)

            for (i in 0 until pointsToShow) {
                // Анимация появления точек (увеличиваются)
                val pointProgress = minOf(1f, lineProgress * 1.5f - i * 0.2f)

                if (pointProgress > 0) {
                    drawCircle(
                        color = Color(0xFF004080),
                        radius = 3f * pointProgress, // Точки увеличиваются
                        center = points[i]
                    )

                    // Белая обводка для контраста
                    drawCircle(
                        color = Color.White,
                        radius = 3f * pointProgress,
                        center = points[i],
                        style = Stroke(width = 0.5f)
                    )
                }
            }
        }
    }
}