package kekmech.glasscardview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.StyleRes
import kekmech.glasscardview.blur.GlassBlurController
import kekmech.glasscardview.rect.GlassCardViewBaseImpl
import kekmech.glasscardview.rect.GlassCardViewDelegate
import kekmech.glasscardview.rect.GlassCardViewImpl


class GlassCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val blurController = GlassBlurController(this)
    private val cardViewImpl: GlassCardViewImpl = GlassCardViewBaseImpl()
    private val mContentPadding = Rect()
    val contentPaddingLeft @Px get() = mContentPadding.left
    val contentPaddingTop @Px get() = mContentPadding.top
    val contentPaddingRight @Px get() = mContentPadding.right
    val contentPaddingBottom @Px get() = mContentPadding.bottom
    private val mShadowBounds = Rect()
    private val mCardViewDelegate = object : GlassCardViewDelegate {

        override val cardView get() = this@GlassCardView
        override var cardBackground: Drawable? = null
            set(drawable) {
                field = drawable
                setBackgroundDrawable(drawable)
            }

        override fun setShadowPadding(left: Int, top: Int, right: Int, bottom: Int) {
            mShadowBounds.set(left, top, right, bottom)
            super@GlassCardView.setPadding(
                left + mContentPadding.left,
                top + mContentPadding.top,
                right + mContentPadding.right,
                bottom + mContentPadding.bottom
            )
        }
    }

    var radius: Float
        get() = cardViewImpl.getRadius(mCardViewDelegate)
        set(value) = cardViewImpl.setRadius(mCardViewDelegate, value)
    var cardElevation: Float
        get() = cardViewImpl.getElevation(mCardViewDelegate)
        set(value) = cardViewImpl.setElevation(mCardViewDelegate, value)
    var maxCardElevation: Float
        get() = cardViewImpl.getMaxElevation(mCardViewDelegate)
        set(value) = cardViewImpl.setMaxElevation(mCardViewDelegate, value)
    var blurRadius: Int
        get() = cardViewImpl.getBlurRadius(mCardViewDelegate)
        set(value) = cardViewImpl.setBlurRadius(mCardViewDelegate, value)

    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.GlassCardView, defStyleAttr, R.style.GlassCardView
        )
        val backgroundColor: ColorStateList?
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
                if (hsv[2] > 0.5f) resources.getColor(R.color.cardview_light_background) else resources.getColor(
                    R.color.cardview_dark_background
                )
            )
        }
        val radius = a.getDimension(R.styleable.GlassCardView_glassCornerRadius, 0f)
        val elevation = a.getDimension(R.styleable.GlassCardView_glassElevation, 0f)
        var maxElevation = a.getDimension(R.styleable.GlassCardView_glassMaxElevation, 0f)
        val defaultPadding = a.getDimensionPixelSize(R.styleable.GlassCardView_contentPadding, 0)
        val blurRadius = a.getDimensionPixelSize(R.styleable.GlassCardView_glassBlurRadius, 8)
        val opacity = a.getFloat(R.styleable.GlassCardView_glassOpacity, 0.4f)
        mContentPadding.left = a.getDimensionPixelSize(
            R.styleable.GlassCardView_contentPaddingLeft,
            defaultPadding
        )
        mContentPadding.top = a.getDimensionPixelSize(
            R.styleable.GlassCardView_contentPaddingTop,
            defaultPadding
        )
        mContentPadding.right = a.getDimensionPixelSize(
            R.styleable.GlassCardView_contentPaddingRight,
            defaultPadding
        )
        mContentPadding.bottom = a.getDimensionPixelSize(
            R.styleable.GlassCardView_contentPaddingBottom,
            defaultPadding
        )
        if (elevation > maxElevation) {
            maxElevation = elevation
        }
        a.recycle()

        cardViewImpl.initialize(
            cardView = mCardViewDelegate,
            context = context,
            backgroundColor = backgroundColor!!,
            radius = radius,
            elevation = elevation,
            maxElevation = maxElevation,
            blurRadius = blurRadius,
            opacity = opacity,
            blurController = blurController
        )
    }

    fun setCardBackgroundColor(@ColorInt color: Int) {
        cardViewImpl.setBackgroundColor(mCardViewDelegate, ColorStateList.valueOf(color))
    }

    fun setCardBackgroundColor(color: ColorStateList?) {
        cardViewImpl.setBackgroundColor(mCardViewDelegate, color)
    }

    fun getCardBackgroundColor(): ColorStateList {
        return cardViewImpl.getBackgroundColor(mCardViewDelegate)
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

    companion object {
        const val DOWNSCALE_FACTOR = 4f
    }
}