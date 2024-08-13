// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        // Adds the Safe Args Gradle plugin to the classpath
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.4")
        // The Safe Args plugin is used for generating type-safe classes for passing data between Android Navigation components
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
}