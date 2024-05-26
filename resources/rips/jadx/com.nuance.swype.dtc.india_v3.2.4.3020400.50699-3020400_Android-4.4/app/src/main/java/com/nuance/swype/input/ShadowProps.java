package com.nuance.swype.input;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.nuance.swype.plugin.ThemeLoader;
import com.nuance.swype.plugin.TypedArrayWrapper;

/* loaded from: classes.dex */
public class ShadowProps {
    private static final float MAX_RADIUS = 25.0f;
    private int color;
    private boolean isHorFlipped;
    private float radius;
    private float scale = 1.0f;
    private float xOffset;
    private float yOffset;

    public void flipHor(boolean flip) {
        this.isHorFlipped = flip;
    }

    public float getOffsetX() {
        return this.isHorFlipped ? -this.xOffset : this.xOffset;
    }

    public float getOffsetY() {
        return this.yOffset;
    }

    public float getRadius() {
        return this.radius;
    }

    public int getColor() {
        return this.color;
    }

    public float getScale() {
        return this.scale;
    }

    public float getForegroundInsetLeft() {
        float offset = getOffsetX();
        if (offset >= 0.0f) {
            return 0.0f;
        }
        return -offset;
    }

    public float getForegroundInsetRight() {
        float offset = getOffsetX();
        if (offset <= 0.0f) {
            return 0.0f;
        }
        return offset;
    }

    public float getForegroundInsetTop() {
        float offset = getOffsetY();
        if (offset >= 0.0f) {
            return 0.0f;
        }
        return -offset;
    }

    public float getForegroundInsetBottom() {
        float offset = getOffsetY();
        if (offset <= 0.0f) {
            return 0.0f;
        }
        return offset;
    }

    public ShadowProps() {
    }

    public ShadowProps(Context context, AttributeSet attrs, int styleName, int styleRes) {
        TypedArrayWrapper props = ThemeLoader.getInstance().obtainStyledAttributes$6d3b0587(context, null, R.styleable.Shadow, 0, R.style.ShadowStyle, R.xml.defaults, "ShadowStyle");
        setProps(props);
        props.recycle();
    }

    public ShadowProps(Context context, TypedArrayWrapper array, int attrName) {
        if (array.getResourceId(attrName, -1) != -1) {
            TypedArrayWrapper props = ThemeLoader.getInstance().obtainStyledAttributes$6d3b0587(context, null, R.styleable.Shadow, 0, R.style.ShadowStyle, R.xml.defaults, "ShadowStyle");
            setProps(props);
            props.recycle();
        }
    }

    public ShadowProps(Context context, TypedArray array) {
        TypedArrayWrapper props = ThemeLoader.getInstance().obtainStyledAttributes$6d3b0587(context, null, R.styleable.Shadow, 0, R.style.ShadowStyle, R.xml.defaults, "ShadowStyle");
        setProps(props);
        props.recycle();
    }

    private void setProps(TypedArrayWrapper attrs) {
        this.color = attrs.getColor(R.styleable.Shadow_shadowColor, 0);
        this.radius = Math.min(attrs.getDimension$255e742(R.styleable.Shadow_shadowRadius), MAX_RADIUS);
        this.xOffset = attrs.getDimension$255e742(R.styleable.Shadow_shadowDx);
        this.yOffset = attrs.getDimension$255e742(R.styleable.Shadow_shadowDy);
        this.scale = attrs.getFloat(R.styleable.Shadow_shadowScale, 1.0f);
    }

    public boolean isEnabled() {
        return this.color != 0;
    }
}
