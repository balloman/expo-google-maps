package com.balloman.expo.googlemaps

import android.app.Activity
import expo.modules.core.interfaces.ReactActivityLifecycleListener
import com.balloman.expo.googlemaps.views.ExpoMapView

class ExpoGoogleMapsReactActivityLifecycleListener : ReactActivityLifecycleListener {
    override fun onPause(activity: Activity?) {
        super.onPause(activity)
        ExpoMapView.mapView?.onPause()
    }

    override fun onResume(activity: Activity?) {
        super.onResume(activity)
        ExpoMapView.mapView?.onResume()
    }

    override fun onDestroy(activity: Activity?) {
        super.onDestroy(activity)
        ExpoMapView.mapView?.onStop()
        ExpoMapView.mapView?.onDestroy()
    }
}