package com.balloman.expo.googlemaps.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.core.view.isVisible
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.balloman.expo.googlemaps.MarkerRecord
import expo.modules.kotlin.AppContext
import expo.modules.kotlin.viewevent.EventDispatcher
import expo.modules.kotlin.views.ExpoView

@SuppressLint("ViewConstructor")
class ExpoMarkerView(context: Context, appContext: AppContext) : ExpoView(context, appContext) {
    private var markerRecord: MarkerRecord? = null
    var gmsMarker: Marker? = null
    private var bitmap: Bitmap? = null
    private var child: View? = null
    val onMarkerPress by EventDispatcher()

    fun updateMarker(marker: MarkerRecord) {
        markerRecord = marker
        gmsMarker?.position = marker.position.toLatLng()
        gmsMarker?.title = marker.title
    }

    fun toMarkerOptions(): MarkerOptions {
        var markerOptions = MarkerOptions()
        if (markerRecord == null) {
            return markerOptions
        }
        markerOptions = MarkerOptions()
            .position(markerRecord!!.position.toLatLng())
            .title(markerRecord!!.title)
        if (bitmap != null) {
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap!!))
        }
        return markerOptions
    }


    override fun onViewAdded(child: View?) {
        this.child = child
        child?.addOnLayoutChangeListener { v, left, top, right, bottom, _, _, _, _ ->
            bitmap = loadBitmapFromView(v, left, top, right, bottom)
            gmsMarker?.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap!!))
        }
        child?.isVisible = false
        super.onViewAdded(child)
    }

    override fun onViewRemoved(child: View?) {
        this.child = null
        bitmap = null
        gmsMarker?.setIcon(null)
        super.onViewRemoved(child)
    }

    private fun loadBitmapFromView(
        view: View,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ): Bitmap {
        val width = if (right - left <= 0) 100 else right - left
        val height = if (bottom - top <= 0) 100 else bottom - top
        val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        view.draw(c)
        return b
    }
}