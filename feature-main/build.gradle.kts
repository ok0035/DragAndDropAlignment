import Libs.androidTestImplementations
import Libs.debugImplementations
import Libs.implementations
import Libs.kaptAndroidTests
import Libs.kaptTests
import Libs.kapts
import Libs.testImplementations

plugins {
    kotlin("kapt")
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.zerosword.feature_main"
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
        compose = true
        buildConfig = true
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
            project(":data"),
            project(":domain"),
            project(":resources"),
            platform(Libs.composeBom),
            platform(Libs.okHttpClientBom),
            Libs.coreKtx,
            Libs.lifecycleRuntimeKtx,
            Libs.lifecycleForCompose,
            Libs.lifecycleService,
            Libs.viewModel,
            Libs.viewModelForCompose,
            Libs.viewModelForSavedState,
            Libs.retrofit,
            Libs.okHttpClient,
            Libs.okHttpInterceptor,
            Libs.sandwich,
            Libs.sandwichForRetrofit,
            Libs.activityCompose,
            Libs.composeUi,
            Libs.composeUiGraphics,
            Libs.composeUiToolingPreview,
            Libs.composeConstraintLayout,
            Libs.material3,
            Libs.glide,
            Libs.glideForCompose,
            Libs.coil,
            Libs.hilt,
            Libs.hiltForCompose
        )
    )

    kapts(
        listOf(
            Libs.hiltCompiler,
            Libs.lifecycleCompiler,
            Libs.glide
        )
    )
    kaptTests(listOf(Libs.hiltCompiler))

    testImplementations(
        listOf(
            Libs.junit,
            Libs.okHttpMockWebServer,
            Libs.hiltAndroidTest
        )
    )

    androidTestImplementations(
        listOf(
            platform(Libs.composeBom),
            Libs.androidxTestJunit,
            Libs.androidxEspressoCore,
            Libs.composeUiTestJunit,
            Libs.hiltAndroidTest
        )
    )

    kaptAndroidTests(
        listOf(
            Libs.hiltCompiler
        )
    )

    debugImplementations(
        listOf(
            Libs.composeUiTooling,
            Libs.composeUiTestManifest
        )
    )
}