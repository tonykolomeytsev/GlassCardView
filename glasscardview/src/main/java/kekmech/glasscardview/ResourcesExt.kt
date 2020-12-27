package kekmech.glasscardview

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.*

@Px
internal fun Resources.dpToPx(number: Number): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, number.toFloat(), displayMetrics
)

@ColorInt
internal fun Context.getResColor(@ColorRes color: Int): Int =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        getColor(color)
    } else {
        @Suppress("DEPRECATION") resources.getColor(color)
    }

internal fun Context.useAttributes(
    attrs: AttributeSet?,
    @StyleableRes styleable: IntArray,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    user: TypedArray.() -> Unit)
{
    val array = obtainStyledAttributes(attrs, styleable, defStyleAttr, defStyleRes)
    user.invoke(array)
    array.recycle()
}