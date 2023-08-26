plugins {
    id("org.jetbrains.kotlin.jvm")
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation ("androidx.paging:paging-common:3.2.0")
    implementation ("io.reactivex.rxjava3:rxjava:3.1.2")
    implementation(libs.kotlinx.coroutine)
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:1.5.2")
}