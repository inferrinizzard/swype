package com.nuance.swype.input;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.ShapeDrawable;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class ShadowEffect {
    protected static final LogManager.Log log = LogManager.getLog("ShadowEffect");
    private final ShadowProps props;

    public ShadowEffect(ShadowProps props) {
        this.props = props;
    }

    public ShadowEffect() {
        this(new ShadowProps());
    }

    public void set(Paint paint) {
        if (this.props.isEnabled()) {
            paint.setShadowLayer(this.props.getRadius(), this.props.getOffsetX(), this.props.getOffsetY(), this.props.getColor());
        }
    }

    public void reset(Paint paint) {
        if (this.props.isEnabled()) {
            paint.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
        }
    }

    public static boolean canUseShadowLayer(Drawable drawable) {
        return getPaint(drawable) != null;
    }

    private static Paint getPaint(Drawable drawable) {
        if (drawable instanceof NinePatchDrawable) {
            log.w("getPaint(): shadow layer not so suitable for NinePatchDrawable");
            Paint paint = ((NinePatchDrawable) drawable).getPaint();
            return paint;
        }
        if (drawable instanceof BitmapDrawable) {
            Paint paint2 = ((BitmapDrawable) drawable).getPaint();
            return paint2;
        }
        if (!(drawable instanceof ShapeDrawable)) {
            return null;
        }
        Paint paint3 = ((ShapeDrawable) drawable).getPaint();
        return paint3;
    }

    public void draw(Canvas canvas, Drawable drawable) {
        if (!this.props.isEnabled()) {
            drawable.draw(canvas);
            return;
        }
        Paint paint = getPaint(drawable);
        if (paint == null) {
            log.w("draw(): drawable does not provide paint (only some types allow access to Paint)");
        }
        set(paint);
        drawable.draw(canvas);
        reset(paint);
    }
}
