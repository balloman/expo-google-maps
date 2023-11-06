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
  var gmsMarker: GMSMarker
  var iconView: UIView = UIView()
  
  required init (appContext: AppContext? = nil) {
    gmsMarker = GMSMarker()
    super.init(appContext: appContext)
  }

  override func didAddSubview(_ subview: UIView) {
    gmsMarker.iconView = subview
  }

  override func removeReactSubview(_ subview: UIView!) {
    if (subview.nativeID == gmsMarker.iconView?.nativeID) {
      gmsMarker.iconView = nil
    }
    super.removeReactSubview(subview)
  }

  func setMap(withMap: GMSMapView?) {
    gmsMarker.map = withMap
  }

  func updateMarker() {
    guard let uMarkerInfo = markerInfo else { return }
    gmsMarker.title = uMarkerInfo.title
    gmsMarker.userData = uMarkerInfo.key
    gmsMarker.position = uMarkerInfo.position.toCoordinate2D()
  }
}
