package kekmech.glasscardview.blur

import android.graphics.*
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import kekmech.glasscardview.GlassCardView
import kekmech.glasscardview.registry.SingleParentBitmapHolder
import kotlin.math.ceil

private const val ROUNDING_VALUE = 64

internal class GlassBlurController(
    private val blurView: GlassCardView,
    private val parentBitmapHolder: SingleParentBitmapHolder,
    isInEditMode: Boolean
) : BlurController {

    private val blurAlgorithm: GlassBlurAlgorithm? = if (isInEditMode) null else GlassBlurAlgorithm(blurView.context)
    private var bufferBitmap: Bitmap? = null
    private var bufferCanvas: Canvas? = null
    private var bufferPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    private val bufferDownscaleFactor: Float = 8f
    private val containerViewLocation = IntArray(2) { 0 }
    private val blurViewLocation = IntArray(2) { 0 }
    private var tintPath: Path = Path()

    private val blurRadius get() = (blurView.blurRadius / GlassCardView.DOWNSCALE_FACTOR).toInt()
    private val preDrawListener = ViewTreeObserver.OnPreDrawListener {
        updateBlur()
        true
    }

    var isBlurEnabled: Boolean = false

    init {
        blurView.viewTreeObserver.addOnPreDrawListener(preDrawListener)
        initialize()
    }

    private fun initialize() {
        if (blurView.isZeroMeasured()) {
            blurView.setWillNotDraw(true)
            return
        }
        blurView.setWillNotDraw(false)
        allocateBitmap()
        updateTintPath()
        updateBufferCanvasMatrix()
    }

    private fun updateTintPath() {
        tintPath = Path()
        tintPath.addRoundRect(
            RectF(0f, 0f, blurView.measuredWidth.toFloat(), blurView.measuredHeight.toFloat()),
            blurView.cornerRadius,
            blurView.cornerRadius,
            Path.Direction.CW
        )
    }

    private fun updateBlur() {
        if (!isBlurEnabled) return
        bufferBitmap?.eraseColor(Color.TRANSPARENT)
        with(bufferCanvas!!) {
            save()
            updateBufferCanvasMatrix()
            parentBitmapHolder.draw(this)
            restore()
        }

        bufferBitmap = bufferBitmap?.let { blurAlgorithm?.blur(it, blurRadius) }
        bufferCanvas?.setBitmap(bufferBitmap)
    }

    private fun allocateBitmap() {
        val (downscaledWidth, downscaledHeight, downscaleFactor) = blurView.measureDownscaledValues()
        //bufferDownscaleFactor = downscaleFactor
        bufferBitmap = Bitmap.createBitmap(
            downscaledWidth,
            downscaledHeight,
            Bitmap.Config.ARGB_8888
        )
        bufferCanvas = Canvas(bufferBitmap!!)
    }

    private fun updateBufferCanvasMatrix() {
        (blurView.parent as ViewGroup).getLocationOnScreen(containerViewLocation)
        blurView.getLocationOnScreen(blurViewLocation)
        val left: Int = blurViewLocation[0] - containerViewLocation[0]
        val top: Int = blurViewLocation[1] - containerViewLocation[1]
        val scaledLeftPosition: Float = -left / bufferDownscaleFactor
        val scaledTopPosition: Float = -top / bufferDownscaleFactor

        bufferCanvas?.translate(scaledLeftPosition, scaledTopPosition)
        //bufferCanvas?.scale(1f / bufferDownscaleFactor, 1f / bufferDownscaleFactor)
    }

    override fun draw(canvas: Canvas): Boolean {
        if (!isBlurEnabled) return true
        if (canvas == bufferCanvas) return false

        updateBlur()
        canvas.save()
        canvas.clipPath(tintPath)
        canvas.scale(bufferDownscaleFactor, bufferDownscaleFactor)
        bufferBitmap?.let { canvas.drawBitmap(it, 0f, 0f, bufferPaint) }
        bufferPaint.xfermode = null
        canvas.restore()

        return true
    }

    override fun updateBlurViewSize() = initialize()

    override fun destroy() {
        isBlurEnabled = false
        blurAlgorithm?.destroy()
    }

    private fun View.measureDownscaledValues(): Triple<Int, Int, Float> {
        // downscale width to
        val scaledWidth: Int = measuredWidth.downscaleSize().roundTo(ROUNDING_VALUE)
        val roundingScaleFactor = measuredWidth.toFloat() / scaledWidth
        val scaledHeight = ceil((measuredHeight / roundingScaleFactor).toDouble()).toInt()
        return Triple(scaledWidth, scaledHeight, roundingScaleFactor)
    }

    private fun View.isZeroMeasured(): Boolean =
        measuredHeight.downscaleSize() == 0 || measuredWidth.downscaleSize() == 0

    private fun Int.downscaleSize(): Int =
        ceil(this / GlassCardView.DOWNSCALE_FACTOR).toInt()

    private fun Int.roundTo(value: Int): Int =
        if (this % value == 0) this else this - this % value + value
}