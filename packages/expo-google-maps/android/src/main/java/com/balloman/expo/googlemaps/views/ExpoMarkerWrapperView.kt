package com.balloman.expo.googlemaps.views

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import expo.modules.kotlin.AppContext
import expo.modules.kotlin.views.ComposeProps
import expo.modules.kotlin.views.ExpoComposeView

@SuppressLint("ViewConstructor")
class ExpoMarkerWrapperView(context: Context, appContext: AppContext) :
    ExpoComposeView<ComposeProps>(context, appContext, withHostingView = true) {

  @Composable override fun Content(modifier: Modifier) {}
}
