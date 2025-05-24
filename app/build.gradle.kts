import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // Firebase plugin
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) // Google Maps plugin
}

android {
    namespace = "com.example.emsismartpresence"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.emsismartpresence"
        minSdk = 29
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Android libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Firebase dependencies
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    // Google Play Services (for maps)
    implementation("com.google.android.gms:play-services-maps:19.2.0")

    // OkHttp for HTTP requests
    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    // JSON handling
    implementation("org.json:json:20230227")

    implementation ("com.google.android.gms:play-services-location:21.1.0")

    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
    implementation ("com.google.maps.android:android-maps-utils:2.2.5")
    implementation ("com.google.android.libraries.places:places:3.2.0")



    implementation ("androidx.gridlayout:gridlayout:1.1.0")
    implementation ("com.google.android.material:material:1.12.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
