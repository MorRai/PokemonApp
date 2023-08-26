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
        viewBinding = true
    }
}

dependencies {
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:1.5.2")

    implementation ("io.reactivex.rxjava3:rxjava:3.1.2")
    implementation ("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation ("androidx.paging:paging-rxjava3:3.2.0")

    //Paging 3
    implementation ("androidx.paging:paging-runtime:3.2.0")


    //implementation ("androidx.lifecycle:lifecycle-runtime-rxjava3:2.4.1")

    implementation(project(":domain"))
    implementation(project(":data"))

    implementation ("io.insert-koin:koin-core:3.3.2")
    implementation(libs.koin.android)



    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.bundles.androidx.lifecycle)
    implementation(libs.bundles.androidx.navigation)



    implementation ("androidx.core:core-ktx:1.8.0")
    implementation  ("androidx.appcompat:appcompat:1.6.1")
    implementation  ("com.google.android.material:material:1.5.0")
    implementation  ("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation  ("junit:junit:4.13.2")
    androidTestImplementation  ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation  ("androidx.test.espresso:espresso-core:3.5.1")

    implementation(libs.coil)
}