package expo.modules.googlemaps.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import expo.modules.googlemaps.AnimateOptions
import expo.modules.googlemaps.Camera
import expo.modules.googlemaps.PolygonRecord
import expo.modules.kotlin.AppContext
import expo.modules.kotlin.viewevent.EventDispatcher
import expo.modules.kotlin.views.ExpoView

@SuppressLint("ViewConstructor")
class ExpoMapView(context: Context, appContext: AppContext) : ExpoView(context, appContext),
    OnMapReadyCallback, OnCameraIdleListener, OnCameraMoveListener {
    private var polygonMap: MutableMap<String, Polygon> = mutableMapOf()
    private var tempPolygons: List<PolygonRecord> = listOf()
    private var tempMarkers: MutableList<ExpoMarkerView> = mutableListOf()
    var googleMap: GoogleMap? = null

    // We need to store the polygons in a temp variable until the map is ready
    var styleJson: String? = null
    var camera: Camera? = null
    var showUserLocation: Boolean = false

    val onMapIdle by EventDispatcher()
    val onDidChange by EventDispatcher()

    init {
        Log.d("ExpoGoogleMapsModule", "ExpoMapView")
        mapView = MapView(context)
        mapView?.onCreate(null)
        mapView?.getMapAsync(this)
        mapView?.onStart()
        addView(mapView)
    }

    override fun onViewAdded(child: View?) {
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
            child.gmsMap = googleMap
            return
        }
        super.onViewAdded(child)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        Log.e("ExpoGoogleMapsModule", "onMapReady")
        googleMap = map
        googleMap!!.uiSettings.isMyLocationButtonEnabled = false
        if (styleJson != null) {
            googleMap!!.setMapStyle(MapStyleOptions(styleJson!!))
        }
        if (camera != null) {
            googleMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(camera!!.toGmsCameraPosition()))
        }
        if (showUserLocation) {
            googleMap!!.isMyLocationEnabled = true
        }
        updatePolygons(tempPolygons)
        tempMarkers.forEach { markerView ->
            val options = markerView.toMarkerOptions()
            val marker = googleMap!!.addMarker(options)
            if (marker == null) {
                Log.e("ExpoGoogleMapsModule", "Failed to add marker")
                return
            }
            markerView.gmsMarker = marker
            markerView.gmsMap = googleMap
        }
        googleMap!!.setOnCameraIdleListener(this)
        googleMap!!.setOnCameraMoveListener(this)
    }

    override fun onCameraIdle() {
        onMapIdle(
            mapOf(
                "cameraPosition" to Camera.fromGmsCameraPosition(googleMap!!.cameraPosition)
            )
        )
    }

    override fun onCameraMove() {
        onDidChange(
            mapOf(
                "cameraPosition" to Camera.fromGmsCameraPosition(googleMap!!.cameraPosition)
            )
        )
    }

    /**
     * Updates the polygons on the map, making sure to only add, remove, or update the polygons that have changed
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
            polygonRecordMap[key]?.let {
                this.polygonMap[key] = googleMap!!.addPolygon(it.toOptions())
            }
        }
        keysToUpdate.forEach { key ->
            polygonRecordMap[key]?.let {
                this.polygonMap[key]?.remove()
                this.polygonMap[key] = googleMap!!.addPolygon(it.toOptions())
            }
        }
    }

    fun fitToBounds(
        topRight: LatLng,
        bottomLeft: LatLng,
        padding: Int,
        animateOptions: AnimateOptions
    ) {
        val bounds = LatLngBounds.builder()
            .include(topRight)
            .include(bottomLeft)
            .build()
        val update = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        val duration = (animateOptions.animationDuration * 1000).toInt()
        googleMap?.animateCamera(update, duration, null)
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