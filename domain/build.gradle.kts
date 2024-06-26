import Libs.androidTestImplementations
import Libs.debugImplementations
import Libs.implementations
import Libs.kaptAndroidTests
import Libs.kapts
import Libs.testImplementations

plugins {
    kotlin("kapt")
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.zerosword.domain"
    compileSdk = AppConfig.compileSdkVer

    defaultConfig {
        minSdk = AppConfig.minSdkVer

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = AppConfig.kotlinCompilerExtVer
    }
    compileOptions {
        sourceCompatibility = AppConfig.javaVersion
        targetCompatibility = AppConfig.javaVersion
    }
    kotlinOptions {
        jvmTarget = AppConfig.jvmTargetVer
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {

    implementations(
        listOf(
            Libs.composeBom,
            Libs.composeUi,
            Libs.composeUiGraphics,
            Libs.activityCompose,
            Libs.coreKtx,
            Libs.hilt
        )
    )

    kapts(
        listOf(
            Libs.hiltCompiler
        )
    )

    testImplementations(
        listOf(
            Libs.junit,
            Libs.hiltAndroidTest,
        )
    )

    androidTestImplementations(
        listOf(
            platform(Libs.composeBom),
            Libs.composeUiTestJunit,
            Libs.androidxTestJunit,
            Libs.androidxEspressoCore,
            Libs.hiltAndroidTest
        )
    )

    kaptAndroidTests(
        listOf(
            Libs.hiltCompiler
        )
    )
}