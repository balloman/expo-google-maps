import ExpoModulesCore
import GoogleMaps

public class ExpoGoogleMapsMarkerModule: Module {
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
  // See https://docs.expo.dev/modules/module-api for more details about available components.
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
        view.mapView?.camera = GMSCameraPosition(latitude: camera.center.latitude, longitude: camera.center.longitude,
                                                 zoom: camera.zoom, bearing: camera.bearing ?? 0, 
                                                 viewingAngle: camera.viewingAngle ?? 0)
      }

      Prop("polygons") { (view, polygons: [Polygon]) in
        view.propPolygons = polygons
      }

      Prop("styleJson") { (view, json: String?) in
        guard (json != nil) else {
          return
        }
        do {
          view.mapView?.mapStyle = try GMSMapStyle(jsonString: json!)
        } catch {
          view.log("Native Error: One or more of the map styles failed to load. \(error)")
        }
      }
      
      Prop("showUserLocation") { (view, showUserLocation: Bool ) in
        print("showUserLocation", showUserLocation)
        if (showUserLocation) {
          view.mapView?.isMyLocationEnabled = true
        } else {
          view.mapView?.isMyLocationEnabled = false
        }
      }
      
      Events("onMapIdle")
      Events("onDidChange")

      //Animates camera to given location
      AsyncFunction("animateCamera") { (view: MapView, camera: Camera, animationOptions: AnimateOptions?) in
        view.animateCamera(to: camera.toGmsCameraPos(), animationOptions: animationOptions ?? AnimateOptions())
      }
      
      AsyncFunction("fitToBounds") { (view: MapView, params: FitToBoundsParams, animationOptions: AnimateOptions?) in
        view.fitToBounds(topRight: params.topRight.toCoordinate2D(), bottomLeft: params.bottomLeft.toCoordinate2D(),
                         insets: params.insets?.toUiEdgeInsets() ?? UIEdgeInsets(),
                         animationOptions: animationOptions ?? AnimateOptions())
      }
    }
  }
}
