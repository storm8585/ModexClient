plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val vCode = (project.findProperty("versionCode") as? String)?.toIntOrNull() ?: 1
val vName = (project.findProperty("versionName") as? String) ?: "1.0.0"
val targetAbi = project.findProperty("targetAbi") as? String

android {
    namespace = "com.modex.vpn"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.modex.vpn"
        minSdk = 26
        targetSdk = 35
        versionCode = vCode
        versionName = vName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        if (!targetAbi.isNullOrBlank()) {
            ndk {
                abiFilters.clear()
                abiFilters.add(targetAbi)
            }
        }
    }

    signingConfigs {
        create("customSign") {
            val ksFile = rootProject.file("keystore.jks")
            if (ksFile.exists()) {
                storeFile = ksFile
                storePassword = "modexpassword"
                keyAlias = "modexkey"
                keyPassword = "modexpassword"
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            val ksFile = rootProject.file("keystore.jks")
            if (ksFile.exists()) {
                signingConfig = signingConfigs.getByName("customSign")
            }
        }
        debug {
            val ksFile = rootProject.file("keystore.jks")
            if (ksFile.exists()) {
                signingConfig = signingConfigs.getByName("customSign")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.10.0")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
