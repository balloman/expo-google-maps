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
  let onMarkerPress = EventDispatcher()
  
  required init (appContext: AppContext? = nil) {
    gmsMarker = GMSMarker()
    gmsMarker.tracksViewChanges = false
    super.init(appContext: appContext)
  }

  override func didAddSubview(_ subview: UIView) {
    gmsMarker.iconView = subview
  }

  override func removeReactSubview(_ subview: UIView!) {
		print("Removing react subview")
		if (subview.nativeID == gmsMarker.iconView?.nativeID) {
			gmsMarker.iconView = nil
		}
    super.removeReactSubview(subview)
  }
	
	override func unmountChildComponentView(_ childComponentView: UIView, index: Int) {
		print("Unmounting child from markerview")
		/* With the new architecture, we need to make sure we remove the view from the heirarchy before the Marker is removed
		 This is because the Marker's icon is held by the map and not the marker, so if we try to remove the marker, Swift will complain
		 that the icon can't be removed since it's owned by a different view if that makes sense.
		*/
		if (childComponentView.nativeID == gmsMarker.iconView?.nativeID) {
			gmsMarker.iconView = nil
			// This is an extra step in case we still want the marker but not the view, so this explicitly deattaches the subview from the map
			childComponentView.removeFromSuperview()
			return
		}
		super.unmountChildComponentView(childComponentView, index: index)
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
