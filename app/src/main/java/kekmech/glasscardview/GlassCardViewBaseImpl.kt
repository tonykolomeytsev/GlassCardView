package kekmech.glasscardview

import android.content.Context
import android.content.res.ColorStateList
import android.view.View


class GlassCardViewBaseImpl : GlassCardViewImpl {

    override fun initialize(
        cardView: GlassCardViewDelegate,
        context: Context,
        backgroundColor: ColorStateList,
        radius: Float,
        elevation: Float,
        maxElevation: Float
    ) {
        val background = RoundRectDrawable(backgroundColor, radius)
        cardView.cardBackground = background

        val view: View = cardView.cardView
        view.clipToOutline = true
        view.elevation = elevation
        setMaxElevation(cardView, maxElevation)
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

    private fun getCardBackground(cardView: GlassCardViewDelegate): RoundRectDrawable =
        cardView.cardBackground as RoundRectDrawable
}