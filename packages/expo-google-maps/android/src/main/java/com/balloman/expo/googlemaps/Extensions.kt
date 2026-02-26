package com.balloman.expo.googlemaps

import expo.modules.kotlin.views.ExpoComposeView

/**
 * Logs a message to the view's JavaScript logger when available.
 *
 * @param message The message to log; if `null`, an empty string is logged.
 * @param level The log level to use (`INFO`, `WARN`, or `ERROR`). Default is `INFO`.
 */
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
