plugins {
    kotlin("jvm") version "1.9.0"
    id("org.jetbrains.compose") version "1.5.11" // Обновите версию
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.apache.poi:poi:5.2.4")
    implementation("org.apache.poi:poi-ooxml:5.2.4")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            // Правильный синтаксис для targetFormats
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Exe,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi
            )

            packageName = "BusinessAnalytics"
            packageVersion = "1.0.0"

            // Включаем все модули
            includeAllModules = true

            // Правильное объявление модулей
            modules("java.instrument", "java.management", "java.naming", "java.sql", "java.xml")

            // Альтернатива - используйте дефолтные модули
            // Оставьте пустым для использования дефолтных

            windows {
                // Сначала создайте файл icon.ico в корне проекта
                // iconFile.set(project.file("src/main/resources/icon.ico"))

                menu = true
                menuGroup = "BusinessAnalytics"
                // Генерируйте UUID здесь: https://www.uuidgenerator.net/
                upgradeUuid = "550e8400-e29b-41d4-a716-446655440000"
                perUserInstall = true

                // Для создания ярлыка на рабочем столе
                shortcut = true
            }

            // Настройки JVM
            jvmArgs += listOf("-Xmx512m", "-Dfile.encoding=UTF-8")
        }
    }
}

kotlin {
    jvmToolchain(17) // Используйте Java 17 для совместимости
}