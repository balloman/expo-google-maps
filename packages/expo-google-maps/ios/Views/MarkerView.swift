//
//  MarkerView.swift
//  ExpoGoogleMaps
//
//  Created by Bernard Allotey on 10/13/23.
//

import ExpoModulesCore
import Foundation
import GoogleMaps

class MarkerView: ExpoView {
  var markerInfo: Marker? {
    didSet {
      updateMarker()
    }
  }
  var gmsMarker: GMSMarker
  let onMarkerPress = EventDispatcher()

  required init(appContext: AppContext? = nil) {
    gmsMarker = GMSMarker()
    gmsMarker.tracksViewChanges = false
    super.init(appContext: appContext)
  }

  /// Sets the marker's icon view to the provided subview.
  /// - Parameters:
  ///   - subview: The view that was added and should be used as the marker's icon.
  override func didAddSubview(_ subview: UIView) {
    gmsMarker.iconView = subview
  }

  /// Detaches a child view that represents the marker's icon (if present) before unmounting, ensuring the icon is removed from the map hierarchy when necessary.
  /// - Parameters:
  ///   - childComponentView: The child view being unmounted; if its `nativeID` matches the marker's `iconView`, the marker's `iconView` is cleared and the view is removed from its superview.
  ///   - index: The index of the child within its parent's subviews.
  override func unmountChildComponentView(_ childComponentView: UIView, index: Int) {
    // With the new architecture, we need to make sure we remove the view from the heirarchy before the Marker is removed
    // This is because the Marker's icon is held by the map and not the marker, so if we try to remove the marker, Swift will complain
    // that the icon can't be removed since it's owned by a different view if that makes sense.
    if childComponentView.nativeID == gmsMarker.iconView?.nativeID {
      gmsMarker.iconView = nil
      // This is an extra step in case we still want the marker but not the view, so this explicitly deattaches the subview from the map
      childComponentView.removeFromSuperview()
      return
    }
    super.unmountChildComponentView(childComponentView, index: index)
  }

  /// Attaches the underlying marker to a Google map or removes it from any map.
  /// - Parameters:
  ///   - withMap: The `GMSMapView` to place the marker on; pass `nil` to remove the marker from its current map.
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
