package com.facebook.share.internal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.facebook.R;

/* loaded from: classes.dex */
public class LikeBoxCountView extends FrameLayout {
    private int additionalTextPadding;
    private Paint borderPaint;
    private float borderRadius;
    private float caretHeight;
    private LikeBoxCountViewCaretPosition caretPosition;
    private float caretWidth;
    private TextView likeCountLabel;
    private int textPadding;

    /* loaded from: classes.dex */
    public enum LikeBoxCountViewCaretPosition {
        LEFT,
        TOP,
        RIGHT,
        BOTTOM
    }

    public LikeBoxCountView(Context context) {
        super(context);
        this.caretPosition = LikeBoxCountViewCaretPosition.LEFT;
        initialize(context);
    }

    public void setText(String text) {
        this.likeCountLabel.setText(text);
    }

    public void setCaretPosition(LikeBoxCountViewCaretPosition caretPosition) {
        this.caretPosition = caretPosition;
        switch (caretPosition) {
            case LEFT:
                setAdditionalTextPadding(this.additionalTextPadding, 0, 0, 0);
                return;
            case TOP:
                setAdditionalTextPadding(0, this.additionalTextPadding, 0, 0);
                return;
            case RIGHT:
                setAdditionalTextPadding(0, 0, this.additionalTextPadding, 0);
                return;
            case BOTTOM:
                setAdditionalTextPadding(0, 0, 0, this.additionalTextPadding);
                return;
            default:
                return;
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int top = getPaddingTop();
        int left = getPaddingLeft();
        int right = getWidth() - getPaddingRight();
        int bottom = getHeight() - getPaddingBottom();
        switch (this.caretPosition) {
            case LEFT:
                left = (int) (left + this.caretHeight);
                break;
            case TOP:
                top = (int) (top + this.caretHeight);
                break;
            case RIGHT:
                right = (int) (right - this.caretHeight);
                break;
            case BOTTOM:
                bottom = (int) (bottom - this.caretHeight);
                break;
        }
        drawBorder(canvas, left, top, right, bottom);
    }

    private void initialize(Context context) {
        setWillNotDraw(false);
        this.caretHeight = getResources().getDimension(R.dimen.com_facebook_likeboxcountview_caret_height);
        this.caretWidth = getResources().getDimension(R.dimen.com_facebook_likeboxcountview_caret_width);
        this.borderRadius = getResources().getDimension(R.dimen.com_facebook_likeboxcountview_border_radius);
        this.borderPaint = new Paint();
        this.borderPaint.setColor(getResources().getColor(R.color.com_facebook_likeboxcountview_border_color));
        this.borderPaint.setStrokeWidth(getResources().getDimension(R.dimen.com_facebook_likeboxcountview_border_width));
        this.borderPaint.setStyle(Paint.Style.STROKE);
        initializeLikeCountLabel(context);
        addView(this.likeCountLabel);
        setCaretPosition(this.caretPosition);
    }

    private void initializeLikeCountLabel(Context context) {
        this.likeCountLabel = new TextView(context);
        FrameLayout.LayoutParams likeCountLabelLayoutParams = new FrameLayout.LayoutParams(-1, -1);
        this.likeCountLabel.setLayoutParams(likeCountLabelLayoutParams);
        this.likeCountLabel.setGravity(17);
        this.likeCountLabel.setTextSize(0, getResources().getDimension(R.dimen.com_facebook_likeboxcountview_text_size));
        this.likeCountLabel.setTextColor(getResources().getColor(R.color.com_facebook_likeboxcountview_text_color));
        this.textPadding = getResources().getDimensionPixelSize(R.dimen.com_facebook_likeboxcountview_text_padding);
        this.additionalTextPadding = getResources().getDimensionPixelSize(R.dimen.com_facebook_likeboxcountview_caret_height);
    }

    private void setAdditionalTextPadding(int left, int top, int right, int bottom) {
        this.likeCountLabel.setPadding(this.textPadding + left, this.textPadding + top, this.textPadding + right, this.textPadding + bottom);
    }

    private void drawBorder(Canvas canvas, float left, float top, float right, float bottom) {
        Path borderPath = new Path();
        float ovalSize = 2.0f * this.borderRadius;
        borderPath.addArc(new RectF(left, top, left + ovalSize, top + ovalSize), -180.0f, 90.0f);
        if (this.caretPosition == LikeBoxCountViewCaretPosition.TOP) {
            borderPath.lineTo((((right - left) - this.caretWidth) / 2.0f) + left, top);
            borderPath.lineTo(((right - left) / 2.0f) + left, top - this.caretHeight);
            borderPath.lineTo((((right - left) + this.caretWidth) / 2.0f) + left, top);
        }
        borderPath.lineTo(right - this.borderRadius, top);
        borderPath.addArc(new RectF(right - ovalSize, top, right, top + ovalSize), -90.0f, 90.0f);
        if (this.caretPosition == LikeBoxCountViewCaretPosition.RIGHT) {
            borderPath.lineTo(right, (((bottom - top) - this.caretWidth) / 2.0f) + top);
            borderPath.lineTo(this.caretHeight + right, ((bottom - top) / 2.0f) + top);
            borderPath.lineTo(right, (((bottom - top) + this.caretWidth) / 2.0f) + top);
        }
        borderPath.lineTo(right, bottom - this.borderRadius);
        borderPath.addArc(new RectF(right - ovalSize, bottom - ovalSize, right, bottom), 0.0f, 90.0f);
        if (this.caretPosition == LikeBoxCountViewCaretPosition.BOTTOM) {
            borderPath.lineTo((((right - left) + this.caretWidth) / 2.0f) + left, bottom);
            borderPath.lineTo(((right - left) / 2.0f) + left, this.caretHeight + bottom);
            borderPath.lineTo((((right - left) - this.caretWidth) / 2.0f) + left, bottom);
        }
        borderPath.lineTo(this.borderRadius + left, bottom);
        borderPath.addArc(new RectF(left, bottom - ovalSize, left + ovalSize, bottom), 90.0f, 90.0f);
        if (this.caretPosition == LikeBoxCountViewCaretPosition.LEFT) {
            borderPath.lineTo(left, (((bottom - top) + this.caretWidth) / 2.0f) + top);
            borderPath.lineTo(left - this.caretHeight, ((bottom - top) / 2.0f) + top);
            borderPath.lineTo(left, (((bottom - top) - this.caretWidth) / 2.0f) + top);
        }
        borderPath.lineTo(left, this.borderRadius + top);
        canvas.drawPath(borderPath, this.borderPaint);
    }
}
