package kekmech.glasscardview.rect

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import kekmech.glasscardview.blur.GlassBlurController


class GlassCardViewBaseImpl : GlassCardViewImpl {

    override fun initialize(
        cardView: GlassCardViewDelegate,
        context: Context,
        backgroundColor: ColorStateList,
        radius: Float,
        elevation: Float,
        maxElevation: Float,
        blurRadius: Int,
        opacity: Float,
        blurController: GlassBlurController
    ) {
        val background = RoundRectDrawable(backgroundColor, radius, blurRadius, blurController)
        cardView.cardBackground = background

        val view: View = cardView.cardView
        view.clipToOutline = true
        view.elevation = elevation
        setMaxElevation(cardView, maxElevation)
        setOpacity(cardView, opacity)
    }

    override fun setRadius(cardView: GlassCardViewDelegate, radius: Float) {
        getCardBackground(cardView).setRadius(radius)
    }

    override fun setMaxElevation(cardView: GlassCardViewDelegate, maxElevation: Float) {
        getCardBackground(cardView).setPadding(maxElevation)
        updatePadding(cardView)
    }

    override fun getMaxElevation(cardView: GlassCardViewDelegate): Float {
        return getCardBackground(cardView).getPadding()
    }

    override fun getMinWidth(cardView: GlassCardViewDelegate): Float {
        return getRadius(cardView) * 2
    }

    override fun getMinHeight(cardView: GlassCardViewDelegate): Float {
        return getRadius(cardView) * 2
    }

    override fun getRadius(cardView: GlassCardViewDelegate): Float {
        return getCardBackground(cardView).getRadius()
    }

    override fun setElevation(cardView: GlassCardViewDelegate, elevation: Float) {
        cardView.cardView.elevation = elevation
    }

    override fun getElevation(cardView: GlassCardViewDelegate): Float {
        return cardView.cardView.elevation
    }

    override fun updatePadding(cardView: GlassCardViewDelegate) {
        cardView.setShadowPadding(0, 0, 0, 0)
    }

    override fun setBackgroundColor(cardView: GlassCardViewDelegate, color: ColorStateList?) {
        getCardBackground(cardView).setColor(color)
    }

    override fun getBackgroundColor(cardView: GlassCardViewDelegate): ColorStateList {
        return getCardBackground(cardView).getColor()!!
    }

    override fun setBlurRadius(cardView: GlassCardViewDelegate, blurRadius: Int) {
        getCardBackground(cardView).setBlurRadius(blurRadius)
    }

    override fun getBlurRadius(cardView: GlassCardViewDelegate): Int {
        return getCardBackground(cardView).getBlurRadius()
    }

    override fun setOpacity(cardView: GlassCardViewDelegate, opacity: Float) {
        getCardBackground(cardView).setOpacity(opacity)
    }

    override fun getOpacity(cardView: GlassCardViewDelegate): Float {
        return getCardBackground(cardView).getOpacityInternal()
    }

    private fun getCardBackground(cardView: GlassCardViewDelegate): RoundRectDrawable =
        cardView.cardBackground as RoundRectDrawable
}