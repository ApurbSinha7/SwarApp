// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
//    id("com.google.gms.google-services") // Apply the Google Services plugin
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}



buildscript {
    repositories {
        google()
        // other repositories
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
    }
}