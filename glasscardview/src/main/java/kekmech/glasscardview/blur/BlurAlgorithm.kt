package kekmech.glasscardview.blur

import android.graphics.Bitmap

interface BlurAlgorithm {
    fun blur(bitmap: Bitmap, blurRadius: Int): Bitmap
    fun destroy()
}