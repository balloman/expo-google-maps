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

  @Composable
  private fun MapPolygons(polygonState: List<PolygonRecord>) {
    polygonState.forEach {
      val fillColor =
          it.fillColor?.takeIf { it.isNotEmpty() }?.toColorInt()
              ?: android.graphics.Color.TRANSPARENT
      val strokeColor =
          it.strokeColor?.takeIf { it.isNotEmpty() }?.toColorInt()
              ?: android.graphics.Color.TRANSPARENT
      Polygon(
          points = it.points,
          fillColor = Color(fillColor),
          strokeColor = Color(strokeColor),
          tag = it.key,
      )
    }
  }

  @Composable
  private fun MapMarkers() {
    children.filterIsInstance<ExpoMarkerView>().forEach { it.MarkerComposableWrapper() }
  }

  suspend fun animateCamera(camera: Camera, options: AnimateOptions?) {
    val cameraUpdate = CameraUpdateFactory.newCameraPosition(camera.toGmsCameraPosition())
    cameraState.animate(
        cameraUpdate,
        options?.animationDuration?.seconds?.inWholeMilliseconds?.toInt() ?: Int.MAX_VALUE,
    )
  }

  suspend fun fitToBounds(options: FitToBoundsParams, animateOptions: AnimateOptions?) {
    val bounds = options.toLatLngBounds()
    val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds.bounds, bounds.padding)
    cameraState.animate(
        cameraUpdate,
        animateOptions?.animationDuration?.seconds?.inWholeMilliseconds?.toInt() ?: Int.MAX_VALUE,
    )
  }
}

fun CameraPositionState.toRecord(): Camera = Camera.fromGmsCameraPosition(this.position)
