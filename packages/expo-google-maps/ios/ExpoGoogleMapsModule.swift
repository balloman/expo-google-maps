import ExpoModulesCore
import GoogleMaps

public class ExpoGoogleMapsMarkerModule: Module {
  /// Defines the ExpoGoogleMapsMarker native module and its MarkerView interface.
  /// - Returns: A ModuleDefinition that registers the "ExpoGoogleMapsMarker" module, exposes a MarkerView with `marker` and `tracksViewChanges` props, and emits the `onMarkerPress` event.
  public func definition() -> ModuleDefinition {
    Name("ExpoGoogleMapsMarker")

    View(MarkerView.self) {
      Prop("marker") { (view, marker: Marker) in
        view.markerInfo = marker
      }

      Prop("tracksViewChanges") { (view, tracks: Bool) in
        view.gmsMarker.tracksViewChanges = tracks
      }

      Events("onMarkerPress")
    }
  }
}

public class ExpoGoogleMapsModule: Module {
  static var keySet = false

  // Each module class must implement the definition function. The definition consists of components
  // that describes the module's functionality and behavior.
  /// Registers and configures the ExpoGoogleMaps native module and its associated view.
  /// 
  /// The module is named "ExpoGoogleMaps", exposes a "log" event, and provides a `setApiKey` function.
  /// Its view is `MapView` and exposes properties: `camera`, `polygons`, `styleJson`, `showUserLocation`, and `mapId`;
  /// events: `onMapIdle` and `onDidChange`; and async methods: `animateCamera` and `fitToBounds`.
  /// - Returns: A ModuleDefinition that registers the "ExpoGoogleMaps" module and configures its MapView, props, events, and functions.
  public func definition() -> ModuleDefinition {
    // Sets the name of the module that JavaScript code will use to refer to the module. Takes a string as an argument.
    // Can be inferred from module's class name, but it's recommended to set it explicitly for clarity.
    // The module will be accessible from `requireNativeModule('ExpoGoogleMaps')` in JavaScript.
    Name("ExpoGoogleMaps")

    Events("log")

    Function("setApiKey") { (value: String) in
      ExpoGoogleMapsModule.keySet = true
      GMSServices.provideAPIKey(value)
    }

    // Enables the module to be used as a native view. Definition components that are accepted as part of the
    // view definition: Prop, Events.
    View(MapView.self) {
      Prop("camera") { (view, camera: Camera) in
        view.mapView?.camera = GMSCameraPosition(
          latitude: camera.center.latitude, longitude: camera.center.longitude,
          zoom: camera.zoom, bearing: camera.bearing ?? 0,
          viewingAngle: camera.viewingAngle ?? 0)
      }

      Prop("polygons") { (view, polygons: [Polygon]) in
        view.propPolygons = polygons
      }

      Prop("styleJson") { (view, json: String?) in
        guard json != nil else {
          return
        }
        do {
          view.mapView?.mapStyle = try GMSMapStyle(jsonString: json!)
        } catch {
          view.log("Native Error: One or more of the map styles failed to load. \(error)")
        }
      }

      Prop("showUserLocation") { (view, showUserLocation: Bool) in
        view.mapView?.isMyLocationEnabled = showUserLocation
      }

      Prop("mapId") { (view, mapId: String?) in
        mapId.map { view.mapOptions.mapID = GMSMapID(identifier: $0) }
      }

      Events("onMapIdle")
      Events("onDidChange")

      //Animates camera to given location
      AsyncFunction("animateCamera") {
        (view: MapView, camera: Camera, animationOptions: AnimateOptions?) in
        view.animateCamera(
          to: camera.toGmsCameraPos(), animationOptions: animationOptions ?? AnimateOptions())
      }

      AsyncFunction("fitToBounds") {
        (view: MapView, params: FitToBoundsParams, animationOptions: AnimateOptions?) in
        view.fitToBounds(
          topRight: params.topRight.toCoordinate2D(),
          bottomLeft: params.bottomLeft.toCoordinate2D(),
          insets: params.insets?.toUiEdgeInsets() ?? UIEdgeInsets(),
          animationOptions: animationOptions ?? AnimateOptions())
      }
    }
  }
}
