package kekmech.glasscardview.registry

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.view.ViewGroup
import android.view.ViewTreeObserver
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

    private var isParentDrawingInProgress: Boolean = true
    private val preDrawListener = ViewTreeObserver.OnPreDrawListener {
        update()
        true
    }

    init {
        parentView.viewTreeObserver.addOnPreDrawListener(preDrawListener)
    }

    private fun update() {
        isParentDrawingInProgress = true
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
        isParentDrawingInProgress = false
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

    fun shouldDraw() = !isParentDrawingInProgress
}