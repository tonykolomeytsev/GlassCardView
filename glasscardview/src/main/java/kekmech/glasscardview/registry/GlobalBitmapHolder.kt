package kekmech.glasscardview.registry

import android.util.Log
import android.view.ViewGroup
import kekmech.glasscardview.GlassCardView
import kotlin.collections.HashMap

internal object GlobalBitmapHolder {

    private const val TAG = "GlobalBitmapHolder"
    private val registry: HashMap<ViewGroup, SingleParentBitmapHolder> = HashMap()
    private val counter: HashMap<ViewGroup, Int> = HashMap()

    fun registerView(glassCardView: GlassCardView): SingleParentBitmapHolder {
        val parent = glassCardView.parent as ViewGroup
        if (!counter.containsKey(parent)) {
            counter[parent] = 0
        }
        counter[parent] = counter[parent]!! + 1
        Log.d(TAG, "registerView: increased counter for SingleParentBitmapHolder ($parent)")
        return registry.getOrPut(parent) {
            SingleParentBitmapHolder(
                parentView = parent,
                downscaleFactor = 8f
            )
        }
    }

    fun deregisterView(glassCardView: GlassCardView) {
        val parent = glassCardView.parent as ViewGroup
        counter[parent] = counter[parent]!! - 1
        Log.d(TAG, "deregisterView: decreased counter for SingleParentBitmapHolder ($parent)")
        if (counter[parent] == 0) {
            registry[parent]!!.destroy()
            registry.remove(parent)
            counter.remove(parent)
            Log.d(TAG, "deregisterView: SingleParentBitmapHolder was removed")
        }
    }
}
