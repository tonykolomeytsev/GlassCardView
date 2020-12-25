package kekmech.glasscardview

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import androidx.annotation.AttrRes

internal fun GlassCardView.loadAttributes(attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int) {
    val a = context.obtainStyledAttributes(
        attrs, R.styleable.GlassCardView, defStyleAttr, R.style.GlassCardView
    )
    backgroundColor = if (a.hasValue(R.styleable.GlassCardView_glassBackgroundColor)) {
        a.getColorStateList(R.styleable.GlassCardView_glassBackgroundColor)
    } else {
        // There isn't one set, so we'll compute one based on the theme
        val aa = context.obtainStyledAttributes(intArrayOf(R.attr.colorBackground))
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
    cornerRadius = a.getDimension(R.styleable.GlassCardView_glassCornerRadius, 0f)
    blurRadius = a.getDimensionPixelSize(R.styleable.GlassCardView_glassBlurRadius, 32)
    opacity = a.getFloat(R.styleable.GlassCardView_glassOpacity, 0.6f)
    a.recycle()
}