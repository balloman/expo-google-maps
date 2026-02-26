import groovy.json.JsonSlurper
import groovy.lang.Closure
import java.io.File

plugins {
  id("com.android.library")
  id("com.diffplug.spotless") version "8.2.1"
  id("dev.detekt") version "2.0.0-alpha.2"
  id("org.jetbrains.kotlin.plugin.compose") version "2.1.20"
}

val packageJson =
    JsonSlurper().parseText(File(projectDir.parentFile, "package.json").readText())
        as Map<String, Any>

group = "com.balloman.expo.googlemaps"

version = packageJson["version"] as String

val expoModulesCorePlugin =
    File(project(":expo-modules-core").projectDir.absolutePath, "ExpoModulesCorePlugin.gradle")

apply(from = expoModulesCorePlugin)

/**
 * Invokes a Closure stored in the project's extra properties by key.
 *
 * Looks up the closure stored under [name] in `project.extra` and calls it.
 *
 * @param name The key of the Closure in `project.extra` to invoke.
 */
fun callExpoClosure(name: String) {
  (project.extra[name] as Closure<*>).call()
}

callExpoClosure("applyKotlinExpoModulesCorePlugin")

callExpoClosure("useCoreDependencies")

callExpoClosure("useExpoPublishing")

android {
  namespace = "com.balloman.expo.googlemaps"
  lint { abortOnError = false }
  buildFeatures { compose = true }
}

// If you want to use the managed Android SDK versions from expo-modules-core, set this to true.
// The Android SDK versions will be bumped from time to time in SDK releases and may introduce
// breaking changes in your module code.
// Most of the time, you may like to manage the Android SDK versions yourself.
val useManagedAndroidSDKVersions = true

if (useManagedAndroidSDKVersions) {
  callExpoClosure("useDefaultAndroidSdkVersions")
} else {
  // Simple helper that allows the root project to override versions declared by this library.
  /**
   * Retrieve an integer property from the root project's extra properties, falling back when absent.
   *
   * @param prop The name of the extra property to read from the root project.
   * @param fallback The value to return when the property is not present.
   * @return The property's value parsed as an `Int` if present, otherwise `fallback`.
   */
  fun safeExtGet(prop: String, fallback: Int): Int {
    val rootExt = rootProject.extra
    return if (rootExt.has(prop)) {
      val value = rootExt[prop]
      if (value is Number) value.toInt() else value.toString().toInt()
    } else {
      fallback
    }
  }

  android {
    compileSdk = safeExtGet("compileSdkVersion", 34)
    defaultConfig {
      minSdk = safeExtGet("minSdkVersion", 24)
      targetSdk = safeExtGet("targetSdkVersion", 34)
    }
  }
}

repositories {
  gradlePluginPortal()
  mavenCentral()
  google()
}

dependencies {
  implementation("com.google.android.gms:play-services-maps:20.0.0")
  implementation("com.google.maps.android:android-maps-utils:4.0.0")
  implementation("com.google.maps.android:maps-compose:6.10.0")
  val composeBom = platform("androidx.compose:compose-bom:2026.01.01")
  implementation(composeBom)
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.foundation:foundation")
  implementation("com.facebook.react:react-android:0.81.5")
}

spotless {
  kotlin {
    target("src/**/*.kt")
    ktfmt()
  }
}

detekt {
  source.setFrom("src/main/java")
  basePath.set(projectDir)
  buildUponDefaultConfig = true
  config.setFrom(file("config/detekt/detekt.yml"))
}
