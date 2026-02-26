package com.balloman.expo.googlemaps.views

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import androidx.core.view.children
import com.balloman.expo.googlemaps.AnimateOptions
import com.balloman.expo.googlemaps.Camera
import com.balloman.expo.googlemaps.Coordinate
import com.balloman.expo.googlemaps.FitToBoundsParams
import com.balloman.expo.googlemaps.OnDidChangeEvent
import com.balloman.expo.googlemaps.PolygonRecord
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Polygon
import expo.modules.kotlin.AppContext
import expo.modules.kotlin.viewevent.EventDispatcher
import expo.modules.kotlin.views.ComposeProps
import expo.modules.kotlin.views.ExpoComposeView
import kotlin.time.Duration.Companion.seconds

data class ExpoMapViewProps(
    val camera: MutableState<Camera> = mutableStateOf(Camera(Coordinate(0.0, 0.0), 0.0, 0.0, 0.0)),
    val polygons: MutableState<List<PolygonRecord>> = mutableStateOf(listOf()),
    val styleJson: MutableState<String?> = mutableStateOf(null),
    val showUserLocation: MutableState<Boolean> = mutableStateOf(false),
    val mapId: MutableState<String?> = mutableStateOf(null),
) : ComposeProps

/**
 * A class for the Map View in Android using Jetpack Compose. Heavily inspired by the work done in
 * the expo-maps alpha
 */
@SuppressLint("ViewConstructor")
class ExpoMapView(context: Context, appContext: AppContext) :
    ExpoComposeView<ExpoMapViewProps>(context, appContext, withHostingView = true) {
  override val props = ExpoMapViewProps()

  private val onMapIdle by EventDispatcher<OnDidChangeEvent>()
  private val onDidChange by EventDispatcher<OnDidChangeEvent>()
  private val mapOptions = GoogleMapOptions().apply { props.mapId.value?.let { mapId(it) } }
  private val markerViews = mutableStateListOf<ExpoMarkerView>()
  private lateinit var cameraState: CameraPositionState
  private var wasLoaded = mutableStateOf(false)

  /**
   * Renders the map UI bound to the current ExpoMapViewProps, manages the map's camera state,
   * and dispatches map events while displaying polygons and markers.
   *
   * The composable reads camera, style, user-location, and polygon/marker props to configure
   * the GoogleMap and emits idle/changed events when the map finishes loading or the camera updates.
   */
  @Composable
  override fun Content(modifier: Modifier) {
    cameraState = updateCameraState()

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraState,
        onMapLoaded = {
          onMapIdle(OnDidChangeEvent(props.camera.value))
          wasLoaded.value = true
        },
        googleMapOptionsFactory = { mapOptions },
        properties =
            MapProperties(
                isMyLocationEnabled = props.showUserLocation.value,
                mapStyleOptions = props.styleJson.value?.let(::MapStyleOptions),
            ),
    ) {
      MapPolygons(props.polygons.value)
      MapMarkers()
    }
  }

  /**
   * Creates and returns a CameraPositionState bound to props.camera while emitting map camera events.
   *
   * The returned state is remembered for the current camera value. After the map has loaded, updates to
   * the camera position trigger an `onDidChange` event. When the camera stops moving, an `onMapIdle`
   * event is emitted.
   *
   * @return The CameraPositionState currently used by the map, initialized from `props.camera`.
   */
  @Composable
  private fun updateCameraState(): CameraPositionState {
    val cameraPosition = props.camera.value
    cameraState =
        remember(cameraPosition) {
          CameraPositionState(position = cameraPosition.toGmsCameraPosition())
        }

    LaunchedEffect(cameraState.position) {
      if (!wasLoaded.value) {
        return@LaunchedEffect
      }

      val position = cameraState.position
      onDidChange(OnDidChangeEvent(Camera.fromGmsCameraPosition(position)))
    }

    LaunchedEffect(cameraState.isMoving) {
      if (cameraState.isMoving) return@LaunchedEffect
      onMapIdle(OnDidChangeEvent(cameraState.toRecord()))
    }

    return cameraState
  }

  /**
   * Renders a Polygon composable for each record in [polygonState].
   *
   * Each PolygonRecord supplies the polygon's points, fill color, stroke color, and key which is used as the polygon tag.
   *
   * @param polygonState List of PolygonRecord objects describing polygons to render. Colors from each record are converted to Compose `Color`.
   */
  @Composable
  private fun MapPolygons(polygonState: List<PolygonRecord>) {
    polygonState.forEach {
      Polygon(
          points = it.points,
          fillColor = Color(it.fillColor.toColorInt()),
          strokeColor = Color(it.strokeColor.toColorInt()),
          tag = it.key,
      )
    }
  }

  /**
   * Renders each child ExpoMarkerView as a map marker.
   */
  @Composable
  private fun MapMarkers() {
    children.filterIsInstance<ExpoMarkerView>().forEach { it.MarkerComposableWrapper() }
  }

  /**
   * Animates the map camera to the specified camera position.
   *
   * @param camera Target camera position to animate to.
   * @param options Optional animation settings. If provided, uses `options.animationDuration` (converted from seconds to milliseconds) as the animation duration; if `null` or duration is not present, falls back to `Int.MAX_VALUE`.
   */
  suspend fun animateCamera(camera: Camera, options: AnimateOptions?) {
    val cameraUpdate = CameraUpdateFactory.newCameraPosition(camera.toGmsCameraPosition())
    cameraState.animate(
        cameraUpdate,
        options?.animationDuration?.seconds?.inWholeMilliseconds?.toInt() ?: Int.MAX_VALUE,
    )
  }

  /**
   * Moves the map camera to fit the given bounds and padding.
   *
   * Calculates a LatLngBounds from `options`, creates a camera update that includes the bounds' padding,
   * and animates the camera to show the bounds.
   *
   * @param options Parameters containing the target bounds and padding.
   * @param animateOptions Optional animation configuration; when present its `animationDuration` (in seconds) is converted to milliseconds and used for the camera animation. If `null`, a maximum integer duration is used.
   */
  suspend fun fitToBounds(options: FitToBoundsParams, animateOptions: AnimateOptions?) {
    val bounds = options.toLatLngBounds()
    val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds.bounds, bounds.padding)
    cameraState.animate(
        cameraUpdate,
        animateOptions?.animationDuration?.seconds?.inWholeMilliseconds?.toInt() ?: Int.MAX_VALUE,
    )
  }
}

/**
 * Converts the current position of a CameraPositionState into a Camera record.
 *
 * @return A `Camera` representing the underlying Google Maps camera position.
 */
fun CameraPositionState.toRecord(): Camera = Camera.fromGmsCameraPosition(this.position)
