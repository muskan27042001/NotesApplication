plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.example.notesapplication"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.notesapplication"
        minSdk = 24
        targetSdk = 23
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    dataBinding{
        android.buildFeatures.dataBinding=true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    //noinspection GradleCompatible,GradleCompatible,GradleCompatible
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")


    implementation("androidx.recyclerview:recyclerview:1.2.1")

    // RecyclerView Animator
    //implementation("jp.wasabeef:recyclerview-animators:3.0.0")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")


    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.0")

    // Annotation processor
    implementation("androidx.lifecycle:lifecycle-common-java8:2.3.1")

    //Room
//    implementation("androidx.room:room-runtime:2.3.0")
  //  implementation("androidx.room:room-compiler:2.3.0")

    implementation("androidx.room:room-runtime:2.5.2")
    kapt("androidx.room:room-compiler:2.5.2")


    //Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:2.5.2")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.1.1")

    // Navigation Components
    implementation("androidx.fragment:fragment-ktx:1.4.0-alpha10")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")

    //material Components
    implementation("com.google.android.material:material:1.5.0-alpha02")

    //color picker library
    implementation("com.thebluealliance:spectrum:0.7.1")

    implementation("com.yahiaangelo.markdownedittext:markdownedittext:1.1.1")
    implementation("io.noties.markwon:core:4.6.0")
    implementation("io.noties.markwon:html:4.6.0")
    implementation("io.noties.markwon:ext-strikethrough:4.6.0")
    implementation("io.noties.markwon:ext-tasklist:4.6.0")


    //easy permission

    implementation("pub.devrel:easypermissions:3.0.0")

    implementation("de.hdodenhof:circleimageview:3.1.0")

    implementation("androidx.work:work-runtime:2.7.0")


}
