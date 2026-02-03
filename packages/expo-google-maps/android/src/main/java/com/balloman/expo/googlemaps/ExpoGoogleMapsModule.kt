package com.balloman.expo.googlemaps

import android.annotation.SuppressLint
import com.balloman.expo.googlemaps.views.ExpoMapView
import com.balloman.expo.googlemaps.views.ExpoMarkerView
import expo.modules.kotlin.functions.Coroutine
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

/** Expo module for the markers, since they need to be separate */
class ExpoGoogleMapsMarkerModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("ExpoGoogleMapsMarker")

    View(ExpoMarkerView::class) { Events("onMarkerPress") }
  }
}

class ExpoGoogleMapsModule : Module() {
  // Each module class must implement the definition function. The definition consists of components
  // that describes the module's functionality and behavior.
  // See https://docs.expo.dev/modules/module-api for more details about available components.
  @SuppressLint("MissingPermission")
  @Suppress("LongMethod")
  override fun definition() = ModuleDefinition {
    // Sets the name of the module that JavaScript code will use to refer to the module. Takes a
    // string as an argument.
    // Can be inferred from module's class name, but it's recommended to set it explicitly for
    // clarity.
    // The module will be accessible from `requireNativeModule('ExpoGoogleMaps')` in JavaScript.
    Name("ExpoGoogleMaps")

    Function("setApiKey") { _: String? ->
      // No-op
    }

    View(ExpoMapView::class) {
      Events("onDidChange", "onMapIdle")

      AsyncFunction("animateCamera") Coroutine
          { view: ExpoMapView, camera: Camera, animationOptions: AnimateOptions? ->
            view.animateCamera(camera, animationOptions)
          }

      AsyncFunction("fitToBounds") Coroutine
          { view: ExpoMapView, options: FitToBoundsParams, animateOptions: AnimateOptions? ->
            view.fitToBounds(options, animateOptions)
          }
    }
  }
}
