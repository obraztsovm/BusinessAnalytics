package com.businessanalytics.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.businessanalytics.ui.theme.UzmkBlue // ДОБАВЬ ИМПОРТ
import com.businessanalytics.ui.theme.UzmkWhite // ДОБАВЬ ИМПОРТ
import com.businessanalytics.ui.theme.UzmkSilver // ДОБАВЬ ИМПОРТ

@Composable
fun SidePanel(
    modifier: Modifier = Modifier,
    onItemSelected: (String) -> Unit = {}
) {
    Column(
        modifier = modifier
            .background(UzmkBlue)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Заголовок приложения - стильно без эмодзи
        Text(
            text = "УЗМК",
            color = UzmkWhite,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Аналитика",
            color = UzmkSilver,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Пункты меню - чистый текст
        SideBarItem("Главная", onClick = { onItemSelected("Главная") })
        SideBarItem("Сводка", onClick = { onItemSelected("Сводка") })
        SideBarItem("Финансы", onClick = { onItemSelected("Финансы") })
        SideBarItem("Производство", onClick = { onItemSelected("Производство") })
        SideBarItem("Продажи", onClick = { onItemSelected("Продажи") })
        SideBarItem("Логистика", onClick = { onItemSelected("Логистика") })
        SideBarItem("Качество", onClick = { onItemSelected("Качество") })
        SideBarItem("Закупки", onClick = { onItemSelected("Закупки") })
    }
}

@Composable
fun SideBarItem(title: String, onClick: () -> Unit) {
    Text(
        text = title,
        color = UzmkWhite,
        fontSize = 14.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable(onClick = onClick)
    )
}