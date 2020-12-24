package kekmech.glasscardview

import android.content.Context
import android.content.res.ColorStateList

interface GlassCardViewImpl {

    fun initialize(
        cardView: GlassCardViewDelegate,
        context: Context,
        backgroundColor: ColorStateList,
        radius: Float,
        elevation: Float,
        maxElevation: Float
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
}