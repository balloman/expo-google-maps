package com.balloman.expo.googlemaps

import android.annotation.SuppressLint
import android.util.Log
import com.balloman.expo.googlemaps.views.ExpoMapView
import com.balloman.expo.googlemaps.views.ExpoMarkerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MapStyleOptions
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import kotlin.math.roundToInt

/** Expo module for the markers, since they need to be separate */
class ExpoGoogleMapsMarkerModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("ExpoGoogleMapsMarker")

    View(ExpoMarkerView::class) {
      Prop("marker") { view: ExpoMarkerView, marker: MarkerRecord -> view.updateMarker(marker) }

      Events("onMarkerPress")
    }
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
    Log.d("ExpoGoogleMapsModule", "LOADED")

    // Defines event names that the module can send to JavaScript.
    Events("log")

    Function("setApiKey") { _: String? ->
      // No-op
    }

    // Enables the module to be used as a native view. Definition components that are accepted as
    // part of
    // the view definition: Prop, Events.
    View(ExpoMapView::class) {
      Events("onMapIdle", "onDidChange")

      Prop("camera") { view: ExpoMapView, camera: Camera -> view.camera = camera }

      Prop("polygons") { view: ExpoMapView, polygons: Array<PolygonRecord> ->
        view.updatePolygons(polygons.toList())
      }

      Prop("styleJson") { view: ExpoMapView, styleJson: String? ->
        if (view.googleMap == null) {
          view.styleJson = styleJson
          return@Prop
        }
        if (styleJson == null) {
          view.googleMap?.setMapStyle(null)
          return@Prop
        }
        val success = view.googleMap?.setMapStyle(MapStyleOptions(styleJson))
        if (!success!!) {
          sendEvent("log", mapOf("error" to "Failed to set style"))
        }
      }

      Prop("showUserLocation") { view: ExpoMapView, showUserLocation: Boolean? ->
        if (view.googleMap == null) {
          view.showUserLocation = showUserLocation!!
          return@Prop
        }
        if (showUserLocation == null) {
          view.googleMap?.isMyLocationEnabled = false
          return@Prop
        }
        view.googleMap?.isMyLocationEnabled = showUserLocation
      }

      Prop("mapId") { view, mapId: String? -> view.setMapId(mapId) }

      AsyncFunction("animateCamera") {
          view: ExpoMapView,
          camera: Camera,
          animationOptions: AnimateOptions? ->
        if (animationOptions == null) {
          view.googleMap?.moveCamera(
              CameraUpdateFactory.newCameraPosition(camera.toGmsCameraPosition())
          )
        } else {
          view.googleMap?.animateCamera(
              CameraUpdateFactory.newCameraPosition(camera.toGmsCameraPosition()),
              (animationOptions.animationDuration * 1000).roundToInt(),
              null,
          )
        }
      }

      AsyncFunction("fitToBounds") {
          view: ExpoMapView,
          params: FitToBoundsParams,
          animationOptions: AnimateOptions? ->
        val topRight = params.topRight.toLatLng()
        val bottomLeft = params.bottomLeft.toLatLng()
        var padding = 0
        if (params.insets != null) {
          padding =
              arrayOf(
                      params.insets.top.roundToInt(),
                      params.insets.left.roundToInt(),
                      params.insets.bottom.roundToInt(),
                      params.insets.right.roundToInt(),
                  )
                  .max()
        }
        if (animationOptions == null) {
          view.fitToBounds(topRight, bottomLeft, padding, AnimateOptions())
        } else {
          view.fitToBounds(topRight, bottomLeft, padding, animationOptions)
        }
      }
    }
  }
}
