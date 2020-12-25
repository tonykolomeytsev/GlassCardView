package kekmech.glasscardview.opengl

import android.opengl.GLES20.*

@Suppress("NOTHING_TO_INLINE")
inline fun glDrawTriangles(first: Int, count: Int) =
    glDrawArrays(GL_TRIANGLES, first, count)

@Suppress("NOTHING_TO_INLINE")
inline fun glDrawStripes(first: Int, count: Int) =
    glDrawArrays(GL_TRIANGLE_STRIP, first, count)

@Suppress("NOTHING_TO_INLINE")
inline fun glDrawFan(first: Int, count: Int) =
    glDrawArrays(GL_TRIANGLE_FAN, first, count)

