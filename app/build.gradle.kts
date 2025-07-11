import java.util.Properties
import java.io.FileInputStream

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

val googleApiKey = localProperties.getProperty("GOOGLE_API_KEY") ?: ""
val googleAppId = localProperties.getProperty("GOOGLE_APP_ID") ?: ""
val googleRepId = localProperties.getProperty("GOOGLE_REP_ID") ?: ""
val googleBucket = localProperties.getProperty("GOOGLE_DATABASE") ?: ""
val googleProjectId = localProperties.getProperty("GOOGLE_PROJECT") ?: ""

val facebookAppId = localProperties.getProperty("FACEBOOK_APP_ID") ?: ""
val facebookClientToken = localProperties.getProperty("FACEBOOK_CLIENT_TOKEN") ?: ""

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

android {
    namespace = "pl.domain.application.petreco"
    compileSdk = 35

    defaultConfig {
        applicationId = "pl.domain.application.petreco"
        minSdk = 25
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resValue("string", "google_api_key", "\"$googleApiKey\"")
        resValue("string", "google_app_id", "\"$googleAppId\"")
        resValue("string", "google_crash_reporting_api_key", "\"$googleRepId\"")
        resValue("string", "google_storage_bucket", "\"$googleBucket\"")
        resValue("string", "project_id", "\"$googleProjectId\"")

        resValue("string", "facebook_app_id", "\"$facebookAppId\"")
        resValue("string", "facebook_client_token", "\"$facebookClientToken\"")
        resValue("string", "fb_login_protocol_scheme", "\"fb$facebookAppId\"")
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        mlModelBinding = true
    }
}


dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation(libs.firebase.auth)
    val nav_version = "2.7.7"
    implementation("androidx.navigation:navigation-compose:$nav_version")
    implementation("com.chargemap.compose:numberpicker:1.0.3")
    implementation("androidx.compose.material:material-icons-extended:1.5.0")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("com.facebook.android:facebook-android-sdk:latest.release")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("io.coil-kt:coil-compose:2.2.2")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("org.tensorflow:tensorflow-lite-task-vision:0.4.2")
    implementation("org.tensorflow:tensorflow-lite:2.13.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.4.3")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.1.0")
    implementation("com.google.accompanist:accompanist-permissions:0.30.1")
    implementation("io.coil-kt:coil-compose:2.4.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}