plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.ssuwap"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ssuwap"
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
    viewBinding{
        enable = true
    }
}

dependencies {
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.prolificinteractive:material-calendarview:1.4.3")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")



    implementation(libs.photoview)

    implementation(libs.glide)

    // CameraX 라이브러리
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)

    // ML Kit 바코드 스캐너
    implementation(libs.mlkit.barcode.scanning)

    //fireStore
    implementation(libs.firebase.storage)

    // Login
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("org.jsoup:jsoup:1.13.1")
    implementation("com.google.firebase:firebase-database:20.0.3")

    // image 불러오기
    implementation("com.github.bumptech.glide:glide:4.15.0")
    implementation(libs.coordinatorlayout)
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.0")

    // androidx
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("androidx.recyclerview:recyclerview:1.3.0")
    implementation ("androidx.gridlayout:gridlayout:1.0.0")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}