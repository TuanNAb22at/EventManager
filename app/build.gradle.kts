plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.eventmanager"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.eventmanager"
        minSdk = 24
        targetSdk = 36
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

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    // Cấu hình chia nhỏ thư mục layout chuyên nghiệp
    sourceSets {
        getByName("main") {
            res.srcDirs(
                "src/main/res-layouts/auth",
                "src/main/res-layouts/main",
                "src/main/res-layouts/event",
                "src/main/res-layouts/items",
                "src/main/res-layouts/profile",
                "src/main/res" // Luôn giữ res mặc định
            )
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.core.splashscreen)
    
    // Room
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // Lifecycle
    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.viewmodel)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
