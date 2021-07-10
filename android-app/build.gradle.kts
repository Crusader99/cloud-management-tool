import com.android.build.gradle.internal.tasks.factory.dependsOn

plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.3")
    defaultConfig {
        applicationId = rootProject.group.toString()
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode = 1
        versionName = rootProject.version.toString()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            val file = getDefaultProguardFile("proguard-android-optimize.txt")
            proguardFiles(file, "proguard-rules.pro")
        }
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

// Choose 'jvm' from disambiguating targets
configurations.all {
    val attr = Attribute.of("de.crusader.targetAttribute", String::class.java)
    attributes.attribute(attr, "android")
}

// Copy web-app to local cache
val websiteCopy by tasks.registering(Copy::class) {
    dependsOn(":web-app:build")
    from("../web-app/build/artifact-js")
    into("src/main/assets/www")
}

tasks.assemble.dependsOn(websiteCopy)

dependencies {
    implementation(project(":common"))

    // https://developer.android.com/jetpack/androidx/migrate/artifact-mappings
    implementation("androidx.webkit:webkit:1.4.0") // Used to provide android web-browser
    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
