plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlinx.kover")
}

android {
    namespace = "com.example.walmart.presentation"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    testOptions {
        unitTests.isReturnDefaultValues = true
        unitTests.isIncludeAndroidResources = true
        animationsDisabled = true // For better consistency in UI tests
    }
}

dependencies {
    implementation(project(":domain"))

    // Robolectric
    testImplementation ("org.robolectric:robolectric:4.11.1")

    // SLF4J Logger
    testImplementation ("org.slf4j:slf4j-simple:1.7.36")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${libs.versions.coroutines.get()}")

    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")

    // Kotlin test
    testImplementation ("org.jetbrains.kotlin:kotlin-test:1.8.20")
    testImplementation ("org.jetbrains.kotlin:kotlin-test-junit:1.8.20")

    // Other test dependencies
    testImplementation ("junit:junit:4.13.2")
    testImplementation ("org.mockito:mockito-core:4.8.0")
    testImplementation ("org.mockito.kotlin:mockito-kotlin:4.0.0")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation ("androidx.arch.core:core-testing:2.1.0")

    // Navigation dependencies
    val navVersion = "2.5.3"
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

    // UI and support libraries
    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Unit test dependencies
    testImplementation("com.google.truth:truth:1.1.3")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.2.0")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("org.robolectric:robolectric:4.9")
    testImplementation("androidx.test.ext:junit:1.1.5")

    testImplementation("androidx.navigation:navigation-testing:$navVersion")

    // Espresso for UI testing
    val espressoVersion = "3.5.1"
    testImplementation("androidx.test.espresso:espresso-core:$espressoVersion")
    testImplementation("androidx.test.espresso:espresso-contrib:$espressoVersion")
    testImplementation("androidx.test.espresso:espresso-intents:$espressoVersion")
    testImplementation("androidx.test.espresso:espresso-accessibility:$espressoVersion")
    testImplementation("androidx.test.espresso:espresso-web:$espressoVersion")
    testImplementation("androidx.test.espresso.idling:idling-concurrent:$espressoVersion")

    testImplementation ("androidx.arch.core:core-testing:2.1.0")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")


    // Fragment Testing
    debugImplementation("androidx.fragment:fragment-testing:1.5.7")

    // Navigation Testing
    androidTestImplementation("androidx.navigation:navigation-testing:2.5.3")

    // Mockito for Android
    androidTestImplementation("org.mockito:mockito-android:5.2.0")
    androidTestImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")

}

koverReport {
    // Filters for all report types of all build variants
    filters {
        excludes {
            classes(
                "*.databinding.*",
                "*.BuildConfig"
            )
        }
    }
}
