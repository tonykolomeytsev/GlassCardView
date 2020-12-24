package kekmech.glasscardview

import android.graphics.drawable.Drawable
import android.view.View

interface GlassCardViewDelegate {
    var cardBackground: Drawable?
    val cardView: View

    fun setShadowPadding(left: Int, top: Int, right: Int, bottom: Int)
}