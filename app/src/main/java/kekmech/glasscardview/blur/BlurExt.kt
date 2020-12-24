package kekmech.glasscardview.blur

import android.view.View
import kekmech.glasscardview.GlassCardView
import kotlin.math.ceil

private const val ROUNDING_VALUE = 64

fun View.isZeroMeasured() = measuredHeight.downscaleSize() == 0 || measuredWidth.downscaleSize() == 0

fun Int.downscaleSize() = ceil(this / GlassCardView.DOWNSCALE_FACTOR).toInt()

fun Int.roundTo(value: Int): Int {
    return if (this % value == 0) {
        this
    } else {
        this - this % value + value
    }
}

fun View.measureDownscaledValues(): Triple<Int, Int, Float> {
    val scaledWidth: Int = measuredWidth.downscaleSize().roundTo(ROUNDING_VALUE)
    //Only width has to be aligned to ROUNDING_VALUE
    val roundingScaleFactor = measuredWidth.toFloat() / scaledWidth
    //Ceiling because rounding or flooring might leave empty space on the View's bottom
    val scaledHeight = ceil((measuredHeight / roundingScaleFactor).toDouble()).toInt()
    return Triple(scaledWidth, scaledHeight, roundingScaleFactor)
}