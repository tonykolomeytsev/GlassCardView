package kekmech.glasscardview

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.*
import kekmech.glasscardview.blur.GlassBlurController
import kekmech.glasscardview.registry.GlobalBitmapHolder
import kekmech.glasscardview.registry.SingleParentBitmapHolder

class GlassCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private lateinit var blurController: GlassBlurController
    private lateinit var parentBitmapHolder: SingleParentBitmapHolder
    private val roundRectDrawable = RoundRectDrawable(null, 0f)

    var backgroundColor: ColorStateList?
        get() = roundRectDrawable.backgroundColor
        set(value) { roundRectDrawable.backgroundColor = value }
    var cornerRadius: Float
        get() = roundRectDrawable.cornerRadius
        set(value) { roundRectDrawable.cornerRadius = value }
    var opacity: Float
        get() = roundRectDrawable.alpha
        set(value) { roundRectDrawable.alpha = value }
    var blurRadius: Int = 0

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

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        blurController.destroy()
        GlobalBitmapHolder.deregisterView(this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        parentBitmapHolder = GlobalBitmapHolder.registerView(this)
        blurController = GlassBlurController(
            this,
            parentBitmapHolder,
            isInEditMode
        )
        blurController.isBlurEnabled = true
    }

    override fun draw(canvas: Canvas) {
        if (!parentBitmapHolder.shouldDraw()) return
        val shouldDraw = blurController.draw(canvas)
        if (shouldDraw) {
            super.draw(canvas)
        }
    }

    companion object {
        internal const val DOWNSCALE_FACTOR = 8f

        const val DEFAULT_BLUR_RADIUS = 32f
        const val DEFAULT_CORNER_RADIUS = 4f
        const val DEFAULT_OPACITY = 0.6f
    }
}