plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)

//    id("com.google.gms.google-services") // Apply the Google Services plugin
}

android {
    namespace = "com.android.swar"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.android.swar"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-auth")

    // Also add the dependency for the Google Play services library and specify its version
    implementation("com.google.android.gms:play-services-auth:21.1.1")

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.database)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.runner)
    implementation(libs.firebase.firestore)
//  implementation(libs.androidx.material3.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("androidx.compose.ui:ui:1.6.7")

    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.28.0")

    implementation ("com.google.android.material:material:1.12.0")

    implementation ("androidx.core:core-splashscreen:1.0.1")
    val nav_version = "2.7.7"

    implementation("androidx.navigation:navigation-compose:$nav_version")

    implementation ("androidx.compose.runtime:runtime-livedata:1.6.7")

    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("com.google.firebase:firebase-storage")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.1")
    implementation ("com.google.accompanist:accompanist-swiperefresh:0.27.0")
}