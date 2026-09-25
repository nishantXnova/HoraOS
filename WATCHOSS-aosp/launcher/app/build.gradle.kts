plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
}
android {
  namespace = "com.horaos.launcher"
  compileSdk = 34
  defaultConfig {
    applicationId = "com.horaos.launcher"
    minSdk = 30 // Wear OS 3+ (TicWatch Pro 3, Fossil Gen6)
    targetSdk = 34
    versionCode = 1
    versionName = "0.1"
  }
  buildTypes { release { isMinifyEnabled = true } }
  buildFeatures { compose = true }
  composeOptions { kotlinCompilerExtensionVersion = "1.5.14" }
  compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
  kotlinOptions { jvmTarget = "17" }
}
dependencies {
  val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
  implementation(composeBom); androidTestImplementation(composeBom)
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.foundation:foundation")
  implementation("androidx.compose.foundation:foundation-layout")
  implementation("androidx.core:core-ktx:1.13.1")
  implementation("androidx.wear.compose:compose-material:1.3.1")
  implementation("androidx.wear.compose:compose-foundation:1.3.1")
  implementation("androidx.core:core-splashscreen:1.0.1")
  implementation("androidx.activity:activity-compose:1.9.2")
  testImplementation("junit:junit:4.13.2")
}
