package android.support.v4.view;

import android.os.Build;
import android.view.Gravity;

/* loaded from: classes.dex */
public final class GravityCompat {
    static final GravityCompatImpl IMPL;

    /* loaded from: classes.dex */
    interface GravityCompatImpl {
        int getAbsoluteGravity(int i, int i2);
    }

    /* loaded from: classes.dex */
    static class GravityCompatImplBase implements GravityCompatImpl {
        GravityCompatImplBase() {
        }

        @Override // android.support.v4.view.GravityCompat.GravityCompatImpl
        public final int getAbsoluteGravity(int gravity, int layoutDirection) {
            return (-8388609) & gravity;
        }
    }

    /* loaded from: classes.dex */
    static class GravityCompatImplJellybeanMr1 implements GravityCompatImpl {
        GravityCompatImplJellybeanMr1() {
        }

        @Override // android.support.v4.view.GravityCompat.GravityCompatImpl
        public final int getAbsoluteGravity(int gravity, int layoutDirection) {
            return Gravity.getAbsoluteGravity(gravity, layoutDirection);
        }
    }

    static {
        if (Build.VERSION.SDK_INT >= 17) {
            IMPL = new GravityCompatImplJellybeanMr1();
        } else {
            IMPL = new GravityCompatImplBase();
        }
    }

    public static int getAbsoluteGravity(int gravity, int layoutDirection) {
        return IMPL.getAbsoluteGravity(gravity, layoutDirection);
    }
}
