package kekmech.glasscardview.rect

import android.content.Context
import android.content.res.ColorStateList
import kekmech.glasscardview.blur.GlassBlurController

interface GlassCardViewImpl {

    fun initialize(
        cardView: GlassCardViewDelegate,
        context: Context,
        backgroundColor: ColorStateList,
        radius: Float,
        elevation: Float,
        maxElevation: Float,
        blurRadius: Int,
        opacity: Float,
        blurController: GlassBlurController
    )

    fun setRadius(cardView: GlassCardViewDelegate, radius: Float)

    fun getRadius(cardView: GlassCardViewDelegate): Float

    fun setElevation(cardView: GlassCardViewDelegate, elevation: Float)

    fun getElevation(cardView: GlassCardViewDelegate): Float

    fun setMaxElevation(cardView: GlassCardViewDelegate, maxElevation: Float)

    fun getMaxElevation(cardView: GlassCardViewDelegate): Float

    fun getMinWidth(cardView: GlassCardViewDelegate): Float

    fun getMinHeight(cardView: GlassCardViewDelegate): Float

    fun updatePadding(cardView: GlassCardViewDelegate)

    fun setBackgroundColor(cardView: GlassCardViewDelegate, color: ColorStateList?)

    fun getBackgroundColor(cardView: GlassCardViewDelegate): ColorStateList

    fun setBlurRadius(cardView: GlassCardViewDelegate, blurRadius: Int)

    fun getBlurRadius(cardView: GlassCardViewDelegate): Int

    fun setOpacity(cardView: GlassCardViewDelegate, opacity: Float)

    fun getOpacity(cardView: GlassCardViewDelegate): Float
}