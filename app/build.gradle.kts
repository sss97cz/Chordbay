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
    kotlinOptions {
        jvmTarget = "11"
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    implementation("androidx.navigation:navigation-compose:2.9.3")
    implementation("androidx.room:room-runtime:2.7.2")

    implementation("io.insert-koin:koin-android:4.1.0")
    implementation("io.insert-koin:koin-androidx-compose:4.1.0")

    // Retrofit for networking
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    // Gson converter for Retrofit (or use Moshi)
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("com.squareup.retrofit2:converter-moshi:3.0.0") // For Moshi
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0") // Optional: For logging network requests
    implementation("com.squareup.moshi:moshi-kotlin:1.15.2") // Check for the latest version
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

    implementation("androidx.datastore:datastore-preferences:1.1.7")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.2")
    // ... other dependencies
    val room_version = "2.7.2"

    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version") // For Java projects or if still using kapt for Room
    // To use KSP, you should have:
    ksp("androidx.room:room-compiler:$room_version") // For Kotlin projects using KSP

    implementation("androidx.room:room-ktx:$room_version") // Optional - Kotlin Extensions and Coroutines support
    // ...
}