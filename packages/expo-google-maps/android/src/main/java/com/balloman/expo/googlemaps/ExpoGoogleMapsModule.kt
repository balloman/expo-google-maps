package com.balloman.expo.googlemaps

import android.annotation.SuppressLint
import com.balloman.expo.googlemaps.views.ExpoMapView
import com.balloman.expo.googlemaps.views.ExpoMarkerView
import expo.modules.kotlin.functions.Coroutine
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

/** Expo module for the markers, since they need to be separate */
class ExpoGoogleMapsMarkerModule : Module() {
  /**
   * Creates the module definition for the ExpoGoogleMapsMarker module.
   *
   * @return A ModuleDefinition that registers the "ExpoGoogleMapsMarker" module and exposes ExpoMarkerView with the "onMarkerPress" event.
   */
  override fun definition() = ModuleDefinition {
    Name("ExpoGoogleMapsMarker")

    View(ExpoMarkerView::class) { Events("onMarkerPress") }
  }
}

class ExpoGoogleMapsModule : Module() {
  // Each module class must implement the definition function. The definition consists of components
  // that describes the module's functionality and behavior.
  /**
   * Creates the ModuleDefinition for the ExpoGoogleMaps module, registering its name, functions, and view bindings.
   *
   * The definition registers:
   * - Module name "ExpoGoogleMaps".
   * - A no-op `setApiKey` function.
   * - A view binding for `ExpoMapView` exposing events `onDidChange` and `onMapIdle` and async functions `animateCamera` and `fitToBounds`.
   *
   * @return The constructed ModuleDefinition for the ExpoGoogleMaps module.
   */
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
