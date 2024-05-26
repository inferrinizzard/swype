package android.support.v4.view;

import android.os.Build;
import android.support.v4.view.LayoutInflaterCompatBase;
import android.support.v4.view.LayoutInflaterCompatHC;
import android.view.LayoutInflater;

/* loaded from: classes.dex */
public final class LayoutInflaterCompat {
    static final LayoutInflaterCompatImpl IMPL;

    /* loaded from: classes.dex */
    interface LayoutInflaterCompatImpl {
        void setFactory(LayoutInflater layoutInflater, LayoutInflaterFactory layoutInflaterFactory);
    }

    /* loaded from: classes.dex */
    static class LayoutInflaterCompatImplBase implements LayoutInflaterCompatImpl {
        LayoutInflaterCompatImplBase() {
        }

        @Override // android.support.v4.view.LayoutInflaterCompat.LayoutInflaterCompatImpl
        public void setFactory(LayoutInflater layoutInflater, LayoutInflaterFactory factory) {
            layoutInflater.setFactory(factory != null ? new LayoutInflaterCompatBase.FactoryWrapper(factory) : null);
        }
    }

    /* loaded from: classes.dex */
    static class LayoutInflaterCompatImplV11 extends LayoutInflaterCompatImplBase {
        LayoutInflaterCompatImplV11() {
        }

        @Override // android.support.v4.view.LayoutInflaterCompat.LayoutInflaterCompatImplBase, android.support.v4.view.LayoutInflaterCompat.LayoutInflaterCompatImpl
        public void setFactory(LayoutInflater layoutInflater, LayoutInflaterFactory factory) {
            LayoutInflaterCompatHC.FactoryWrapperHC factoryWrapperHC = factory != null ? new LayoutInflaterCompatHC.FactoryWrapperHC(factory) : null;
            layoutInflater.setFactory2(factoryWrapperHC);
            LayoutInflater.Factory factory2 = layoutInflater.getFactory();
            if (factory2 instanceof LayoutInflater.Factory2) {
                LayoutInflaterCompatHC.forceSetFactory2(layoutInflater, (LayoutInflater.Factory2) factory2);
            } else {
                LayoutInflaterCompatHC.forceSetFactory2(layoutInflater, factoryWrapperHC);
            }
        }
    }

    /* loaded from: classes.dex */
    static class LayoutInflaterCompatImplV21 extends LayoutInflaterCompatImplV11 {
        LayoutInflaterCompatImplV21() {
        }

        @Override // android.support.v4.view.LayoutInflaterCompat.LayoutInflaterCompatImplV11, android.support.v4.view.LayoutInflaterCompat.LayoutInflaterCompatImplBase, android.support.v4.view.LayoutInflaterCompat.LayoutInflaterCompatImpl
        public final void setFactory(LayoutInflater layoutInflater, LayoutInflaterFactory factory) {
            layoutInflater.setFactory2(factory != null ? new LayoutInflaterCompatHC.FactoryWrapperHC(factory) : null);
        }
    }

    static {
        int version = Build.VERSION.SDK_INT;
        if (version >= 21) {
            IMPL = new LayoutInflaterCompatImplV21();
        } else if (version >= 11) {
            IMPL = new LayoutInflaterCompatImplV11();
        } else {
            IMPL = new LayoutInflaterCompatImplBase();
        }
    }

    public static void setFactory(LayoutInflater inflater, LayoutInflaterFactory factory) {
        IMPL.setFactory(inflater, factory);
    }
}
