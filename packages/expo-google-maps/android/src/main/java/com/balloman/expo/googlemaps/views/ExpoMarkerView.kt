package com.balloman.expo.googlemaps.views

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.drawToBitmap
import com.balloman.expo.googlemaps.MarkerRecord
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import expo.modules.kotlin.AppContext
import expo.modules.kotlin.viewevent.EventDispatcher
import expo.modules.kotlin.views.ComposeProps
import expo.modules.kotlin.views.ExpoComposeView

data class ExpoMarkerViewProps(
    val marker: MutableState<MarkerRecord> = mutableStateOf(MarkerRecord()),
    val tracksViewChanges: MutableState<Boolean> = mutableStateOf(false),
) : ComposeProps

/** A class for the marker view in Android using Jetpack Compose. */
@SuppressLint("ViewConstructor")
class ExpoMarkerView(context: Context, appContext: AppContext) :
    ExpoComposeView<ExpoMarkerViewProps>(context, appContext, withHostingView = true) {
  override val props = ExpoMarkerViewProps()
  private val children: MutableState<List<View>> = mutableStateOf(listOf())
  private val onMarkerPress by EventDispatcher()

  @Composable
  fun MarkerComposableWrapper() {
    val markerRecord by props.marker
    val markerState = remember { MarkerState() }
    LaunchedEffect(markerRecord.position) {
      markerState.position = markerRecord.position.toLatLng()
    }
    val relevantView = children.value.firstOrNull()
    val viewBitmap =
        if (props.tracksViewChanges.value) relevantView?.drawToBitmap()
        else remember { relevantView?.drawToBitmap() }
    Marker(
        title = markerRecord.title,
        tag = markerRecord.key,
        state = markerState,
        icon = viewBitmap?.let { BitmapDescriptorFactory.fromBitmap(it) },
        onClick = {
          onMarkerPress(emptyMap())
          return@Marker true
        },
    )
  }

  @Composable
  override fun Content(modifier: Modifier) {
    // no-op
  }

  override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
    if (child is ComposeView) {
      super.addView(child, index, params)
    } else {
      children.value += child
    }
  }
}
