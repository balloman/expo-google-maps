//
//  MarkerView.swift
//  ExpoGoogleMaps
//
//  Created by Bernard Allotey on 10/13/23.
//

import Foundation
import ExpoModulesCore
import GoogleMaps

class MarkerView: ExpoView {
  var markerInfo: Marker? {
    didSet {
      updateMarker()
    }
  }
  private var gmsMarker: GMSMarker
  
  required init (appContext: AppContext? = nil) {
    gmsMarker = GMSMarker()
    super.init(appContext: appContext)
    clipsToBounds = true
  }
  
  override func layoutSubviews() {
    
  }
  
  func setMap(withMap: GMSMapView?) {
    gmsMarker.map = withMap
  }
  
  func updateMarker() {
    guard let uMarkerInfo = markerInfo else { return }
    gmsMarker.title = uMarkerInfo.title
    gmsMarker.userData = uMarkerInfo.key
    gmsMarker.iconView = subviews.first
    gmsMarker.position = uMarkerInfo.position.toCoordinate2D()
  }
  
  deinit {
    gmsMarker.map = nil
  }
}
