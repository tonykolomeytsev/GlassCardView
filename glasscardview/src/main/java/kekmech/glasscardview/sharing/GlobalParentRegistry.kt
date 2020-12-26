package kekmech.glasscardview.sharing

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnPreDrawListener
import kekmech.glasscardview.GlassCardView
import kotlin.collections.HashMap
import kotlin.math.ceil

internal class SingleParentBitmapHolder(
    private val parentView: ViewGroup,
    private val downscaleFactor: Float
) {
    private var bufferBitmap: Bitmap? = null
    private var bufferCanvas: Canvas? = null
    private val parentRect: Rect = Rect()
    private val Rect.downscaledWidth get() = ceil(width() / downscaleFactor).toInt()
    private val Rect.downscaledHeight get() = ceil(height() / downscaleFactor).toInt()
    private val canReuseBitmapBuffer get() =
        bufferBitmap?.width == parentRect.downscaledWidth &&
                bufferBitmap?.height == parentRect.downscaledHeight

    private val preDrawListener = OnPreDrawListener {
        update()
        true
    }

    init {
        parentView.viewTreeObserver.addOnPreDrawListener(preDrawListener)
    }

    private fun update() {
        parentView.getGlobalVisibleRect(parentRect)
        if (!canReuseBitmapBuffer) {
            bufferBitmap?.recycle()
            bufferBitmap = Bitmap.createBitmap(
                parentRect.downscaledWidth,
                parentRect.downscaledHeight,
                Bitmap.Config.ARGB_8888
            )
            bufferCanvas = Canvas(bufferBitmap!!)
        }
        bufferCanvas!!.scale(1f / downscaleFactor, 1f / downscaleFactor)
        parentView.draw(bufferCanvas)
    }

    fun draw(canvas: Canvas) {
        val bitmap = bufferBitmap ?: return
        canvas.drawBitmap(bitmap, 0f, 0f, null)
    }

    fun destroy() {
        parentView.viewTreeObserver.removeOnPreDrawListener(preDrawListener)
        bufferCanvas = null
        bufferBitmap?.recycle()
        bufferBitmap = null
    }
}

internal object GlobalParentBitmapHolder {

    private val registry: HashMap<ViewGroup, SingleParentBitmapHolder> = HashMap()
    private val counter: HashMap<ViewGroup, Int> = HashMap()

    fun registerView(glassCardView: GlassCardView): SingleParentBitmapHolder {
        val parent = glassCardView.parent as ViewGroup
        if (!counter.containsKey(parent)) {
            counter[parent] = 0
        }
        counter[parent] = counter[parent]!! + 1
        return registry.getOrPut(parent) {
            SingleParentBitmapHolder(
                parentView = parent,
                downscaleFactor = 8f
            )
        }
    }

    fun removeView(glassCardView: GlassCardView) {
        val parent = glassCardView.parent as ViewGroup
        counter[parent] = counter[parent]!! - 1
        if (counter[parent] == 0) {
            registry.remove(parent)
            counter.remove(parent)
        }
    }
}
