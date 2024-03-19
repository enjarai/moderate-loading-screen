import dev.kikugie.stonecutter.gradle.StonecutterSettings

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://server.bbkr.space/artifactory/libs-release/")
        maven("https://maven.kikugie.dev/releases")
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.3.2"
}

extensions.configure<StonecutterSettings> {
    kotlinController(true)
    centralScript("build.gradle.kts")
    shared {
        versions("1.19.4", "1.20.1", "1.20.2", "1.20.4")
    }
    create(rootProject)
}