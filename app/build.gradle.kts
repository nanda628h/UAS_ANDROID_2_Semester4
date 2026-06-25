import java.util.Properties

// 1. Ambil API KEY dari local.properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use {
        localProperties.load(it)
    }
}

val geminiApiKey = localProperties.getProperty("GEMINI_API_KEY") ?: ""

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.remainderjadwal"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.remainderjadwal"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Inject API KEY ke BuildConfig
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        // Upgrade ke Java 11 sangat disarankan untuk SDK AI terbaru agar tidak crash
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // UI Dasar
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // 🔥 Gemini SDK - Saya update ke 0.9.0 agar support fitur JSON Mode & model terbaru
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // WAJIB: Library biar Java bisa pakai fitur Gemini (Callback/Futures)
    implementation("com.google.guava:guava:33.0.0-android")

    // Lifecycle & Utilities
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}