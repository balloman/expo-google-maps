//
//  AppContext+Extensions.swift
//  ExpoGoogleMaps
//
//  Created by Bernard Allotey on 11/1/23.
//

import ExpoModulesCore
import Foundation

extension AppContext {
  /// Emits a "log" event containing the provided values to the app's event emitter.
  /// Does nothing if no event emitter is configured.
  /// - Parameters:
  ///   - items: Values to include as the event body; forwarded as an array in the emitted event.
  func log(_ items: Any...) {
    self.eventEmitter?.sendEvent(withName: "log", body: items)
  }
}
