package android.support.v7.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.RoundRectDrawableWithShadow;

/* loaded from: classes.dex */
class CardViewEclairMr1 implements CardViewImpl {
    final RectF sCornerRect = new RectF();

    @Override // android.support.v7.widget.CardViewImpl
    public void initStatic() {
        RoundRectDrawableWithShadow.sRoundRectHelper = new RoundRectDrawableWithShadow.RoundRectHelper() { // from class: android.support.v7.widget.CardViewEclairMr1.1
            @Override // android.support.v7.widget.RoundRectDrawableWithShadow.RoundRectHelper
            public final void drawRoundRect(Canvas canvas, RectF bounds, float cornerRadius, Paint paint) {
                float twoRadius = cornerRadius * 2.0f;
                float innerWidth = (bounds.width() - twoRadius) - 1.0f;
                float innerHeight = (bounds.height() - twoRadius) - 1.0f;
                if (cornerRadius >= 1.0f) {
                    float roundedCornerRadius = cornerRadius + 0.5f;
                    CardViewEclairMr1.this.sCornerRect.set(-roundedCornerRadius, -roundedCornerRadius, roundedCornerRadius, roundedCornerRadius);
                    int saved = canvas.save();
                    canvas.translate(bounds.left + roundedCornerRadius, bounds.top + roundedCornerRadius);
                    canvas.drawArc(CardViewEclairMr1.this.sCornerRect, 180.0f, 90.0f, true, paint);
                    canvas.translate(innerWidth, 0.0f);
                    canvas.rotate(90.0f);
                    canvas.drawArc(CardViewEclairMr1.this.sCornerRect, 180.0f, 90.0f, true, paint);
                    canvas.translate(innerHeight, 0.0f);
                    canvas.rotate(90.0f);
                    canvas.drawArc(CardViewEclairMr1.this.sCornerRect, 180.0f, 90.0f, true, paint);
                    canvas.translate(innerWidth, 0.0f);
                    canvas.rotate(90.0f);
                    canvas.drawArc(CardViewEclairMr1.this.sCornerRect, 180.0f, 90.0f, true, paint);
                    canvas.restoreToCount(saved);
                    canvas.drawRect((bounds.left + roundedCornerRadius) - 1.0f, bounds.top, 1.0f + (bounds.right - roundedCornerRadius), bounds.top + roundedCornerRadius, paint);
                    canvas.drawRect((bounds.left + roundedCornerRadius) - 1.0f, 1.0f + (bounds.bottom - roundedCornerRadius), 1.0f + (bounds.right - roundedCornerRadius), bounds.bottom, paint);
                }
                canvas.drawRect(bounds.left, Math.max(0.0f, cornerRadius - 1.0f) + bounds.top, bounds.right, 1.0f + (bounds.bottom - cornerRadius), paint);
            }
        };
    }

    @Override // android.support.v7.widget.CardViewImpl
    public final void updatePadding(CardViewDelegate cardView) {
        Rect shadowPadding = new Rect();
        getShadowBackground(cardView).getPadding(shadowPadding);
        cardView.setMinWidthHeightInternal((int) Math.ceil(getMinWidth(cardView)), (int) Math.ceil(getMinHeight(cardView)));
        cardView.setShadowPadding(shadowPadding.left, shadowPadding.top, shadowPadding.right, shadowPadding.bottom);
    }

    @Override // android.support.v7.widget.CardViewImpl
    public final void onCompatPaddingChanged(CardViewDelegate cardView) {
    }

    @Override // android.support.v7.widget.CardViewImpl
    public final void onPreventCornerOverlapChanged(CardViewDelegate cardView) {
        getShadowBackground(cardView).setAddPaddingForCorners(cardView.getPreventCornerOverlap());
        updatePadding(cardView);
    }

    @Override // android.support.v7.widget.CardViewImpl
    public final void setBackgroundColor(CardViewDelegate cardView, int color) {
        RoundRectDrawableWithShadow shadowBackground = getShadowBackground(cardView);
        shadowBackground.mPaint.setColor(color);
        shadowBackground.invalidateSelf();
    }

    @Override // android.support.v7.widget.CardViewImpl
    public final void setRadius(CardViewDelegate cardView, float radius) {
        RoundRectDrawableWithShadow shadowBackground = getShadowBackground(cardView);
        if (radius < 0.0f) {
            throw new IllegalArgumentException("Invalid radius " + radius + ". Must be >= 0");
        }
        float f = (int) (0.5f + radius);
        if (shadowBackground.mCornerRadius != f) {
            shadowBackground.mCornerRadius = f;
            shadowBackground.mDirty = true;
            shadowBackground.invalidateSelf();
        }
        updatePadding(cardView);
    }

    @Override // android.support.v7.widget.CardViewImpl
    public final float getRadius(CardViewDelegate cardView) {
        return getShadowBackground(cardView).mCornerRadius;
    }

    @Override // android.support.v7.widget.CardViewImpl
    public final void setElevation(CardViewDelegate cardView, float elevation) {
        RoundRectDrawableWithShadow shadowBackground = getShadowBackground(cardView);
        shadowBackground.setShadowSize(elevation, shadowBackground.mRawMaxShadowSize);
    }

    @Override // android.support.v7.widget.CardViewImpl
    public final float getElevation(CardViewDelegate cardView) {
        return getShadowBackground(cardView).mRawShadowSize;
    }

    @Override // android.support.v7.widget.CardViewImpl
    public final void setMaxElevation(CardViewDelegate cardView, float maxElevation) {
        RoundRectDrawableWithShadow shadowBackground = getShadowBackground(cardView);
        shadowBackground.setShadowSize(shadowBackground.mRawShadowSize, maxElevation);
        updatePadding(cardView);
    }

    @Override // android.support.v7.widget.CardViewImpl
    public final float getMaxElevation(CardViewDelegate cardView) {
        return getShadowBackground(cardView).mRawMaxShadowSize;
    }

    @Override // android.support.v7.widget.CardViewImpl
    public final float getMinWidth(CardViewDelegate cardView) {
        RoundRectDrawableWithShadow shadowBackground = getShadowBackground(cardView);
        return ((shadowBackground.mInsetShadow + shadowBackground.mRawMaxShadowSize) * 2.0f) + (Math.max(shadowBackground.mRawMaxShadowSize, shadowBackground.mCornerRadius + shadowBackground.mInsetShadow + (shadowBackground.mRawMaxShadowSize / 2.0f)) * 2.0f);
    }

    @Override // android.support.v7.widget.CardViewImpl
    public final float getMinHeight(CardViewDelegate cardView) {
        RoundRectDrawableWithShadow shadowBackground = getShadowBackground(cardView);
        return ((shadowBackground.mInsetShadow + (shadowBackground.mRawMaxShadowSize * 1.5f)) * 2.0f) + (Math.max(shadowBackground.mRawMaxShadowSize, shadowBackground.mCornerRadius + shadowBackground.mInsetShadow + ((shadowBackground.mRawMaxShadowSize * 1.5f) / 2.0f)) * 2.0f);
    }

    private static RoundRectDrawableWithShadow getShadowBackground(CardViewDelegate cardView) {
        return (RoundRectDrawableWithShadow) cardView.getCardBackground();
    }

    @Override // android.support.v7.widget.CardViewImpl
    public final void initialize(CardViewDelegate cardView, Context context, int backgroundColor, float radius, float elevation, float maxElevation) {
        RoundRectDrawableWithShadow background = new RoundRectDrawableWithShadow(context.getResources(), backgroundColor, radius, elevation, maxElevation);
        background.setAddPaddingForCorners(cardView.getPreventCornerOverlap());
        cardView.setCardBackground(background);
        updatePadding(cardView);
    }
}
