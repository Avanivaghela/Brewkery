plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.brewkery"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.brewkery"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

// Retrofit - API connection
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

// Gson - JSON parsing
    implementation("com.google.code.gson:gson:2.11.0")

// Kotlin Coroutines - Background tasks
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

// Coil - Product images
    implementation("io.coil-kt:coil:2.7.0")

// Material Design components
    implementation("com.google.android.material:material:1.12.0")

// RecyclerView - Menu item lists
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.fragment:fragment-ktx:1.8.5")
}