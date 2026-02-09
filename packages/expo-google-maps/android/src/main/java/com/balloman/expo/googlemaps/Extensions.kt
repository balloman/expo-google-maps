package com.balloman.expo.googlemaps

import expo.modules.kotlin.views.ExpoComposeView

fun ExpoComposeView<*>.jsLog(message: String?, level: LogLevel = LogLevel.INFO) {
  this.appContext.jsLogger?.let {
    val logFunction: (String) -> Any =
        when (level) {
          LogLevel.INFO -> it::info
          LogLevel.WARN -> it::warn
          LogLevel.ERROR -> it::error
        }
    logFunction(message ?: "")
  }
}

enum class LogLevel {
  INFO,
  WARN,
  ERROR,
}
