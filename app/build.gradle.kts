plugins {
    alias(libs.plugins.android.application)
    // Apply the Safe Args plugin for type-safe navigation arguments
    id("androidx.navigation.safeargs")
    alias(libs.plugins.google.gms.google.services)  // Use the latest version

}

android {
    namespace = "com.example.mealmate"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mealmate"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    // Room dependencies for database management
    implementation("androidx.room:room-runtime:2.6.1")
    // Provides the Room library, which offers an abstraction layer over SQLite

    annotationProcessor("androidx.room:room-compiler:2.6.1")
    // Annotation processor that generates code for Room's database and DAO classes

    // Retrofit dependencies for making network requests
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Retrofit is a type-safe HTTP client for interacting with REST APIs

    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // Converter that allows Retrofit to parse JSON responses into Java objects using Gson

    // OkHttp dependency for HTTP client functionalities
    implementation("com.squareup.okhttp3:okhttp:3.14.9")
    // OkHttp is a powerful HTTP client used for network operations, underlying Retrofit

    // Glide dependencies for image loading and caching
    implementation("com.github.bumptech.glide:glide:4.11.0")
    // Glide is an image loading library that simplifies loading and caching images in Android apps

    annotationProcessor("com.github.bumptech.glide:compiler:4.11.0")
    // Annotation processor for Glide that generates necessary code to integrate with Glide's APIs

    // Glide Transformations library for Android
    // Provides a variety of image transformations for Glide, such as rounding corners, blurring, and applying custom effects.
    // Ideal for enhancing images loaded with the Glide image loading library by applying various visual effects and modifications.
    implementation("jp.wasabeef:glide-transformations:4.3.0")

    // CardView dependency for displaying UI components in card-like layouts
    implementation("androidx.cardview:cardview:1.0.0")
    // CardView is a UI component that displays information in a card format with rounded corners and shadows

    // Navigation dependencies for handling in-app navigation
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    // Latest version of Navigation Fragment

    implementation("androidx.navigation:navigation-ui:2.7.7")
    // Latest version of Navigation UI


    // Google Material Components for Android
    // This library provides components that follow Google's Material Design guidelines, including UI components, themes, and more.
    // It offers a set of modern, customizable widgets and styles to help you build a visually appealing and consistent user interface.
    // Some of the components included are buttons, text fields, bottom navigation bars, and dialogs.
    // For more information and to explore all the available components, visit the Material Components for Android documentation:
    // https://material.io/develop/android
    implementation("com.google.android.material:material:1.12.0")

    // GridLayout library for flexible grid-based layouts
    implementation("androidx.gridlayout:gridlayout:1.0.0")

    // CircleImageView library for easy circular image views
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Lottie library for Android
    // Provides support for rendering animations and vector graphics from JSON files
    // created with Adobe After Effects. Ideal for integrating high-quality, lightweight animations
    // into your application with minimal performance impact.
    implementation("com.airbnb.android:lottie:5.1.0")

// Material Design Components for UI elements
    implementation("com.google.android.material:material:1.12.0")

// Glide for image loading and caching
    implementation ("com.github.bumptech.glide:glide:4.11.0")

// Annotation processor for Glide to generate API
    annotationProcessor ("com.github.bumptech.glide:compiler:4.11.0")

// Glide Transformations for advanced image manipulation
    implementation ("jp.wasabeef:glide-transformations:4.3.0")

// Firebase Authentication for user sign-up and login
    implementation ("com.google.firebase:firebase-auth:21.0.1")

//Google Sign-In Integration
    implementation ("com.google.android.gms:play-services-auth:20.5.0")
}