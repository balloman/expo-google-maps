package com.balloman.expo.googlemaps.views

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.balloman.expo.googlemaps.MarkerRecord
import expo.modules.kotlin.AppContext
import expo.modules.kotlin.views.ComposeProps
import expo.modules.kotlin.views.ExpoComposeView

data class ExpoMarkerViewProps(
    val marker: MutableState<MarkerRecord> = mutableStateOf(MarkerRecord()),
) : ComposeProps

/** A class for the marker view in Android using Jetpack Compose. */
@SuppressLint("ViewConstructor")
class ExpoComposeMarkerView(context: Context, appContext: AppContext) :
    ExpoComposeView<ExpoMarkerViewProps>(context, appContext, withHostingView = true) {
  override val props = ExpoMarkerViewProps()

  @Composable override fun Content(modifier: Modifier) {}
}
