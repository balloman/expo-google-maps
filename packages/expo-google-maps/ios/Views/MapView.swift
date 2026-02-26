import ExpoModulesCore
import GoogleMaps

// This view will be used as a native component. Make sure to inherit from `ExpoView`
// to apply the proper styling (e.g. border radius and shadows).
class MapView: ExpoView, GMSMapViewDelegate {
  let mapView: GMSMapView?
  let onMapIdle = EventDispatcher()
  let onDidChange = EventDispatcher()
	let mapOptions: GMSMapViewOptions
  private var markers: [String: MarkerView] = [:]
  private var polygons: [String: GMSPolygon] = [:]
  var propPolygons: [Polygon] = []
  
  required init (appContext: AppContext? = nil) {
    mapOptions = GMSMapViewOptions()
    mapOptions.camera = GMSCameraPosition(latitude: 37.42, longitude: -122.20, zoom: 14)
    if (!ExpoGoogleMapsModule.keySet) {
      appContext?.log("API Key not set, but can be set with setApiKey(). Attempting to display the map would crash the app")
      mapView = nil
    } else {
      mapView = GMSMapView(options: mapOptions)
    }
    super.init(appContext: appContext)
    clipsToBounds = true
    if mapView != nil {
      addSubview(mapView!)
      mapView?.delegate = self
    }
  }
  
  override func layoutSubviews() {
    mapView?.frame = bounds
    setPolygons(polygonRecords: propPolygons)
  }
  
  override func insertSubview(_ view: UIView, at index: Int) {
    handleSubviewInsertion(subview: view)
    super.insertSubview(view, at: index)
  }

  override func willRemoveSubview(_ subview: UIView) {
    handleSubviewRemoval(subview: subview)
    super.willRemoveSubview(subview)
  }

  // MARK: - New Architecture Support

  override func mountChildComponentView(_ childComponentView: UIView, index: Int) {
    handleSubviewInsertion(subview: childComponentView)
    super.mountChildComponentView(childComponentView, index: index)
  }

  override func unmountChildComponentView(_ childComponentView: UIView, index: Int) {
    handleSubviewRemoval(subview: childComponentView)
    super.unmountChildComponentView(childComponentView, index: index)
  }

  // MARK: - Helper Methods

  private func handleSubviewInsertion(subview: UIView) {
		guard let markerView = subview as? MarkerView,
				  let key = markerView.gmsMarker.userData as? String else {
			return
		}
		markerView.setMap(withMap: mapView)
		markers[key] = markerView
	}

	private func handleSubviewRemoval(subview: UIView) {
		guard let markerView = subview as? MarkerView,
				  let key = markerView.gmsMarker.userData as? String else {
			return
		}
		markerView.gmsMarker.map = nil
		markers.removeValue(forKey: key)
	}

  func animateCamera(to: GMSCameraPosition, animationOptions: AnimateOptions) {
    CATransaction.begin()
    CATransaction.setAnimationDuration(animationOptions.animationDuration)
    CATransaction.setAnimationTimingFunction(CAMediaTimingFunction(
      name: CAMediaTimingFunctionName(rawValue: animationOptions.animationFunction ?? "default")
    ))
    mapView?.animate(to: to)
    CATransaction.commit()
  }
  
  func fitToBounds(topRight: CLLocationCoordinate2D, bottomLeft: CLLocationCoordinate2D, insets: UIEdgeInsets,
                   animationOptions: AnimateOptions) {
    let bounds = GMSCoordinateBounds(coordinate: topRight, coordinate: bottomLeft)
    guard let newCamera = mapView?.camera(for: bounds, insets: insets) else {
      return;
    }
    animateCamera(to: newCamera, animationOptions: animationOptions)
  }
  
  private func createPath(from coordinates: [Coordinate]) -> GMSMutablePath {
    coordinates.reduce(into: GMSMutablePath()) { path, coordinate in
      path.add(coordinate.toCoordinate2D())
    }
  }

  func setPolygons(polygonRecords: [Polygon]) {
    polygonRecords.forEach { polygonRecord in
      guard let polygon = polygons[polygonRecord.key] else {
        let newPolygon = GMSPolygon(path: createPath(from: polygonRecord.coordinates))
        newPolygon.fillColor = polygonRecord.fillColor
        newPolygon.strokeColor = polygonRecord.strokeColor
        newPolygon.map = mapView
        polygons[polygonRecord.key] = newPolygon
        return
      }
      polygon.fillColor = polygonRecord.fillColor
      polygon.strokeColor = polygonRecord.strokeColor
      polygon.path = createPath(from: polygonRecord.coordinates)
    }

    let inputKeys = Set(polygonRecords.map { $0.key })
    for key in polygons.keys {
      if (!inputKeys.contains(key)) {
        let gmsPolygon = polygons[key]
        gmsPolygon?.map = nil
        gmsPolygon?.layer.removeFromSuperlayer()
        polygons.removeValue(forKey: key)
      }
    }
  }
  
  // MARK: MapViewDelegate
  
  func mapView(_ mapView: GMSMapView, idleAt cameraPosition: GMSCameraPosition) {
    onMapIdle([
      "cameraPosition": cameraPosition.toCameraRecord().toDictionary()
    ])
  }
  
  func mapView(_ mapView: GMSMapView, didChange position: GMSCameraPosition) {
    onDidChange([
      "cameraPosition": position.toCameraRecord().toDictionary()
    ])
  }
  
  func mapView(_ mapView: GMSMapView, didTap marker: GMSMarker) -> Bool {
    guard let markerKey = marker.userData as? String,
          let markerView = markers[markerKey] else {
      return false
    }
    markerView.onMarkerPress([:])
    return true
  }
}
