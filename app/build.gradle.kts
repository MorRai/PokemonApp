plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace ="com.example.pokemonapp"
    compileSdk =33

    defaultConfig {
        applicationId = "com.example.pokemonapp"
        minSdk =28
        targetSdk =33
        versionCode =1
        versionName ="1.0"

        testInstrumentationRunner ="androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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

    buildFeatures {
        compose  = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.0"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    //для компоус
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation ("androidx.paging:paging-compose:1.0.0-alpha17")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation ("io.insert-koin:koin-androidx-compose:3.4.1")
    implementation ("androidx.navigation:navigation-compose:2.5.3")

    implementation ("androidx.compose.ui:ui:1.4.3")
    implementation ("androidx.compose.ui:ui-tooling-preview:1.4.3")
    implementation ("androidx.compose.material3:material3:1.0.0-alpha11")
    androidTestImplementation ("androidx.compose.ui:ui-test-junit4:1.4.3")
    debugImplementation ("androidx.compose.ui:ui-tooling:1.4.3")
    debugImplementation ("androidx.compose.ui:ui-test-manifest:1.4.3")


    implementation(project(":domain"))
    implementation(project(":data"))

    implementation (libs.reactivex.rxjava3.rxjava)
    implementation (libs.reactivex.rxjava3.rxandroid)
    implementation (libs.androidx.paging.runtime)
    implementation (libs.androidx.paging.rxjava3)

    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.bundles.androidx.lifecycle)
    implementation(libs.bundles.androidx.navigation)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso)
    implementation(libs.coil)
}