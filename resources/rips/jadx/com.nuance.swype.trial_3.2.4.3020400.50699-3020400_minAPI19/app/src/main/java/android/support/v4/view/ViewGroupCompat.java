package android.support.v4.view;

import android.os.Build;
import android.view.ViewGroup;

/* loaded from: classes.dex */
public final class ViewGroupCompat {
    static final ViewGroupCompatImpl IMPL;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface ViewGroupCompatImpl {
        void setMotionEventSplittingEnabled$4d3af60(ViewGroup viewGroup);
    }

    /* loaded from: classes.dex */
    static class ViewGroupCompatStubImpl implements ViewGroupCompatImpl {
        ViewGroupCompatStubImpl() {
        }

        @Override // android.support.v4.view.ViewGroupCompat.ViewGroupCompatImpl
        public void setMotionEventSplittingEnabled$4d3af60(ViewGroup group) {
        }
    }

    /* loaded from: classes.dex */
    static class ViewGroupCompatHCImpl extends ViewGroupCompatStubImpl {
        ViewGroupCompatHCImpl() {
        }

        @Override // android.support.v4.view.ViewGroupCompat.ViewGroupCompatStubImpl, android.support.v4.view.ViewGroupCompat.ViewGroupCompatImpl
        public final void setMotionEventSplittingEnabled$4d3af60(ViewGroup group) {
            group.setMotionEventSplittingEnabled(false);
        }
    }

    /* loaded from: classes.dex */
    static class ViewGroupCompatIcsImpl extends ViewGroupCompatHCImpl {
        ViewGroupCompatIcsImpl() {
        }
    }

    /* loaded from: classes.dex */
    static class ViewGroupCompatJellybeanMR2Impl extends ViewGroupCompatIcsImpl {
        ViewGroupCompatJellybeanMR2Impl() {
        }
    }

    /* loaded from: classes.dex */
    static class ViewGroupCompatLollipopImpl extends ViewGroupCompatJellybeanMR2Impl {
        ViewGroupCompatLollipopImpl() {
        }
    }

    static {
        int version = Build.VERSION.SDK_INT;
        if (version >= 21) {
            IMPL = new ViewGroupCompatLollipopImpl();
            return;
        }
        if (version >= 18) {
            IMPL = new ViewGroupCompatJellybeanMR2Impl();
            return;
        }
        if (version >= 14) {
            IMPL = new ViewGroupCompatIcsImpl();
        } else if (version >= 11) {
            IMPL = new ViewGroupCompatHCImpl();
        } else {
            IMPL = new ViewGroupCompatStubImpl();
        }
    }
}
