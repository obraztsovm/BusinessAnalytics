package com.businessanalytics.ui.components

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color as ComposeColor
import java.awt.*
import java.awt.dnd.*
import java.io.File
import javax.swing.*
import javax.swing.border.EmptyBorder

@Composable
fun FileDropZone(
    modifier: Modifier = Modifier,
    onFileSelected: (File) -> Unit = {}
) {
    var isDragOver by remember { mutableStateOf(false) }
    var isClicked by remember { mutableStateOf(false) }

    SwingPanel(
        modifier = modifier,
        background = ComposeColor.Transparent,
        factory = {
            createOrangeDropZonePanel(isDragOver, isClicked, onFileSelected) { dragOver, clicked ->
                isDragOver = dragOver
                isClicked = clicked
            }
        },
        update = { panel ->
            updateOrangeDropZonePanel(panel, isDragOver, isClicked)
        }
    )
}

private fun createOrangeDropZonePanel(
    isDragOver: Boolean,
    isClicked: Boolean,
    onFileSelected: (File) -> Unit,
    onStateChange: (Boolean, Boolean) -> Unit
): JPanel {
    return object : JPanel() {
        init {
            layout = BorderLayout()
            isOpaque = false

            // Основной контент с скругленными углами
            val contentPanel = object : JPanel() {
                override fun paintComponent(g: Graphics) {
                    val g2d = g as Graphics2D
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

                    // Оранжевый градиент
                    val gradient = when {
                        isDragOver -> {
                            GradientPaint(
                                0f, 0f, Color(0xFF, 0xF3, 0xE0), // Светло-оранжевый
                                width.toFloat(), height.toFloat(), Color(0xFF, 0xE0, 0xB2) // Более светлый оранжевый
                            )
                        }
                        isClicked -> {
                            GradientPaint(
                                0f, 0f, Color(0xFF, 0xEC, 0xB3), // Оранжевый при клике
                                width.toFloat(), height.toFloat(), Color(0xFF, 0xE0, 0xB2)
                            )
                        }
                        else -> {
                            GradientPaint(
                                0f, 0f, Color(0xFF, 0xFA, 0xFA), // Почти белый
                                width.toFloat(), height.toFloat(), Color(0xFF, 0xF5, 0xE6) // Очень светлый оранжевый
                            )
                        }
                    }

                    g2d.paint = gradient
                    g2d.fillRoundRect(0, 0, width, height, 24, 24)

                    // Оранжевая граница
                    val borderColor = when {
                        isDragOver -> Color(0xFF, 0x57, 0x22) // Яркий оранжевый при перетаскивании
                        isClicked -> Color(0xFF, 0x98, 0x00) // Основной оранжевый при клике
                        else -> Color(0xFF, 0xB7, 0x4D) // Светло-оранжевая граница
                    }

                    g2d.color = borderColor
                    g2d.stroke = BasicStroke(if (isDragOver) 2.5f else if (isClicked) 2f else 1.5f)
                    g2d.drawRoundRect(1, 1, width - 3, height - 3, 24, 24)

                    // Оранжевый пунктир при перетаскивании
                    if (isDragOver) {
                        g2d.stroke = BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                            0f, floatArrayOf(10f, 5f), 0f)
                        g2d.color = Color(0xFF, 0x57, 0x22)
                        g2d.drawRoundRect(4, 4, width - 8, height - 8, 20, 20)
                    }
                }
            }.apply {
                layout = BorderLayout()
                border = EmptyBorder(40, 40, 40, 40)

                // Внутренний контент
                val innerPanel = JPanel().apply {
                    layout = BoxLayout(this, BoxLayout.Y_AXIS)
                    isOpaque = false

                    // Иконка (эмодзи)
                    val iconLabel = JLabel("📊").apply {
                        font = Font("Segoe UI Emoji", Font.PLAIN, 64)
                        alignmentX = Component.CENTER_ALIGNMENT
                        foreground = Color(0xFF, 0x98, 0x00) // Оранжевый
                        border = EmptyBorder(0, 0, 24, 0)
                    }

                    // Заголовок
                    val titleLabel = JLabel(
                        if (isDragOver) "Отпустите Excel файл" else "Загрузите данные"
                    ).apply {
                        font = Font("Segoe UI", Font.BOLD, 24)
                        alignmentX = Component.CENTER_ALIGNMENT
                        foreground = Color(0x21, 0x21, 0x21) // Темный текст
                        border = EmptyBorder(0, 0, 12, 0)
                    }

                    // Подзаголовок
                    val subtitleLabel = JLabel(
                        if (isDragOver) "Готово к анализу" else "Перетащите файл .xlsx или .xls"
                    ).apply {
                        font = Font("Segoe UI", Font.PLAIN, 16)
                        alignmentX = Component.CENTER_ALIGNMENT
                        foreground = Color(0x66, 0x66, 0x66) // Серый текст
                        border = EmptyBorder(0, 0, 8, 0)
                    }

                    // Кнопка/ссылка
                    val actionLabel = JLabel(
                        if (isDragOver) "анализ начнется автоматически" else "или нажмите для выбора файла"
                    ).apply {
                        font = Font("Segoe UI", Font.PLAIN, 14)
                        alignmentX = Component.CENTER_ALIGNMENT
                        foreground = Color(0xFF, 0x98, 0x00) // Оранжевый акцент
                    }

                    // Информация о форматах
                    val formatsLabel = JLabel("Поддерживаемые форматы: Excel (.xlsx, .xls)").apply {
                        font = Font("Segoe UI", Font.PLAIN, 12)
                        alignmentX = Component.CENTER_ALIGNMENT
                        foreground = Color(0x99, 0x99, 0x99) // Светло-серый
                        border = EmptyBorder(24, 0, 0, 0)
                    }

                    add(iconLabel)
                    add(titleLabel)
                    add(subtitleLabel)
                    add(actionLabel)
                    add(formatsLabel)
                }

                add(innerPanel, BorderLayout.CENTER)
            }

            add(contentPanel, BorderLayout.CENTER)

            // Обработка клика
            addMouseListener(object : java.awt.event.MouseAdapter() {
                override fun mousePressed(e: java.awt.event.MouseEvent) {
                    onStateChange(isDragOver, true)
                }

                override fun mouseReleased(e: java.awt.event.MouseEvent) {
                    onStateChange(isDragOver, false)
                    if (contains(e.point)) {
                        openFileDialog(onFileSelected)
                    }
                }

                override fun mouseEntered(e: java.awt.event.MouseEvent) {
                    cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                }

                override fun mouseExited(e: java.awt.event.MouseEvent) {
                    cursor = Cursor.getDefaultCursor()
                    onStateChange(false, false)
                }
            })

            // Настройка drag-and-drop
            setupOrangeDragAndDrop(this, onStateChange, onFileSelected)
        }

        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            // Оранжевая тень при перетаскивании
            if (isDragOver) {
                val g2d = g as Graphics2D
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

                g2d.color = Color(0xFF, 0x98, 0x00, 30) // Полупрозрачный оранжевый
                g2d.fillRoundRect(8, 8, width, height, 24, 24)
            }
        }
    }
}

private fun updateOrangeDropZonePanel(panel: JPanel, isDragOver: Boolean, isClicked: Boolean) {
    panel.repaint()

    // Обновляем текст
    panel.components.forEach { component ->
        if (component is JPanel) {
            component.components.forEach { innerComponent ->
                if (innerComponent is JPanel) {
                    innerComponent.components.forEach { label ->
                        if (label is JLabel) {
                            when (label.text) {
                                "Загрузите данные",
                                "Отпустите Excel файл" -> {
                                    label.text = if (isDragOver) "Отпустите Excel файл" else "Загрузите данные"
                                    label.foreground = if (isDragOver) Color(0xFF, 0x57, 0x22) else Color(0x21, 0x21, 0x21)
                                }
                                "Перетащите файл .xlsx или .xls",
                                "Готово к анализу" -> {
                                    label.text = if (isDragOver) "Готово к анализу" else "Перетащите файл .xlsx или .xls"
                                }
                                "или нажмите для выбора файла",
                                "анализ начнется автоматически" -> {
                                    label.text = if (isDragOver) "анализ начнется автоматически" else "или нажмите для выбора файла"
                                    label.foreground = Color(0xFF, 0x98, 0x00)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun setupOrangeDragAndDrop(
    component: Component,
    onStateChange: (Boolean, Boolean) -> Unit,
    onFileSelected: (File) -> Unit
) {
    component.dropTarget = object : DropTarget() {
        override fun dragEnter(dtde: DropTargetDragEvent) {
            if (dtde.transferable.isDataFlavorSupported(
                    java.awt.datatransfer.DataFlavor.javaFileListFlavor
                )) {
                dtde.acceptDrag(DnDConstants.ACTION_COPY)
                onStateChange(true, false)
            }
        }

        override fun dragExit(dtde: DropTargetEvent) {
            onStateChange(false, false)
        }

        override fun drop(dtde: DropTargetDropEvent) {
            onStateChange(false, false)

            try {
                dtde.acceptDrop(DnDConstants.ACTION_COPY)

                @Suppress("UNCHECKED_CAST")
                val transferable = dtde.transferable
                val files = transferable.getTransferData(
                    java.awt.datatransfer.DataFlavor.javaFileListFlavor
                ) as List<File>

                files.firstOrNull()?.let { file ->
                    if (file.name.endsWith(".xlsx", ignoreCase = true) ||
                        file.name.endsWith(".xls", ignoreCase = true)) {
                        SwingUtilities.invokeLater {
                            onFileSelected(file)
                        }
                        dtde.dropComplete(true)
                        return
                    }
                }

                showErrorDialog("Пожалуйста, выберите файл Excel (.xlsx или .xls)")
                dtde.dropComplete(false)
            } catch (e: Exception) {
                showErrorDialog("Ошибка при чтении файла: ${e.message}")
                dtde.dropComplete(false)
            }
        }
    }
}

private fun openFileDialog(onFileSelected: (File) -> Unit) {
    val fileDialog = FileDialog(null as Frame?, "Выберите Excel файл", FileDialog.LOAD)
    fileDialog.setFilenameFilter { _, name ->
        name.endsWith(".xlsx", ignoreCase = true) ||
                name.endsWith(".xls", ignoreCase = true)
    }
    fileDialog.isVisible = true

    if (fileDialog.file != null) {
        val selectedFile = File(fileDialog.directory, fileDialog.file)
        onFileSelected(selectedFile)
    }
}

private fun showErrorDialog(message: String) {
    JOptionPane.showMessageDialog(
        null,
        message,
        "Ошибка загрузки",
        JOptionPane.ERROR_MESSAGE
    )
}