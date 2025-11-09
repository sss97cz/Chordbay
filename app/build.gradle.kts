import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}
android {
    namespace = "com.example.chords2"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.chords2"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
            freeCompilerArgs.add("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
        }
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.unit)
    implementation(libs.androidx.foundation)
    implementation(libs.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.compose.material.icons.extended)


    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room.runtime)

    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // Retrofit for networking
    implementation(libs.retrofit)
    // Gson converter for Retrofit (or use Moshi)
    implementation(libs.converter.gson)
    implementation(libs.converter.moshi) // For Moshi
    implementation(libs.logging.interceptor) // Optional: For logging network requests
    implementation(libs.moshi.kotlin) // Check for the latest version
    implementation(libs.converter.scalars)

    implementation(libs.androidx.datastore.preferences)
    ksp(libs.moshi.kotlin.codegen)
    // ... other dependencies

    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler) // For Java projects or if still using kapt for Room
    // To use KSP, you should have:
    ksp(libs.androidx.room.compiler) // For Kotlin projects using KSP

    implementation(libs.androidx.room.ktx) // Optional - Kotlin Extensions and Coroutines support
    // ...
}