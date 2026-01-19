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
            // ПРАВИЛЬНЫЙ СИНТАКСИС для targetFormats
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Exe,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi
                // Если нужно для Mac/Linux, раскомментируй:
                // org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
                // org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
            )

            packageName = "BusinessAnalytics"
            packageVersion = "1.0.0"
            description = "Аналитика для УЗСМК"
            vendor = "УЗСМК"
            copyright = "© 2024 УЗСМК. Все права защищены."

            // ВКЛЮЧАЕМ JVM ВНУТРЬ УСТАНОВЩИКА
            includeAllModules = true

            // Настройки для Windows
            windows {
                // Создай файл icon.ico в src/main/resources/
                // iconFile.set(project.file("src/main/resources/icon.ico"))

                menu = true
                menuGroup = "Business Analytics"
                upgradeUuid = "550e8400-e29b-41d4-a716-446655440000"
                perUserInstall = true
                shortcut = true
            }

            // Настройки JVM
            jvmArgs += listOf("-Xmx512m", "-Dfile.encoding=UTF-8")

            // Указываем модули Java (опционально, можно закомментировать)
            modules(
                "java.base",
                "java.desktop",
                "java.sql",
                "java.xml"
            )
        }
    }
}
kotlin {
    jvmToolchain(17) // Используйте Java 17 для совместимости
}