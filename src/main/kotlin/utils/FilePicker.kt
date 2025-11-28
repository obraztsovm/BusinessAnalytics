package com.businessanalytics.utils

import androidx.compose.ui.window.AwtWindow
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

fun selectExcelFile(parent: Frame? = null): File? {
    val fileDialog = FileDialog(parent, "Выберите Excel файл", FileDialog.LOAD)
    fileDialog.setFilenameFilter { _, name ->
        name.endsWith(".xlsx") || name.endsWith(".xls")
    }
    fileDialog.isVisible = true

    return if (fileDialog.file != null) {
        File(fileDialog.directory, fileDialog.file)
    } else {
        null
    }
}