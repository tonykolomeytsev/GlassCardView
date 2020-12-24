package kekmech.glasscardview.blur

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur


class GlassBlurAlgorithm(context: Context) {

    private val renderScript = RenderScript.create(context)
    private val blurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
    private var globalAllocation: Allocation? = null

    private var lastBitmapHeight = -1
    private var lastBitmapWidth = -1

    private val Bitmap.canReuseAllocation: Boolean
        get() = height == lastBitmapHeight && width == lastBitmapWidth

    fun blur(bitmap: Bitmap, blurRadius: Int): Bitmap {
        //Allocation will use the same backing array of pixels as bitmap if created with USAGE_SHARED flag
        val allocation = Allocation.createFromBitmap(renderScript, bitmap)

        if (!bitmap.canReuseAllocation) {
            globalAllocation?.destroy()
            globalAllocation = Allocation.createTyped(renderScript, allocation.type)
            lastBitmapWidth = bitmap.width
            lastBitmapHeight = bitmap.height
        }

        blurScript.setRadius(blurRadius.toFloat())
        blurScript.setInput(allocation)
        //do not use inAllocation in forEach. it will cause visual artifacts on blurred Bitmap
        blurScript.forEach(globalAllocation)
        globalAllocation?.copyTo(bitmap)

        allocation.destroy()
        return bitmap
    }

    fun destroy() {
        blurScript.destroy()
        renderScript.destroy()
        globalAllocation?.destroy()
    }
}