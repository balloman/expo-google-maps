package expo.modules.googlemaps

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import expo.modules.kotlin.records.Field
import expo.modules.kotlin.records.Record

class Marker : Record {
    @Field
    val coordinate: Coordinate = Coordinate()

    @Field
    val title: String = ""

    @Field
    val key: String = ""
}

class PolygonRecord : Record {
    @Field
    val key = ""

    @Field
    val fillColor: String = ""

    @Field
    val strokeColor: String = ""

    @Field
    val coordinates: Array<Coordinate> = arrayOf()
}

data class Coordinate(@Field val latitude: Double = 0.0, @Field val longitude: Double = 0.0) :
    Record {
    fun toLatLng(): LatLng {
        return LatLng(latitude, longitude)
    }
}

data class Camera(
    @Field val center: Coordinate, @Field val zoom: Double,
    @Field val bearing: Double, @Field val viewingAngle: Double
) : Record {
    fun toGmsCameraPosition(): CameraPosition {
        return CameraPosition.Builder()
            .target(center.toLatLng())
            .zoom(zoom.toFloat())
            .bearing(bearing.toFloat())
            .tilt(viewingAngle.toFloat())
            .build()
    }

    companion object {
        fun fromGmsCameraPosition(cameraPosition: CameraPosition): Camera {
            return Camera(
                Coordinate(cameraPosition.target.latitude, cameraPosition.target.longitude),
                cameraPosition.zoom.toDouble(),
                cameraPosition.bearing.toDouble(),
                cameraPosition.tilt.toDouble()
            )
        }
    }
}

class Insets : Record {
    @Field
    val top: Double = 0.0

    @Field
    val left: Double = 0.0

    @Field
    val bottom: Double = 0.0

    @Field
    val right: Double = 0.0
}

class AnimateOptions : Record {
    @Field
    val animationDuration: Double = 1.0
}