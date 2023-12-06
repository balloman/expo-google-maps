package expo.modules.googlemaps

import android.content.Context
import expo.modules.core.interfaces.Package
import expo.modules.core.interfaces.ReactActivityLifecycleListener

class ExpoGoogleMapsPackage : Package {
    override fun createReactActivityLifecycleListeners(activityContext: Context?): List<ReactActivityLifecycleListener> {
        return listOf(ExpoGoogleMapsReactActivityLifecycleListener())
    }
}