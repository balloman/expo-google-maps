//
//  GoogleMaps+Extensions.swift
//  ExpoGoogleMaps
//
//  Created by Bernard Allotey on 11/4/23.
//

import Foundation
import GoogleMaps

extension GMSCameraPosition {
  func toCameraRecord() -> Camera {
    let camera = Camera()
    camera.center = self.target.toCoordinate()
    camera.bearing = self.bearing
    camera.viewingAngle = self.viewingAngle
    camera.zoom = self.zoom
    return camera
  }
}

extension CLLocationCoordinate2D {
  func toCoordinate() -> Coordinate {
    return Coordinate(latitude: self.latitude, longitude: self.longitude)
  }
}
