//
//  AppContext+Extensions.swift
//  ExpoGoogleMaps
//
//  Created by Bernard Allotey on 11/1/23.
//

import Foundation
import ExpoModulesCore

extension AppContext {
  func log(_ items: Any...) {
    self.eventEmitter?.sendEvent(withName: "log", body: items)
  }
}
