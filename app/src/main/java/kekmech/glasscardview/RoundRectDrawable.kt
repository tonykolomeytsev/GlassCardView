package kekmech.glasscardview

import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable


class RoundRectDrawable(backgroundColor: ColorStateList?, radius: Float) : Drawable() {

    private var mRadius: Float = radius
    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    private val mBoundsF: RectF = RectF()
    private val mBoundsI: Rect = Rect()
    private var mPadding: Float = 0f

    private var mBackground: ColorStateList? = null
    private var mTintFilter: PorterDuffColorFilter? = null
    private var mTint: ColorStateList? = null
    private var mTintMode: PorterDuff.Mode? = PorterDuff.Mode.SRC_IN

    init {
        setBackground(backgroundColor)
    }

    private fun setBackground(color: ColorStateList?) {
        mBackground = color ?: ColorStateList.valueOf(Color.TRANSPARENT)
        mPaint.color = mBackground!!.getColorForState(state, mBackground!!.defaultColor)
    }

    fun setPadding(padding: Float) {
        if (padding == mPadding) {
            return
        }
        mPadding = padding
        updateBounds(null)
        invalidateSelf()
    }

    fun getPadding() = mPadding

    override fun draw(canvas: Canvas) {
        val paint = mPaint
        val clearColorFilter: Boolean
        if (mTintFilter != null && paint.colorFilter == null) {
            paint.colorFilter = mTintFilter
            clearColorFilter = true
        } else {
            clearColorFilter = false
        }

        canvas.drawRoundRect(mBoundsF, mRadius, mRadius, paint)

        if (clearColorFilter) {
            paint.colorFilter = null
        }
    }

    private fun updateBounds(boundsForUpdate: Rect?) {
        val bounds: Rect = boundsForUpdate ?: bounds
        mBoundsF.set(bounds.left, bounds.top, bounds.right, bounds.bottom)
        mBoundsI.set(bounds)
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        updateBounds(bounds)
    }

    override fun getOutline(outline: Outline) {
        outline.setRoundRect(mBoundsI, mRadius)
    }

    fun setRadius(radius: Float) {
        if (radius == mRadius) {
            return
        }
        mRadius = radius
        updateBounds(null)
        invalidateSelf()
    }

    fun getRadius() = mRadius

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        mPaint.colorFilter = cf
    }

    override fun getOpacity() = PixelFormat.TRANSLUCENT

    fun setColor(color: ColorStateList?) {
        setBackground(color)
        invalidateSelf()
    }

    fun getColor() = mBackground

    override fun setTintList(tint: ColorStateList?) {
        mTint = tint
        mTintFilter = createTintFilter(mTint, mTintMode)
        invalidateSelf()
    }

    override fun setTintMode(tintMode: PorterDuff.Mode?) {
        mTintMode = tintMode!!
        mTintFilter = createTintFilter(mTint, mTintMode)
        invalidateSelf()
    }

    override fun onStateChange(stateSet: IntArray?): Boolean {
        val newColor = mBackground!!.getColorForState(stateSet, mBackground!!.defaultColor)
        val colorChanged = newColor != mPaint.color
        if (colorChanged) {
            mPaint.color = newColor
        }
        if (mTint != null && mTintMode != null) {
            mTintFilter = createTintFilter(mTint, mTintMode)
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
        (mTint != null && mTint!!.isStateful ||
                mBackground != null && mBackground!!.isStateful || super.isStateful())

    private fun RectF.set(left: Int, top: Int, right: Int, bottom: Int) =
        set(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
}
