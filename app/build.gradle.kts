plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.0.21"
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("jacoco")
}

android {
    namespace = "no.uio.ifi.in2000.team47.rocketboy"
    compileSdk = 35

    defaultConfig {
        applicationId = "no.uio.ifi.in2000.team47.rocketboy"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
        }
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
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

    // Kotlin Serialization
    implementation(libs.kotlinx.serialization.json)

    // Retrofit (for other network calls)
    implementation(libs.retrofit)
    implementation(libs.converter.scalars)

    // Lifecycle & ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.core.ktx.v1120)
    implementation(libs.androidx.lifecycle.runtime.ktx.v270)
    implementation(libs.androidx.activity.compose.v182)
    implementation(platform(libs.androidx.compose.bom.v20240200))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.androidx.navigation.compose)

    // Ktor dependencies
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.json)
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    // Coil for image loading
    implementation(libs.coil.compose)

    // Logging for debugging
    implementation(libs.slf4j.simple)
    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.coordinatorlayout)

    implementation(libs.androidx.compose.ui.ui)
    implementation(libs.androidx.compose.material3.material3)
    implementation(libs.androidx.navigation.compose.v260)

    // Material Components for BottomNavigationView
    implementation(libs.material)

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // RoomDB
    implementation(libs.androidx.room.runtime)  // Runtime
    ksp(libs.androidx.room.compiler)    // Annotation processor
    implementation(libs.androidx.room.ktx)  // Kotlin extensions (Coroutines + Flow)

    // MapBox
    implementation(libs.android)    // Mapbox maps
    implementation(libs.maps.compose)    // Maps for compose

}

tasks.withType<Test> {
    ignoreFailures = true
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*"
    )

    val debugTree = fileTree("${layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }

    sourceDirectories.setFrom(files("${projectDir}/src/main/java"))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(layout.buildDirectory.get()) {
        include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
    })
}