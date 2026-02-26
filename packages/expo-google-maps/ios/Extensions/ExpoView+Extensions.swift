//
//  ExpoView+Extensions.swift
//  ExpoGoogleMaps
//
//  Created by Bernard Allotey on 10/14/23.
//

import ExpoModulesCore
import Foundation

extension ExpoView {
  /// Emits a "log" event to the module's event emitter containing the provided items.
  /// If `appContext` or its `eventEmitter` is unavailable, no event is sent.
  /// - Parameters:
  ///   - item: One or more values to include as the event body (packaged as an array).
  func log(_ item: Any...) {
    self.appContext?.eventEmitter?.sendEvent(withName: "log", body: item)
  }
}
