package kekmech.glasscardview.blur

import android.graphics.Bitmap
import android.graphics.Color

class EditModeBlurAlgorithm : BlurAlgorithm {

    override fun blur(bitmap: Bitmap, blurRadius: Int): Bitmap {
        val pixels = bitmap.toIntArray()
        val fixedBlurRadius = (if (blurRadius % 2 == 0) blurRadius + 1 else blurRadius)
            .coerceIn(1, 25) / 2

        for (i in 0 until DEFAULT_BOX_BLUR_ITERATIONS) {
            boxBlurHorizontal(pixels, bitmap.width, bitmap.height, fixedBlurRadius)
            boxBlurVertical(pixels, bitmap.width, bitmap.height, fixedBlurRadius)
        }

        bitmap.fromIntArray(pixels)
        return bitmap
    }


    private fun boxBlurHorizontal(
        pixels: IntArray,
        width: Int,
        height: Int,
        radius: Int
    ) {
        var index = 0
        val newColors = IntArray(width)

        for (y in 0 until height) {
            var hits = 0
            var r: Long = 0
            var g: Long = 0
            var b: Long = 0
            for (x in -radius until width) {
                val oldPixel: Int = x - radius - 1
                if (oldPixel >= 0) {
                    val color = pixels[index + oldPixel]
                    if (color != 0) {
                        r -= Color.red(color)
                        g -= Color.green(color)
                        b -= Color.blue(color)
                    }
                    hits--
                }
                val newPixel: Int = x + radius
                if (newPixel < width) {
                    val color = pixels[index + newPixel]
                    if (color != 0) {
                        r += Color.red(color)
                        g += Color.green(color)
                        b += Color.blue(color)
                    }
                    hits++
                }
                if (x >= 0) {
                    newColors[x] = Color.argb(
                        0xFF,
                        (r / hits).toInt(),
                        (g / hits).toInt(),
                        (b / hits).toInt()
                    )
                }
            }
            for (x in 0 until width) {
                pixels[index + x] = newColors[x]
            }
            index += width
        }
    }

    private fun boxBlurVertical(
        pixels: IntArray,
        width: Int,
        height: Int,
        radius: Int
    ) {
        val newColors = IntArray(height)
        val oldPixelOffset = -(radius + 1) * width
        val newPixelOffset = radius * width
        for (x in 0 until width) {
            var hits = 0
            var r: Long = 0
            var g: Long = 0
            var b: Long = 0
            var index = -radius * width + x
            for (y in -radius until height) {
                val oldPixel = y - radius - 1
                if (oldPixel >= 0) {
                    val color = pixels[index + oldPixelOffset]
                    if (color != 0) {
                        r -= Color.red(color).toLong()
                        g -= Color.green(color).toLong()
                        b -= Color.blue(color).toLong()
                    }
                    hits--
                }
                val newPixel = y + radius
                if (newPixel < height) {
                    val color = pixels[index + newPixelOffset]
                    if (color != 0) {
                        r += Color.red(color).toLong()
                        g += Color.green(color).toLong()
                        b += Color.blue(color).toLong()
                    }
                    hits++
                }
                if (y >= 0) {
                    newColors[y] = Color.argb(
                        0xFF,
                        (r / hits).toInt(),
                        (g / hits).toInt(),
                        (b / hits).toInt()
                    )
                }
                index += width
            }
            for (y in 0 until height) {
                pixels[y * width + x] = newColors[y]
            }
        }
    }

    override fun destroy() {
        /* no-op */
    }

    private fun Bitmap.toIntArray(): IntArray {
        val pixels = IntArray(width * height)
        getPixels(pixels, 0, width, 0, 0, width, height)
        return pixels
    }

    private fun Bitmap.fromIntArray(pixels: IntArray) {
        setPixels(pixels, 0, width, 0, 0, width, height)
    }

    companion object {
        private const val DEFAULT_BOX_BLUR_ITERATIONS = 1
    }
}