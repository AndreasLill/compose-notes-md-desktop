import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

group = "com.andreaslill.composenotesmd.desktop"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.components.resources)
    implementation("io.github.vinceglb:filekit-compose:0.6.2")
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            // sun/misc/unsafe
            modules("jdk.unsupported")
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.AppImage)
            packageName = "ComposeNotesMD"
            packageVersion = "1.0.0"
        }
    }
}