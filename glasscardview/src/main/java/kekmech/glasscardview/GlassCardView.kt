package kekmech.glasscardview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.StyleRes
import kekmech.glasscardview.blur.GlassBlurController

class GlassCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val blurController = GlassBlurController(this, isInEditMode)
    private val roundRectDrawable = RoundRectDrawable(null, 0f)
    private val mContentPadding = Rect()
    val contentPaddingLeft @Px get() = mContentPadding.left
    val contentPaddingTop @Px get() = mContentPadding.top
    val contentPaddingRight @Px get() = mContentPadding.right
    val contentPaddingBottom @Px get() = mContentPadding.bottom

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
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.GlassCardView, defStyleAttr, R.style.GlassCardView
        )
        backgroundColor = if (a.hasValue(R.styleable.GlassCardView_glassBackgroundColor)) {
            a.getColorStateList(R.styleable.GlassCardView_glassBackgroundColor)
        } else {
            // There isn't one set, so we'll compute one based on the theme
            val aa = getContext().obtainStyledAttributes(intArrayOf(R.attr.colorBackground))
            val themeColorBackground = aa.getColor(0, 0)
            aa.recycle()

            // If the theme colorBackground is light, use our own light color, otherwise dark
            val hsv = FloatArray(3)
            Color.colorToHSV(themeColorBackground, hsv)
            ColorStateList.valueOf(
                if (hsv[2] > 0.5f) {
                    resources.getColor(R.color.cardview_light_background)
                } else {
                    resources.getColor(R.color.cardview_dark_background)
                }
            )
        }
        cornerRadius = a.getDimension(R.styleable.GlassCardView_glassCornerRadius, DEFAULT_CORNER_RADIUS.dp.toFloat())
        blurRadius = a.getDimensionPixelSize(R.styleable.GlassCardView_glassBlurRadius, DEFAULT_BLUR_RADIUS.dp)
        opacity = a.getFloat(R.styleable.GlassCardView_glassOpacity, 0.6f)
        a.recycle()

        background = roundRectDrawable
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
        blurController.isBlurEnabled = false
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        blurController.isBlurEnabled = true
    }

    override fun draw(canvas: Canvas) {
        val shouldDraw = blurController.draw(canvas)
        if (shouldDraw) {
            super.draw(canvas)
        }
    }

    private val Number.dp get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, toFloat(), resources.displayMetrics
    ).toInt()

    companion object {
        const val DOWNSCALE_FACTOR = 8f

        const val DEFAULT_BLUR_RADIUS = 32f
        const val DEFAULT_CORNER_RADIUS = 4f
    }
}