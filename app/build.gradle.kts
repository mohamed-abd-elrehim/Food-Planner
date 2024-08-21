plugins {
    alias(libs.plugins.android.application)
    id("androidx.navigation.safeargs")  // Apply the Safe Args plugin for type-safe navigation arguments
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Room dependencies for database management
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // Retrofit dependencies for making network requests
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp dependency for HTTP client functionalities
    implementation("com.squareup.okhttp3:okhttp:3.14.9")

    // Glide dependencies for image loading and caching
    implementation("com.github.bumptech.glide:glide:4.11.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.11.0")

    // Glide Transformations library for Android
    implementation("jp.wasabeef:glide-transformations:4.3.0")

    // CardView dependency for displaying UI components in card-like layouts
    implementation("androidx.cardview:cardview:1.0.0")

    // Navigation dependencies for handling in-app navigation
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")

    // Google Material Components for Android
    implementation("com.google.android.material:material:1.12.0")

    // GridLayout library for flexible grid-based layouts
    implementation("androidx.gridlayout:gridlayout:1.0.0")

    // CircleImageView library for easy circular image views
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Lottie library for Android
    implementation("com.airbnb.android:lottie:5.1.0")

    // Firebase Authentication for user sign-up and login
    implementation("com.google.firebase:firebase-auth:21.0.1")

    // Google Sign-In Integration
    implementation("com.google.android.gms:play-services-auth:20.5.0")

    implementation ("androidx.viewpager2:viewpager2:1.0.0")

    implementation ("com.github.bumptech.glide:glide:4.11.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.11.0")
    implementation ("jp.wasabeef:glide-transformations:4.3.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.exoplayer:exoplayer:2.18.2")


}
