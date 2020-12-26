package kekmech.glasscardview.opengl

import android.opengl.GLES20.*

@Suppress("NOTHING_TO_INLINE")
inline fun glDrawTriangles(first: Int, count: Int) =
    glDrawArrays(GL_TRIANGLES, first, count)

@Suppress("NOTHING_TO_INLINE")
inline fun glDrawStripes(first: Int, count: Int) =
    glDrawArrays(GL_TRIANGLE_STRIP, first, count)

@Suppress("NOTHING_TO_INLINE")
inline fun glDrawFans(first: Int, count: Int) =
    glDrawArrays(GL_TRIANGLE_FAN, first, count)

@Suppress("NOTHING_TO_INLINE")
inline fun glDrawLines(first: Int, count: Int) =
    glDrawArrays(GL_LINES, first, count)

