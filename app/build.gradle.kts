plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "es.uma.quiziosity"
    compileSdk = 34

    defaultConfig {
        applicationId = "es.uma.quiziosity"
        minSdk = 34
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        android.buildFeatures.buildConfig = true
        buildConfigField("String", "DEEPL_API_KEY", "\"${project.findProperty("DEEPL_API_KEY") ?: ""}\"")
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
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.recyclerview)

    // https://mvnrepository.com/artifact/androidx.security/security-crypto
    implementation(libs.androidx.security.crypto)

    // https://mvnrepository.com/artifact/com.squareup.retrofit2/retrofit
    implementation(libs.retrofit)

    // https://mvnrepository.com/artifact/com.squareup.retrofit2/converter-gson
    implementation (libs.converter.gson)

    // https://mvnrepository.com/artifact/com.deepl.api/deepl-java
    implementation(libs.deepl.java)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.preference.ktx)







    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.room.runtime)

    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    // See Add the KSP plugin to your project
    ksp(libs.androidx.room.compiler)

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)

    // https://mvnrepository.com/artifact/at.favre.lib/bcrypt
    implementation(libs.bcrypt)


}

