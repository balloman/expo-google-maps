package com.balloman.expo.googlemaps.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import com.balloman.expo.googlemaps.AnimateOptions
import com.balloman.expo.googlemaps.Camera
import com.balloman.expo.googlemaps.MS_TO_SECONDS
import com.balloman.expo.googlemaps.PolygonRecord
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import expo.modules.kotlin.AppContext
import expo.modules.kotlin.viewevent.EventDispatcher
import expo.modules.kotlin.views.ExpoView

@SuppressLint("ViewConstructor")
class ExpoMapView(context: Context, appContext: AppContext) :
    ExpoView(context, appContext),
    OnMapReadyCallback,
    OnCameraIdleListener,
    OnCameraMoveListener,
    OnMarkerClickListener {
  private var polygonMap: MutableMap<String, Polygon> = mutableMapOf()
  private var tempPolygons: List<PolygonRecord> = listOf()
  private var tempMarkers: MutableList<ExpoMarkerView> = mutableListOf()
  private var markers: MutableMap<Marker, ExpoMarkerView> = hashMapOf()
  val mapOptions: GoogleMapOptions = GoogleMapOptions()
  var googleMap: GoogleMap? = null
  var styleJson: String? = null
  var camera: Camera? = null
  var showUserLocation: Boolean = false

  val onMapIdle by EventDispatcher()
  val onDidChange by EventDispatcher()

  init {
    Log.d("ExpoGoogleMapsModule", "ExpoMapView")
    mapView = MapView(context, mapOptions)
    mapView?.onCreate(null)
    mapView?.getMapAsync(this)
    mapView?.onStart()
    addView(mapView)
  }

  @Suppress("ReturnCount")
  override fun onViewAdded(child: View?) {
    // This lets us add the markers temporarily, while waiting for the map to be ready
    if (child is ExpoMarkerView && googleMap == null) {
      tempMarkers.add(child)
      return
    } else if (child is ExpoMarkerView) {
      val marker = googleMap!!.addMarker(child.toMarkerOptions())
      if (marker == null) {
        Log.e("ExpoGoogleMapsModule", "Failed to add marker")
        return
      }
      child.gmsMarker = marker
      markers[marker] = child
      return
    }
    super.onViewAdded(child)
  }

  @SuppressLint("MissingPermission", "PotentialBehaviorOverride")
  override fun onMapReady(map: GoogleMap) {
    Log.e("ExpoGoogleMapsModule", "onMapReady")
    googleMap = map
    map.uiSettings.isMyLocationButtonEnabled = false
    styleJson?.let { map.setMapStyle(MapStyleOptions(it)) }
    camera?.let { map.moveCamera(CameraUpdateFactory.newCameraPosition(it.toGmsCameraPosition())) }
    map.isMyLocationEnabled = showUserLocation
    updatePolygons(tempPolygons)
    tempMarkers.forEach { markerView ->
      val options = markerView.toMarkerOptions()
      val marker = map.addMarker(options)
      if (marker == null) {
        Log.e("ExpoGoogleMapsModule", "Failed to add marker")
        return
      }
      markerView.gmsMarker = marker
      markers[marker] = markerView
    }
    map.setOnCameraIdleListener(this)
    map.setOnCameraMoveListener(this)
    map.setOnMarkerClickListener(this)
  }

  override fun onCameraIdle() {
    onMapIdle(mapOf("cameraPosition" to Camera.fromGmsCameraPosition(googleMap!!.cameraPosition)))
  }

  override fun onCameraMove() {
    onDidChange(mapOf("cameraPosition" to Camera.fromGmsCameraPosition(googleMap!!.cameraPosition)))
  }

  override fun onMarkerClick(p0: Marker): Boolean {
    val markerView = markers[p0]
    if (markerView == null) {
      Log.e("ExpoGoogleMapsModule", "Failed to find marker")
      return false
    }
    markerView.onMarkerPress(mapOf())
    return false
  }

  /**
   * Updates the polygons on the map, making sure to only add, remove, or update the polygons that
   * have changed
   */
  fun updatePolygons(polygonRecords: List<PolygonRecord>) {
    if (googleMap == null) {
      tempPolygons = polygonRecords
      return
    }
    if (polygonRecords.isEmpty()) {
      polygonMap.values.forEach { polygon -> polygon.remove() }
      polygonMap.clear()
      return
    }
    val polygonRecordMap = polygonRecords.associateBy { polygon -> polygon.key }
    val passedKeys = polygonRecordMap.keys.toHashSet()
    val currentKeys = this.polygonMap.keys.toHashSet()
    val keysToRemove = currentKeys.subtract(passedKeys)
    val keysToAdd = passedKeys.subtract(currentKeys)
    val keysToUpdate = passedKeys.intersect(currentKeys)
    keysToRemove.forEach { key ->
      this.polygonMap[key]?.remove()
      this.polygonMap.remove(key)
    }
    keysToAdd.forEach { key ->
      polygonRecordMap[key]?.let { this.polygonMap[key] = googleMap!!.addPolygon(it.toOptions()) }
    }
    keysToUpdate.forEach { key ->
      polygonRecordMap[key]?.let {
        this.polygonMap[key]?.remove()
        this.polygonMap[key] = googleMap!!.addPolygon(it.toOptions())
      }
    }
  }

  /**
   * Fits the map to the bounds provided
   *
   * @param topRight The top right bound
   * @param bottomLeft The bottom left bound
   * @param padding The amount of padding to add between the edge and the bound
   * @param animateOptions The animation to use for the fitting
   */
  fun fitToBounds(
      topRight: LatLng,
      bottomLeft: LatLng,
      padding: Int,
      animateOptions: AnimateOptions,
  ) {
    val bounds = LatLngBounds.builder().include(topRight).include(bottomLeft).build()
    val update = CameraUpdateFactory.newLatLngBounds(bounds, padding)
    val duration = (animateOptions.animationDuration * MS_TO_SECONDS).toInt()
    googleMap?.animateCamera(update, duration, null)
  }

  /**
   * Sets the map Id
   *
   * @param mapId The map id to use
   */
  fun setMapId(mapId: String?) {
    mapOptions.mapId(mapId ?: "")
  }

  private fun PolygonRecord.toOptions(): PolygonOptions {
    val fillColor = Color.parseColor(this.fillColor)
    val strokeColor = Color.parseColor(this.strokeColor)
    return PolygonOptions()
        .fillColor(fillColor)
        .strokeColor(strokeColor)
        .addAll(this.coordinates.map { coordinate -> coordinate.toLatLng() })
  }

  companion object {
    var mapView: MapView? = null
  }
}
