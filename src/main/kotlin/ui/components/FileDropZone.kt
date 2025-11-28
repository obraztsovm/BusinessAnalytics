package com.businessanalytics.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.AwtWindow
import com.businessanalytics.utils.selectExcelFile
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

@Composable
fun FileDropZone(
    modifier: Modifier = Modifier,
    onFileSelected: (File) -> Unit = {}
) {
    var isDragOver by remember { mutableStateOf(false) }
    var showFileDialog by remember { mutableStateOf(false) }

    // Диалог выбора файла
    if (showFileDialog) {
        AwtWindow(
            create = {
                object : FileDialog(null as Frame?, "Выберите Excel файл", FileDialog.LOAD) {
                    override fun setVisible(value: Boolean) {
                        super.setVisible(value)
                        if (value) {
                            setFilenameFilter { _, name ->
                                name.endsWith(".xlsx") || name.endsWith(".xls")
                            }
                        } else {
                            showFileDialog = false
                            if (file != null) {
                                val selectedFile = File(directory, file)
                                onFileSelected(selectedFile)
                            }
                        }
                    }
                }
            },
            dispose = { it.dispose() }
        )
    }

    Box(
        modifier = modifier
            .border(
                width = 2.dp,
                color = if (isDragOver) Color(0xFF2196F3) else Color(0xFFBDBDBD),
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = if (isDragOver) Color(0xFFE3F2FD) else Color(0xFFFAFAFA),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(32.dp)
            .clickable { showFileDialog = true },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📊",
                fontSize = 48.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "📥 Перетащите Excel файл сюда",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF424242)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "или нажмите для выбора файла",
                fontSize = 14.sp,
                color = Color(0xFF757575)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Поддерживаемые форматы: .xlsx, .xls",
                fontSize = 12.sp,
                color = Color(0xFF9E9E9E)
            )
        }
    }
}