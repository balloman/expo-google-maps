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
  /**
   * Convert this Coordinate to a Google Maps `LatLng`.
   *
   * @return A `LatLng` constructed from this coordinate's latitude and longitude.
   */
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
  /**
   * Creates a Google Maps CameraPosition that reflects this Camera's center, zoom, bearing, and viewing angle.
   *
   * @return A CameraPosition matching this Camera's center, zoom, bearing, and viewing angle.
   */
  fun toGmsCameraPosition(): CameraPosition {
    return CameraPosition.Builder()
        .target(center.toLatLng())
        .zoom(zoom.toFloat())
        .bearing(bearing.toFloat())
        .tilt(viewingAngle.toFloat())
        .build()
  }

  companion object {
    /**
     * Create a Camera from a Google Maps CameraPosition.
     *
     * @param cameraPosition The CameraPosition to convert.
     * @return A Camera with the same center coordinate, zoom, bearing, and viewing angle as the provided CameraPosition.
     */
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

  /**
 * Constructs a LatLngBounds spanning the rectangle defined by these insets.
 *
 * The southwest corner is created from (left, bottom) and the northeast corner from (right, top).
 *
 * @return A LatLngBounds whose southwest corner is (left, bottom) and northeast corner is (right, top).
 */
fun toBounds(): LatLngBounds = LatLngBounds(LatLng(left, bottom), LatLng(right, top))
}

class FitToBoundsParams(
    @Field val topRight: Coordinate,
    @Field val bottomLeft: Coordinate,
    @Field val insets: Insets?,
) : Record {

  data class CameraBounds(val bounds: LatLngBounds, val padding: Int)

  /**
   * Builds a LatLngBounds that includes the record's topRight and bottomLeft coordinates and computes
   * an integer padding value to use when fitting the camera to those bounds.
   *
   * The padding is the maximum of the insets' top, bottom, left, and right components rounded to
   * the nearest integer; if `insets` is null the padding is 0.
   *
   * @return A CameraBounds containing the computed LatLngBounds and the padding in pixels.
   */
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
