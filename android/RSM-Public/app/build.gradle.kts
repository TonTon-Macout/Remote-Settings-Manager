plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "dev.vanila.rsm"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.vanila.rsm"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.2.0"

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
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
        languageVersion = "1.9"
    }

    packaging {
        resources {
            excludes += "/META-INF/{INDEX.LIST,DEPENDENCIES,NOTICE,LICENSE,NOTICE.txt,LICENSE.txt,io.netty.versions.properties}"
        }
    }
}

dependencies {
    // Основные
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Для CoordinatorLayout и Material
    implementation(libs.androidx.coordinatorlayout)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.json)
    implementation(libs.jsoup)
    implementation(libs.androidsvg)
    implementation (libs.androidx.swiperefreshlayout)

    implementation(libs.sliding.panel)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.core.animation)
    implementation(libs.firebase.appdistribution.gradle)


    // Тесты
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    implementation ("com.github.skydoves:colorpickerview:2.2.4")
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation("com.github.lihangleo2:ShadowLayout:3.4.1")
    implementation ("com.google.code.gson:gson:2.11.0")

    implementation("com.github.xiaogegexiao:rxbonjour:1.0.8")

    implementation ("com.github.Dimezis:BlurView:version-3.2.0")



    implementation ("com.google.android.gms:play-services-location:21.3.0")

    implementation("com.github.skydoves:balloon:1.6.4")

}
configurations.all {
    resolutionStrategy {
        eachDependency {
            if (requested.group == "org.jetbrains.kotlin" && requested.name.startsWith("kotlin-")) {
                useVersion(libs.versions.kotlin.get()) 
            }
        }
    }
}