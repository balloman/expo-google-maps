//
//  ExpoGoogleMapsTypes.swift
//  ExpoGoogleMaps
//
//  Created by Bernard Allotey on 10/13/23.
//

import ExpoModulesCore
import Foundation
import GoogleMaps

struct Marker: Record {
  @Field
  var position: Coordinate

  @Field
  var title: String?

  @Field
  var key: String

}

struct Polygon: Record {
  @Field
  var key: String

  @Field
  var fillColor: UIColor = UIColor(.clear)

  @Field
  var coordinates: [Coordinate]

  @Field
  var strokeColor: UIColor = UIColor(.clear)
}

struct Coordinate: Record {
  @Field
  var latitude: Double = 0

  @Field
  var longitude: Double = 0

  func toCoordinate2D() -> CLLocationCoordinate2D {
    return CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
  }
}

struct Camera: Record {
  @Field
  var center: Coordinate

  @Field
  var zoom: Float32 = 0

  @Field
  var bearing: Double? = 0

  @Field
  var viewingAngle: Double? = 0

  func toGmsCameraPos() -> GMSCameraPosition {
    return GMSCameraPosition(
      target: center.toCoordinate2D(), zoom: zoom, bearing: bearing ?? 0,
      viewingAngle: viewingAngle ?? 0)
  }
}

struct Insets: Record {
  @Field
  var top: Double? = 0

  @Field
  var bottom: Double? = 0

  @Field
  var left: Double? = 0

  @Field
  var right: Double? = 0

  func toUiEdgeInsets() -> UIEdgeInsets {
    return UIEdgeInsets(top: top ?? 0, left: left ?? 0, bottom: bottom ?? 0, right: right ?? 0)
  }
}

struct FitToBoundsParams: Record {
  @Field
  var topRight: Coordinate

  @Field
  var bottomLeft: Coordinate

  @Field
  var insets: Insets?
}

struct AnimateOptions: Record {
  @Field
  var animationDuration: Double = 1

  @Field
  var animationFunction: String? = "default"
}
