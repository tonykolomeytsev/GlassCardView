package kekmech.glasscardview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Rect
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.Px
import androidx.annotation.StyleRes
import kekmech.glasscardview.opengl.GlassRenderer


class GlassCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var glSurface: GLSurfaceView = GLSurfaceView(context)
    private var renderer: GLSurfaceView.Renderer = GlassRenderer(context)
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
        loadAttributes(attrs, defStyleAttr)
        setupOpenGLSurfaceView()
        roundRectDrawable.elevation = 4f
        background = roundRectDrawable
    }

    private fun setupOpenGLSurfaceView() {
        with(glSurface) {
            setEGLContextClientVersion(2)
            setRenderer(renderer)
            addView(glSurface)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        glSurface.onPause()
    }

    override fun onAttachedToWindow() {
        glSurface.onResume()
        super.onAttachedToWindow()
    }
}