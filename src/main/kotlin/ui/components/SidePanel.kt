package com.businessanalytics.ui.components

import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.businessanalytics.ui.theme.UzmkBlue // ДОБАВЬ ИМПОРТ
import com.businessanalytics.ui.theme.UzmkDarkText
import com.businessanalytics.ui.theme.UzmkWhite // ДОБАВЬ ИМПОРТ
import com.businessanalytics.ui.theme.UzmkSilver // ДОБАВЬ ИМПОРТ
import com.businessanalytics.ui.theme.UzmkSteel

@Composable
fun SidePanel(
    modifier: Modifier = Modifier,
    selectedScreen: String = "Главная",
    onItemSelected: (String) -> Unit = {}
) {
    Column(
        modifier = modifier
            .background(UzmkWhite)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Заголовок - без точек, меньше отступ
        Column(
            modifier = Modifier.padding(bottom = 24.dp) // Уменьшили отступ с 32 до 24
        ) {
            Text(
                text = "УЗМК",
                color = UzmkBlue,
                fontSize = 28.sp, // Больше
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 2.dp) // Маленький отступ
            )
            Text(
                text = "Аналитика",
                color = UzmkSteel,
                fontSize = 14.sp, // Меньше
                fontWeight = FontWeight.Medium
            )
        }

        // Пункты меню без точек
        SideBarItem("Главная", selectedScreen == "Главная", onClick = { onItemSelected("Главная") })
        SideBarItem("Сводка", selectedScreen == "Сводка", onClick = { onItemSelected("Сводка") })
        SideBarItem("Финансы", selectedScreen == "Финансы", onClick = { onItemSelected("Финансы") })
        SideBarItem("Производство", selectedScreen == "Производство", onClick = { onItemSelected("Производство") })
        SideBarItem("Продажи", selectedScreen == "Продажи", onClick = { onItemSelected("Продажи") })
        SideBarItem("Логистика", selectedScreen == "Логистика", onClick = { onItemSelected("Логистика") })
        SideBarItem("Качество", selectedScreen == "Качество", onClick = { onItemSelected("Качество") })
        SideBarItem("Закупки", selectedScreen == "Закупки", onClick = { onItemSelected("Закупки") })
    }
}

@Composable
fun SideBarItem(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) UzmkBlue.copy(alpha = 0.15f)
                else Color.Transparent
            )
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) UzmkBlue else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Убираем Row с точкой, оставляем только текст
        Text(
            text = title,
            color = if (isSelected) UzmkBlue else UzmkDarkText,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}