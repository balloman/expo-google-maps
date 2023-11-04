import ExpoModulesCore
import GoogleMaps

// This view will be used as a native component. Make sure to inherit from `ExpoView`
// to apply the proper styling (e.g. border radius and shadows).
class MapView: ExpoView, GMSMapViewDelegate {
  let mapView: GMSMapView?
  let onMapIdle = EventDispatcher()
  private var markers: [MarkerView] = []
  private var polygons: [String: GMSPolygon] = [:]
  var propPolygons: [Polygon] = []
  
  required init (appContext: AppContext? = nil) {
    print("Initializing Map View")
    let camera = GMSCameraPosition.camera(withLatitude: 37.42, longitude: -122.20, zoom: 14)
    if (!ExpoGoogleMapsModule.keySet) {
      appContext?.log("API Key not set, but can be set with setApiKey(). Attempting to display the map would crash the app")
      mapView = nil
    } else {
      mapView = GMSMapView.map(withFrame: CGRect.zero, camera: camera)
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
    let markerViews = subviews.compactMap { ($0 as? MarkerView) }
    markerViews.forEach {
      $0.setMap(withMap: mapView)
    }
    setPolygons(polygonRecords: propPolygons)
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
  
  func setPolygons(polygonRecords: [Polygon]) {
    polygonRecords.forEach { polygonRecord in
      guard let polygon = polygons[polygonRecord.key] else {
        let path = polygonRecord.coordinates.reduce(into: GMSMutablePath(), { path, coordinate in
          path.add(coordinate.toCoordinate2D())
        })
        let newPolygon = GMSPolygon(path: path)
        newPolygon.fillColor = polygonRecord.fillColor
        newPolygon.strokeColor = polygonRecord.strokeColor
        newPolygon.map = mapView
        polygons[polygonRecord.key] = newPolygon
        return
      }
      polygon.fillColor = polygonRecord.fillColor
      polygon.strokeColor = polygonRecord.strokeColor
      let path = polygonRecord.coordinates.reduce(into: GMSMutablePath(), { path, coordinate in
        path.add(coordinate.toCoordinate2D())
      })
      polygon.path = path
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
    print("Map Idle", cameraPosition, cameraPosition.toCameraRecord())
    onMapIdle([
      "cameraPosition": cameraPosition.toCameraRecord().toDictionary()
    ])
  }
}
