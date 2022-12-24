pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }

    plugins {
        kotlin("android") version "1.7.21"
        id("com.squareup.sqldelight") version "1.5.4"
        id("com.android.application") version "7.3.1"
        id("org.jetbrains.kotlin.plugin.serialization") version "1.7.21"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.y9vad9.com")
    }
}

rootProject.name = "contacts-app"

includeBuild("build-logic/dependencies")
includeBuild("build-logic/configuration")
