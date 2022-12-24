import com.android.build.api.dsl.Optimization
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(Deps.Plugins.Configuration.Kotlin.Android.App)
    id(Deps.Plugins.SQLDelight.Id)
    id(Deps.Plugins.Serialization.Id)
}

group = AppInfo.PACKAGE
version = AppInfo.VERSION

dependencies {
    implementation(Deps.Libs.Androidx.AppCompat)
    implementation(Deps.Libs.SQLDelight.AndroiDriver)
    implementation(Deps.Libs.Ktor.Client.Cio)
    implementation(Deps.Libs.Ktor.Client.Core)
    implementation(Deps.Libs.Ktor.Json)
    implementation(Deps.Libs.Ktor.ContentNegotiation)
    implementation(Deps.Libs.Kotlinx.Coroutines)
    implementation(Deps.Libs.Kotlinx.Serialization)
    implementation(Deps.Libs.Androidx.Compose.UI)
    implementation(Deps.Libs.Androidx.Compose.Icons)
    implementation(Deps.Libs.Androidx.Compose.Material3)
    implementation(Deps.Libs.Androidx.Compose.Activity)
    implementation(Deps.Libs.Androidx.Compose.ExtendedIcons)
    implementation(Deps.Libs.Androidx.Compose.ViewModel)
    implementation(Deps.Libs.Androidx.LifecycleViewModelKtx)
    implementation(Deps.Libs.Androidx.Compose.UITooling)
    implementation(Deps.Libs.Androidx.Compose.Navigation)
    implementation(Deps.Libs.Coil.Compose)
    implementation(Deps.Libs.Androidx.Compose.SystemUiController)
    implementation(Deps.Libs.Androidx.Compose.Material)
    implementation(Deps.Libs.Kotlinx.Immutables)

    testImplementation(Deps.Libs.JUnit.Jupiter)
}

android {
    compileSdk = Deps.compileSdkVersion
    namespace = "com.y9vad9.contacts"

    defaultConfig {
        targetSdk = Deps.compileSdkVersion
        minSdk = Deps.minSdkVersion
        applicationId = "com.y9vad9.contacts"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true


            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Deps.androidComposeVersion
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

sqldelight {
    database("ContactsDatabase") {
        dialect = "sqlite:3.18"
        packageName = "com.y9vad9.contacts.repositories.datasource.db"
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-opt-in=androidx.compose.material3.ExperimentalMaterial3Api")
    }
}