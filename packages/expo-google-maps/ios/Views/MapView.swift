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

  required init(appContext: AppContext? = nil) {
    mapOptions = GMSMapViewOptions()
    mapOptions.camera = GMSCameraPosition(latitude: 37.42, longitude: -122.20, zoom: 14)
    if !ExpoGoogleMapsModule.keySet {
      appContext?.log(
        "API Key not set, but can be set with setApiKey(). Attempting to display the map would crash the app"
      )
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

  /// Updates the view layout by resizing the embedded map to fill the view's bounds and applying the current polygon properties.
  /// 
  /// This ensures `mapView` matches `bounds` and refreshes rendered polygons from `propPolygons`.
  override func layoutSubviews() {
    mapView?.frame = bounds
    setPolygons(polygonRecords: propPolygons)
  }

  /// Performs pre-insertion handling for a subview (for example, attaching marker views to the map) and inserts the view into the receiver's view hierarchy.
  /// - Parameters:
  ///   - view: The subview to insert.
  ///   - index: The index in the subviews array at which to insert the view.
  override func insertSubview(_ view: UIView, at index: Int) {
    handleSubviewInsertion(subview: view)
    super.insertSubview(view, at: index)
  }

  /// Notifies the view that a subview is about to be removed and performs cleanup for MarkerView subviews.
  /// - Parameter subview: The subview that will be removed.
  override func willRemoveSubview(_ subview: UIView) {
    handleSubviewRemoval(subview: subview)
    super.willRemoveSubview(subview)
  }

  /// Mounts a child component view into this view and processes it for map-specific insertion.
  /// - Parameters:
  ///   - childComponentView: The child UIView being mounted.
  ///   - index: The position at which the child view is mounted.

  override func mountChildComponentView(_ childComponentView: UIView, index: Int) {
    handleSubviewInsertion(subview: childComponentView)
    super.mountChildComponentView(childComponentView, index: index)
  }

  /// Handles removal of a child component view from the map and forwards the unmount to the superclass.
  /// - Parameters:
  ///   - childComponentView: The child view being unmounted; if it represents a marker, it will be detached from the map and internal tracking updated.
  ///   - index: The index at which the child was mounted.
  override func unmountChildComponentView(_ childComponentView: UIView, index: Int) {
    handleSubviewRemoval(subview: childComponentView)
    super.unmountChildComponentView(childComponentView, index: index)
  }

  /// Attaches a MarkerView subview to the map and registers it in the view's marker dictionary.
  /// - Parameter subview: The subview to inspect; if it is a `MarkerView` whose `gmsMarker.userData` is a `String`, the marker is attached to `mapView` and stored in `markers` under that key.

  private func handleSubviewInsertion(subview: UIView) {
    guard let markerView = subview as? MarkerView,
      let key = markerView.gmsMarker.userData as? String
    else {
      return
    }
    markerView.setMap(withMap: mapView)
    markers[key] = markerView
  }

  /// Detaches a subview's marker from the map and removes its entry from the local markers dictionary when the subview is a tracked MarkerView.
  /// - Parameter subview: The subview to inspect; if it is a `MarkerView` whose `gmsMarker.userData` is a `String` key, that marker's `map` is set to `nil` and the key is removed from `markers`.
  private func handleSubviewRemoval(subview: UIView) {
    guard let markerView = subview as? MarkerView,
      let key = markerView.gmsMarker.userData as? String
    else {
      return
    }
    markerView.gmsMarker.map = nil
    markers.removeValue(forKey: key)
  }

  /// Animates the map camera to the specified camera position using the provided animation options.
  /// - Parameters:
  ///   - to: Destination camera position.
  ///   - animationOptions: Animation configuration; `animationDuration` controls the animation length and `animationFunction` (a CAMediaTimingFunction name) controls the timing curve — uses `"default"` if `animationFunction` is `nil`.
  func animateCamera(to: GMSCameraPosition, animationOptions: AnimateOptions) {
    CATransaction.begin()
    CATransaction.setAnimationDuration(animationOptions.animationDuration)
    CATransaction.setAnimationTimingFunction(
      CAMediaTimingFunction(
        name: CAMediaTimingFunctionName(rawValue: animationOptions.animationFunction ?? "default")
      ))
    mapView?.animate(to: to)
    CATransaction.commit()
  }

  /// Moves the map camera to fit the rectangular bounds defined by two corner coordinates, applying edge insets and animation options.
  /// - Parameters:
  ///   - topRight: The northeast corner of the bounds to fit.
  ///   - bottomLeft: The southwest corner of the bounds to fit.
  ///   - insets: Edge insets to apply when fitting the bounds into the view.
  ///   - animationOptions: Animation parameters (duration and timing) used when animating the camera.
  func fitToBounds(
    topRight: CLLocationCoordinate2D, bottomLeft: CLLocationCoordinate2D, insets: UIEdgeInsets,
    animationOptions: AnimateOptions
  ) {
    let bounds = GMSCoordinateBounds(coordinate: topRight, coordinate: bottomLeft)
    guard let newCamera = mapView?.camera(for: bounds, insets: insets) else {
      return
    }
    animateCamera(to: newCamera, animationOptions: animationOptions)
  }

  /// Builds a GMSMutablePath from an array of Coordinate values.
  /// - Parameters:
  ///   - coordinates: The coordinates to convert into the path; values are added in array order.
  /// - Returns: A `GMSMutablePath` containing the converted coordinates in the same order.
  private func createPath(from coordinates: [Coordinate]) -> GMSMutablePath {
    coordinates.reduce(into: GMSMutablePath()) { path, coordinate in
      path.add(coordinate.toCoordinate2D())
    }
  }

  /// Synchronizes the map's polygons with the provided polygon records.
  /// 
  /// Creates new GMSPolygon objects or updates existing ones to match each record's coordinates and styling, and removes any polygons currently displayed on the map that are not present in `polygonRecords`.
  /// - Parameters:
  ///   - polygonRecords: An array of Polygon records where each record contains a unique `key`, an ordered list of `coordinates`, and styling properties (`fillColor`, `strokeColor`).
  func setPolygons(polygonRecords: [Polygon]) {
    for polygonRecord in polygonRecords {
      guard let polygon = polygons[polygonRecord.key] else {
        let newPolygon = GMSPolygon(path: createPath(from: polygonRecord.coordinates))
        newPolygon.fillColor = polygonRecord.fillColor
        newPolygon.strokeColor = polygonRecord.strokeColor
        newPolygon.map = mapView
        polygons[polygonRecord.key] = newPolygon
        continue
      }
      polygon.fillColor = polygonRecord.fillColor
      polygon.strokeColor = polygonRecord.strokeColor
      polygon.path = createPath(from: polygonRecord.coordinates)
    }

    let inputKeys = Set(polygonRecords.map { $0.key })
    for key in polygons.keys {
      if !inputKeys.contains(key) {
        let gmsPolygon = polygons[key]
        gmsPolygon?.map = nil
        gmsPolygon?.layer.removeFromSuperlayer()
        polygons.removeValue(forKey: key)
      }
    }
  }

  /// Called when the map's camera becomes idle and emits an `onMapIdle` event containing the current camera position.
  /// - Parameters:
  ///   - mapView: The map view whose camera became idle.
  ///   - cameraPosition: The current camera position; serialized and included in the event payload.

  func mapView(_ mapView: GMSMapView, idleAt cameraPosition: GMSCameraPosition) {
    onMapIdle([
      "cameraPosition": cameraPosition.toCameraRecord().toDictionary()
    ])
  }

  /// Handles camera position changes by emitting an `onDidChange` event containing the new camera position.
  /// - Parameters:
  ///   - mapView: The `GMSMapView` whose camera changed.
  ///   - position: The new `GMSCameraPosition` for the map.
  func mapView(_ mapView: GMSMapView, didChange position: GMSCameraPosition) {
    onDidChange([
      "cameraPosition": position.toCameraRecord().toDictionary()
    ])
  }

  /// Handles taps on map markers by dispatching the associated MarkerView's press event if the marker is tracked.
  /// - Parameters:
  ///   - mapView: The map view containing the tapped marker.
  ///   - marker: The tapped `GMSMarker`; expected to hold a string key in `userData` identifying a tracked `MarkerView`.
  /// - Returns: `true` if a tracked `MarkerView` was found and its `onMarkerPress` was invoked, `false` otherwise.
  func mapView(_ mapView: GMSMapView, didTap marker: GMSMarker) -> Bool {
    guard let markerKey = marker.userData as? String,
      let markerView = markers[markerKey]
    else {
      return false
    }
    markerView.onMarkerPress([:])
    return true
  }
}
