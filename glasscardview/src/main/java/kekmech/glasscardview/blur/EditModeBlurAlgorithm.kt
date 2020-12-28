package kekmech.glasscardview.blur

import android.graphics.Bitmap


class EditModeBlurAlgorithm : BlurAlgorithm {

    override fun blur(bitmap: Bitmap, blurRadius: Int): Bitmap {
        val source = bitmap.toIntArray()
        val target = bitmap.toIntArray()
        val fixedBlurRadius = (if (blurRadius % 2 == 0) blurRadius + 1 else blurRadius)
            .coerceIn(1, 25)

        for (i in 0 until DEFAULT_BOX_BLUR_ITERATIONS) {
            boxBlur(source, target, bitmap.width, bitmap.height, fixedBlurRadius)
            boxBlur(target, source, bitmap.height, bitmap.width, fixedBlurRadius)
        }

        bitmap.fromIntArray(target)
        return bitmap
    }


    private fun boxBlur(`in`: IntArray, `out`: IntArray, width: Int, height: Int, radius: Int) {
        val widthMinus1 = width - 1
        val tableSize = 2 * radius + 1
        val divide = IntArray(256 * tableSize)

        for (i in 0 until 256 * tableSize) divide[i] = i / tableSize

        var inIndex = 0

        for (y in 0 until height) {
            var outIndex = y
            var ta = 0
            var tr = 0
            var tg = 0
            var tb = 0
            for (i in -radius..radius) {
                val rgb = `in`[inIndex + clamp(i, 0, width - 1)]
                ta += rgb shr 24 and 0xff
                tr += rgb shr 16 and 0xff
                tg += rgb shr 8 and 0xff
                tb += rgb and 0xff
            }
            for (x in 0 until width) {
                out[outIndex] =
                    divide[ta] shl 24 or (divide[tr] shl 16) or (divide[tg] shl 8) or divide[tb]
                var i1 = x + radius + 1
                if (i1 > widthMinus1) i1 = widthMinus1
                var i2 = x - radius
                if (i2 < 0) i2 = 0
                val rgb1 = `in`[inIndex + i1]
                val rgb2 = `in`[inIndex + i2]
                ta += (rgb1 shr 24 and 0xff) - (rgb2 shr 24 and 0xff)
                tr += (rgb1 and 0xff0000) - (rgb2 and 0xff0000) shr 16
                tg += (rgb1 and 0xff00) - (rgb2 and 0xff00) shr 8
                tb += (rgb1 and 0xff) - (rgb2 and 0xff)
                outIndex += height
            }
            inIndex += width
        }
    }

    override fun destroy() {
        /* no-op */
    }

    private fun Bitmap.toIntArray(): IntArray {
        val buffer = IntArray(width * height)
        var i = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                buffer[i++] = getPixel(x, y)
            }
        }
        return buffer
    }

    private fun Bitmap.fromIntArray(buffer: IntArray) {
        var i = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                setPixel(x, y, buffer[i++])
            }
        }
    }

    private fun clamp(x: Int, a: Int, b: Int): Int {
        return if (x < a) a else if (x > b) b else x
    }

    companion object {
        private const val DEFAULT_BOX_BLUR_ITERATIONS = 1
    }
}