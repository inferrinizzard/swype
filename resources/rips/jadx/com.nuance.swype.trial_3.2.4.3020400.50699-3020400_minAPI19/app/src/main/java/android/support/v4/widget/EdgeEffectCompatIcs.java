package android.support.v4.widget;

import android.widget.EdgeEffect;

/* loaded from: classes.dex */
final class EdgeEffectCompatIcs {
    public static boolean onPull(Object edgeEffect, float deltaDistance) {
        ((EdgeEffect) edgeEffect).onPull(deltaDistance);
        return true;
    }
}
