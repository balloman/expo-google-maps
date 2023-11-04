//
//  ExpoView+Extensions.swift
//  ExpoGoogleMaps
//
//  Created by Bernard Allotey on 10/14/23.
//

import Foundation
import ExpoModulesCore

extension ExpoView {
  func log(_ item: Any...) {
    self.appContext?.eventEmitter?.sendEvent(withName: "log", body: item)
  }
}
