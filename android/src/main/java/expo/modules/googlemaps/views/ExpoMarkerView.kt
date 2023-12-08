package expo.modules.googlemaps.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import expo.modules.googlemaps.MarkerRecord
import expo.modules.kotlin.AppContext
import expo.modules.kotlin.views.ExpoView

@SuppressLint("ViewConstructor")
class ExpoMarkerView(context: Context, appContext: AppContext) : ExpoView(context, appContext) {
    var markerRecord: MarkerRecord? = null
        private set
    var gmsMarker: Marker? = null
    var gmsMap: GoogleMap? = null
    private var bitmap: Bitmap? = null
    private var child: View? = null

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
        removeView(child)
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