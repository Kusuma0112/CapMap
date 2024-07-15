plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.example.capmap"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.capmap"
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
        dataBinding = true
        viewBinding =  true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
//    firebase
    implementation(libs.firebase.analytics)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage)
    implementation(libs.legacy.support.v4)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
//    google maps
    implementation(libs.places)
    implementation (libs.android.maps.utils)
    implementation (libs.play.services.maps)
    implementation (libs.google.maps.services)
    implementation (libs.slf4j.simple)
//    retrofit
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.converter.scalars)
// Navigation Component
    implementation (libs.navigation.fragment)
    implementation (libs.navigation.ui)

    // Circular ImageView library
    implementation (libs.circleimageview)

    // Glide Library
    implementation (libs.glide)
    annotationProcessor (libs.compiler)

    //crop image
    implementation(libs.ucrop)

    //circular view
    implementation (libs.circleimageview)

    implementation(libs.volley)
    //glide library
    implementation (libs.glide)




}