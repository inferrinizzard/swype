package android.support.v7.widget;

import android.content.Context;
import android.view.View;

/* loaded from: classes.dex */
final class CardViewApi21 implements CardViewImpl {
    @Override // android.support.v7.widget.CardViewImpl
    public final void initialize(CardViewDelegate cardView, Context context, int backgroundColor, float radius, float elevation, float maxElevation) {
        RoundRectDrawable background = new RoundRectDrawable(backgroundColor, radius);
        cardView.setCardBackground(background);
        View view = cardView.getCardView();
        view.setClipToOutline(true);
        view.setElevation(elevation);
        setMaxElevation(cardView, maxElevation);
    }

    @Override // android.support.v7.widget.CardViewImpl
    public final void setRadius(CardViewDelegate cardView, float radius) {
        RoundRectDrawable cardBackground = getCardBackground(cardView);
        if (radius == cardBackground.mRadius) {
            return;
        }
        cardBackground.mRadius = radius;
        cardBackground.updateBounds(null);
        cardBackground.invalidateSelf();
    }

    @Override // android.support.v7.widget.CardViewImpl
    public final void initStatic() {
    }

    @Override // android.support.v7.widget.CardViewImpl
    public final void setMaxElevation(CardViewDelegate cardView, float maxElevation) {
        RoundRectDrawable cardBackground = getCardBackground(cardView);
        boolean useCompatPadding = cardView.getUseCompatPadding();
        boolean preventCornerOverlap = cardView.getPreventCornerOverlap();
        if (maxElevation != cardBackground.mPadding || cardBackground.mInsetForPadding != useCompatPadding || cardBackground.mInsetForRadius != preventCornerOverlap) {
            cardBackground.mPadding = maxElevation;
            cardBackground.mInsetForPadding = useCompatPadding;
            cardBackground.mInsetForRadius = preventCornerOverlap;
            cardBackground.updateBounds(null);
            cardBackground.invalidateSelf();
        }
        updatePadding(cardView);
    }

    @Override // android.support.v7.widget.CardViewImpl
    public final float getMaxElevation(CardViewDelegate cardView) {
        return getCardBackground(cardView).mPadding;
    }

    @Override // android.support.v7.widget.CardViewImpl
    public final float getRadius(CardViewDelegate cardView) {
        return getCardBackground(cardView).mRadius;
    }

    @Override // android.support.v7.widget.CardViewImpl
    public final void setElevation(CardViewDelegate cardView, float elevation) {
        cardView.getCardView().setElevation(elevation);
    }

    @Override // android.support.v7.widget.CardViewImpl
    public final float getElevation(CardViewDelegate cardView) {
        return cardView.getCardView().getElevation();
    }

    @Override // android.support.v7.widget.CardViewImpl
    public final void updatePadding(CardViewDelegate cardView) {
        if (!cardView.getUseCompatPadding()) {
            cardView.setShadowPadding(0, 0, 0, 0);
            return;
        }
        float elevation = getCardBackground(cardView).mPadding;
        float radius = getCardBackground(cardView).mRadius;
        int hPadding = (int) Math.ceil(RoundRectDrawableWithShadow.calculateHorizontalPadding(elevation, radius, cardView.getPreventCornerOverlap()));
        int vPadding = (int) Math.ceil(RoundRectDrawableWithShadow.calculateVerticalPadding(elevation, radius, cardView.getPreventCornerOverlap()));
        cardView.setShadowPadding(hPadding, vPadding, hPadding, vPadding);
    }

    @Override // android.support.v7.widget.CardViewImpl
    public final void setBackgroundColor(CardViewDelegate cardView, int color) {
        RoundRectDrawable cardBackground = getCardBackground(cardView);
        cardBackground.mPaint.setColor(color);
        cardBackground.invalidateSelf();
    }

    private static RoundRectDrawable getCardBackground(CardViewDelegate cardView) {
        return (RoundRectDrawable) cardView.getCardBackground();
    }

    @Override // android.support.v7.widget.CardViewImpl
    public final float getMinWidth(CardViewDelegate cardView) {
        return getCardBackground(cardView).mRadius * 2.0f;
    }

    @Override // android.support.v7.widget.CardViewImpl
    public final float getMinHeight(CardViewDelegate cardView) {
        return getCardBackground(cardView).mRadius * 2.0f;
    }

    @Override // android.support.v7.widget.CardViewImpl
    public final void onCompatPaddingChanged(CardViewDelegate cardView) {
        setMaxElevation(cardView, getCardBackground(cardView).mPadding);
    }

    @Override // android.support.v7.widget.CardViewImpl
    public final void onPreventCornerOverlapChanged(CardViewDelegate cardView) {
        setMaxElevation(cardView, getCardBackground(cardView).mPadding);
    }
}
