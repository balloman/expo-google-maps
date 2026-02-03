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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.core.view.children
import androidx.core.view.drawToBitmap
import com.balloman.expo.googlemaps.MarkerRecord
import com.balloman.expo.googlemaps.jsLog
import com.facebook.react.uimanager.PixelUtil.pxToDp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.MarkerState.Companion.invoke
import expo.modules.kotlin.AppContext
import expo.modules.kotlin.views.ComposeProps
import expo.modules.kotlin.views.ExpoComposeView

data class ExpoMarkerViewProps(
    val marker: MutableState<MarkerRecord> = mutableStateOf(MarkerRecord())
) : ComposeProps

/** A class for the marker view in Android using Jetpack Compose. */
@SuppressLint("ViewConstructor")
class ExpoComposeMarkerView(context: Context, appContext: AppContext) :
    ExpoComposeView<ExpoMarkerViewProps>(context, appContext, withHostingView = true) {
  override val props = ExpoMarkerViewProps()
  val children: MutableState<List<View>> = mutableStateOf(listOf())

  @Composable
  fun MarkerComposableWrapper() {
    val markerRecord by props.marker
    if (markerRecord.key == "2") {
      jsLog("Rerendering...")
    }
    val markerState = remember(markerRecord.key) { MarkerState() }
    LaunchedEffect(markerRecord.position) {
      markerState.position = markerRecord.position.toLatLng()
    }
    Marker(
      title = markerRecord.title,
      tag = markerRecord.key,
      state = markerState,
      icon = BitmapDescriptorFactory.fromBitmap(viewBitmap),
    )
    when (val subviewValue = children.value.firstOrNull()) {
      null -> Marker(title = markerRecord.title, tag = markerRecord.key, state = markerState)
      else -> {
        val viewBitmap = subviewValue.drawToBitmap()
        val height = max(subviewValue.height.toFloat().pxToDp().dp, 100f.pxToDp().dp)
        val width = max(subviewValue.width.toFloat().pxToDp().dp, 100f.pxToDp().dp)
        //        MarkerComposable(
        //            title = markerRecord.title,
        //            tag = markerRecord.key,
        //            state = markerState,
        //        ) {
        //          AndroidView(
        //              factory = { subviewValue },
        //              Modifier.size(
        //                  width,
        //                  height,
        //              ),
        //          )
        //        }
        Marker(
            title = markerRecord.title,
            tag = markerRecord.key,
            state = markerState,
            icon = BitmapDescriptorFactory.fromBitmap(viewBitmap),
        )
      }
    }
  }

  @Composable
  override fun Content(modifier: Modifier) {
    //    children.forEach { jsLog(it::class.qualifiedName) }
  }

  override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
    if (child is ComposeView) {
      super.addView(child, index, params)
    } else {
      children.value += child
    }
  }
}
