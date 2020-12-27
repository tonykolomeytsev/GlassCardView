package kekmech.glasscardview

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.annotation.*
import kekmech.glasscardview.blur.GlassBlurController
import kekmech.glasscardview.buffer.GlobalFrameBuffer
import kekmech.glasscardview.buffer.SingleViewFrameBuffer

class GlassCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
    _framesSourceView: View? = null
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private lateinit var blurController: GlassBlurController
    private lateinit var viewFrameBuffer: SingleViewFrameBuffer
    private lateinit var preDrawListener: ViewTreeObserver.OnPreDrawListener
    private val roundRectDrawable = RoundRectDrawable(null, 0f)

    var backgroundColor: ColorStateList?
        get() = roundRectDrawable.backgroundColor
        set(value) { roundRectDrawable.backgroundColor = value }
    var cornerRadius: Float
        get() = roundRectDrawable.cornerRadius
        set(value) {
            roundRectDrawable.cornerRadius = value.coerceAtLeast(0f)
            if (this::blurController.isInitialized) blurController.updateTintPath()
        }
    var opacity: Float
        get() = roundRectDrawable.alpha
        set(value) { roundRectDrawable.alpha = value.coerceIn(0f, 1f) }
    var blurRadius: Int = 0
        set(value) { field = value.coerceIn(BLUR_RADIUS_MIN, BLUR_RADIUS_MAX) }
    var framesSourceView: View? = _framesSourceView
        set(value) {
            if (isAttachedToWindow) {
                detachFrameSourceView()
                field = value
                attachFrameSourceView()
                invalidate()
            } else {
                field = value
            }
        }

    init {
        context.useAttributes(
            attrs,
            R.styleable.GlassCardView,
            defStyleAttr,
            R.style.GlassCardView
        ) {
            backgroundColor = getBackgroundColor(context)
            cornerRadius = getDimension(
                R.styleable.GlassCardView_glassCornerRadius,
                resources.dpToPx(DEFAULT_CORNER_RADIUS)
            )
            blurRadius = getDimension(
                R.styleable.GlassCardView_glassBlurRadius,
                resources.dpToPx(DEFAULT_BLUR_RADIUS)
            ).toInt()
            opacity = getFloat(R.styleable.GlassCardView_glassOpacity, DEFAULT_OPACITY)
        }
        background = roundRectDrawable
        Log.d(TAG, "Allocating GlassCardView")
    }

    private fun TypedArray.getBackgroundColor(context: Context): ColorStateList? {
        return if (hasValue(R.styleable.GlassCardView_glassBackgroundColor)) {
            getColorStateList(R.styleable.GlassCardView_glassBackgroundColor)
        } else {
            ColorStateList.valueOf(context.getResColor(R.color.cardview_light_background))
        }
    }

    override fun setBackgroundColor(@ColorInt color: Int) {
        backgroundColor = ColorStateList.valueOf(color)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        blurController.updateBlurViewSize()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        attachFrameSourceView()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        detachFrameSourceView()
    }

    override fun draw(canvas: Canvas) {
        if (!viewFrameBuffer.shouldDraw()) return
        val shouldDraw = blurController.draw(canvas)
        if (shouldDraw) {
            super.draw(canvas)
        }
    }

    private fun attachFrameSourceView() {
        viewFrameBuffer = GlobalFrameBuffer
            .registerView(framesSourceView ?: parent as View)
        blurController = GlassBlurController(
            blurView = this,
            framesSourceView = framesSourceView ?: parent as View,
            frameBuffer = viewFrameBuffer,
            isInEditMode = isInEditMode
        )
        blurController.isBlurEnabled = true
        preDrawListener = ViewTreeObserver.OnPreDrawListener {
            viewFrameBuffer.update()
            true
        }
        viewTreeObserver.addOnPreDrawListener(preDrawListener)
    }

    private fun detachFrameSourceView() {
        viewTreeObserver.removeOnPreDrawListener(preDrawListener)
        blurController.destroy()
        GlobalFrameBuffer.deregisterView(framesSourceView ?: parent as View)
    }

    companion object {
        private const val TAG = "GlassCardView"
        internal const val DOWNSCALE_FACTOR = 8f

        const val DEFAULT_BLUR_RADIUS = 32
        const val BLUR_RADIUS_MIN = 8
        const val BLUR_RADIUS_MAX = 25 * 8

        const val DEFAULT_CORNER_RADIUS = 4f
        const val DEFAULT_OPACITY = 0.6f
    }
}