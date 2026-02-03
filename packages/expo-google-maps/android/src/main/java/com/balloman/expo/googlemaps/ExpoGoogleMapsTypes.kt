package com.balloman.expo.googlemaps

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import expo.modules.kotlin.records.Field
import expo.modules.kotlin.records.Record
import kotlin.math.roundToInt

class MarkerRecord : Record {
  @Field val position: Coordinate = Coordinate(0.0, 0.0)

  @Field val title: String = ""

  @Field val key: String = ""
}

class PolygonRecord : Record {
  @Field val key = ""

  @Field val fillColor: String = ""

  @Field val strokeColor: String = ""

  @Field val coordinates: Array<Coordinate> = arrayOf()

  /** Gets the points of the polygon as a list of LatLngs */
  val points
    get() = coordinates.map { it.toLatLng() }.toList()
}

data class Coordinate(@Field val latitude: Double, @Field val longitude: Double) : Record {
  fun toLatLng(): LatLng {
    return LatLng(latitude, longitude)
  }
}

data class Camera(
    @Field val center: Coordinate,
    @Field val zoom: Double,
    @Field val bearing: Double,
    @Field val viewingAngle: Double,
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
          cameraPosition.tilt.toDouble(),
      )
    }
  }
}

data class OnDidChangeEvent(@Field val cameraPosition: Camera) : Record

class Insets : Record {
  @Field val top: Double = 0.0

  @Field val left: Double = 0.0

  @Field val bottom: Double = 0.0

  @Field val right: Double = 0.0

  fun toBounds(): LatLngBounds = LatLngBounds(LatLng(left, bottom), LatLng(right, top))
}

class FitToBoundsParams(
    @Field val topRight: Coordinate,
    @Field val bottomLeft: Coordinate,
    @Field val insets: Insets?,
) : Record {

  data class CameraBounds(val bounds: LatLngBounds, val padding: Int)

  fun toLatLngBounds(): CameraBounds {
    val bounds =
        LatLngBounds.builder().include(topRight.toLatLng()).include(bottomLeft.toLatLng()).build()
    val padding =
        if (insets != null) {
          arrayOf(
                  insets.top.roundToInt(),
                  insets.bottom.roundToInt(),
                  insets.left.roundToInt(),
                  insets.right.roundToInt(),
              )
              .max()
        } else 0
    return FitToBoundsParams.CameraBounds(bounds, padding)
  }
}

class AnimateOptions : Record {
  @Field val animationDuration: Double = 1.0
}
