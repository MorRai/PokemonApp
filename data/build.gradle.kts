
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace ="com.example.data"
    compileSdk = 33

    defaultConfig {
        minSdk = 28
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":domain"))

    implementation (libs.reactivex.rxjava3.rxjava)
    implementation (libs.reactivex.rxjava3.rxandroid)
    implementation (libs.androidx.paging.runtime)
    implementation (libs.androidx.paging.rxjava3)
    implementation (libs.squareup.retrofit2.adapter.rxjava3)
    implementation (libs.androidx.room.paging)
    implementation  (libs.androidx.room.rxjava3)
    kapt(libs.androidx.room.compiler)
    implementation(libs.bundles.androidx.room)
    implementation(libs.bundles.androidx.retrofit)
    implementation(libs.koin.android)
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso)
}