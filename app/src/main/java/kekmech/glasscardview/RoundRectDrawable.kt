package kekmech.glasscardview

import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable

class RoundRectDrawable(
    _backgroundColor: ColorStateList?,
    _cornerRadius: Float
) : Drawable() {

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    private val boundsF: RectF = RectF()
    private val boundsI: Rect = Rect()

    private var tintFilter: PorterDuffColorFilter? = null
    private var tint: ColorStateList? = null
    private var tintMode: PorterDuff.Mode? = PorterDuff.Mode.SRC_IN

    var alpha: Float = 1f
        set(value) {
            if (field == value) return
            field = value
            paint.alpha = (255 * value).toInt()
            invalidateSelf()
        }
    var cornerRadius: Float = _cornerRadius
        set(value) {
            if (field == value) return
            field = value
            updateBounds(null)
            invalidateSelf()
        }
    var backgroundColor: ColorStateList? = _backgroundColor
        set(value) {
            if (field == value) return
            field = value ?: ColorStateList.valueOf(Color.TRANSPARENT)
            paint.color = field!!.getColorForState(state, field!!.defaultColor)
            invalidateSelf()
        }

    override fun draw(canvas: Canvas) {
        val clearColorFilter: Boolean
        if (tintFilter != null && paint.colorFilter == null) {
            paint.colorFilter = tintFilter
            clearColorFilter = true
        } else {
            clearColorFilter = false
        }

        canvas.drawRoundRect(boundsF, cornerRadius, cornerRadius, paint)

        if (clearColorFilter) paint.colorFilter = null
    }

    private fun updateBounds(boundsForUpdate: Rect?) {
        val bounds: Rect = boundsForUpdate ?: bounds
        boundsF.set(bounds.left, bounds.top, bounds.right, bounds.bottom)
        boundsI.set(bounds)
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        updateBounds(bounds)
    }

    override fun getOutline(outline: Outline) = outline.setRoundRect(boundsI, cornerRadius)

    override fun setColorFilter(cf: ColorFilter?) {
        paint.colorFilter = cf
    }

    override fun getOpacity() = PixelFormat.TRANSLUCENT

    override fun setTintList(tint: ColorStateList?) {
        this.tint = tint
        tintFilter = createTintFilter(this.tint, tintMode)
        invalidateSelf()
    }

    override fun setTintMode(tintMode: PorterDuff.Mode?) {
        this.tintMode = tintMode!!
        tintFilter = createTintFilter(tint, this.tintMode)
        invalidateSelf()
    }

    override fun onStateChange(stateSet: IntArray?): Boolean {
        val newColor = this.backgroundColor!!.getColorForState(stateSet, this.backgroundColor!!.defaultColor)
        val colorChanged = newColor != paint.color
        if (colorChanged) {
            paint.color = newColor
        }
        if (tint != null && tintMode != null) {
            tintFilter = createTintFilter(tint, tintMode)
            return true
        }
        return colorChanged
    }

    private fun createTintFilter(
        tint: ColorStateList?,
        tintMode: PorterDuff.Mode?
    ): PorterDuffColorFilter? {
        if (tint == null || tintMode == null) {
            return null
        }
        val color = tint.getColorForState(state, Color.TRANSPARENT)
        return PorterDuffColorFilter(color, tintMode)
    }

    override fun isStateful() =
        (tint != null && tint!!.isStateful ||
                this.backgroundColor != null && this.backgroundColor!!.isStateful || super.isStateful())

    override fun getMinimumHeight() = (cornerRadius * 2).toInt()

    override fun getMinimumWidth() = (cornerRadius * 2).toInt()

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    private fun RectF.set(left: Int, top: Int, right: Int, bottom: Int) =
        set(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
}
