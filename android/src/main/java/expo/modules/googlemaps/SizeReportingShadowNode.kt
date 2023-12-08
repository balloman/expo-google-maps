package expo.modules.googlemaps

import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.UIViewOperationQueue

class SizeReportingShadowNode : LayoutShadowNode() {

    override fun onCollectExtraUpdates(uiViewOperationQueue: UIViewOperationQueue?) {
        super.onCollectExtraUpdates(uiViewOperationQueue)

        val data = hashMapOf<String, Float>()
        data["width"] = layoutWidth
        data["height"] = layoutHeight
        uiViewOperationQueue?.enqueueUpdateExtraData(reactTag, data)
    }
}