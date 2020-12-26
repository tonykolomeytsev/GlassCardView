package kekmech.glasscardview.buffer

import android.util.Log
import android.view.View
import android.view.ViewGroup
import kekmech.glasscardview.GlassCardView
import kotlin.collections.HashMap

internal object GlobalFrameBuffer {

    private const val TAG = "GlobalBitmapHolder"
    private val registry: HashMap<View, SingleViewFrameBuffer> = HashMap()
    private val counter: HashMap<View, Int> = HashMap()

    fun registerView(frameBufferView: View): SingleViewFrameBuffer {
        if (!counter.containsKey(frameBufferView)) {
            counter[frameBufferView] = 0
        }
        counter[frameBufferView] = counter[frameBufferView]!! + 1
        Log.d(TAG, "registerView: increased counter for SingleParentBitmapHolder ($frameBufferView)")
        return registry.getOrPut(frameBufferView) {
            SingleViewFrameBuffer(frameBufferView = frameBufferView)
        }
    }

    fun deregisterView(frameBufferView: View) {
        counter[frameBufferView] = counter[frameBufferView]!! - 1
        Log.d(TAG, "deregisterView: decreased counter for SingleParentBitmapHolder ($frameBufferView)")
        if (counter[frameBufferView] == 0) {
            registry[frameBufferView]!!.destroy()
            registry.remove(frameBufferView)
            counter.remove(frameBufferView)
            Log.d(TAG, "deregisterView: SingleParentBitmapHolder was removed")
        }
    }
}
