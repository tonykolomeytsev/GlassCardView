package kekmech.glasscardview.buffer

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver

internal class SingleViewFrameBuffer(
    private val frameBufferView: View
) {
    private var bufferBitmap: Bitmap? = null
    private var bufferCanvas: Canvas? = null
    private val parentRect: Rect = Rect()
    private var bufferWidth: Int = -1
    private var bufferHeight: Int = -1
    private val canReuseBitmapBuffer get() =
        bufferBitmap?.width == bufferWidth && bufferBitmap?.height == bufferHeight

    private var isParentDrawingInProgress: Boolean = true
    private val preDrawListener = ViewTreeObserver.OnPreDrawListener {
        update()
        true
    }

    init {
        frameBufferView.viewTreeObserver.addOnPreDrawListener(preDrawListener)
    }

    fun update() {
        isParentDrawingInProgress = true
        frameBufferView.getGlobalVisibleRect(parentRect)
        if (!canReuseBitmapBuffer) {
            bufferWidth = parentRect.width()
            bufferHeight = parentRect.height()
            bufferBitmap?.recycle()
            bufferBitmap = Bitmap.createBitmap(
                bufferWidth,
                bufferHeight,
                Bitmap.Config.ARGB_8888
            )
            bufferCanvas = Canvas(bufferBitmap!!)
        }
        frameBufferView.draw(bufferCanvas)
        isParentDrawingInProgress = false
    }

    fun draw(canvas: Canvas) {
        val bitmap = bufferBitmap ?: return
        canvas.drawBitmap(bitmap, 0f, 0f, null)
    }

    fun destroy() {
        frameBufferView.viewTreeObserver.removeOnPreDrawListener(preDrawListener)
        bufferCanvas = null
        bufferBitmap?.recycle()
        bufferBitmap = null
    }

    fun shouldDraw() = !isParentDrawingInProgress
}