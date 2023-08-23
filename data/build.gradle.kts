
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

    implementation ("androidx.paging:paging-common:3.2.0")
    implementation ("androidx.room:room-paging:2.5.2")
    implementation ("androidx.paging:paging-rxjava2:3.2.0")
    implementation  ("androidx.room:room-rxjava2:2.5.2")

    kapt(libs.androidx.room.compiler)
    implementation(libs.bundles.androidx.room)

    implementation(libs.bundles.androidx.retrofit)

    implementation(libs.koin.android)

    implementation ("androidx.core:core-ktx:1.7.0")
    implementation  ("androidx.appcompat:appcompat:1.6.1")
    implementation  ("com.google.android.material:material:1.9.0")
    testImplementation  ("junit:junit:4.13.2")
    androidTestImplementation  ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation  ("androidx.test.espresso:espresso-core:3.5.1")
}