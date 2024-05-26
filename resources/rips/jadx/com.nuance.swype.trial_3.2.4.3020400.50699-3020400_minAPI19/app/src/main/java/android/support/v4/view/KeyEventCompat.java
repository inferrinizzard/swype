package android.support.v4.view;

import android.os.Build;
import android.view.KeyEvent;
import com.nuance.swype.input.R;

/* loaded from: classes.dex */
public final class KeyEventCompat {
    static final KeyEventVersionImpl IMPL;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface KeyEventVersionImpl {
        boolean metaStateHasModifiers$255f299(int i);

        boolean metaStateHasNoModifiers(int i);

        void startTracking(KeyEvent keyEvent);
    }

    /* loaded from: classes.dex */
    static class BaseKeyEventVersionImpl implements KeyEventVersionImpl {
        BaseKeyEventVersionImpl() {
        }

        private static int metaStateFilterDirectionalModifiers(int metaState, int modifiers, int basic, int left, int right) {
            boolean wantBasic = (basic & 1) != 0;
            int directional = left | right;
            boolean wantLeftOrRight = (directional & 1) != 0;
            if (wantBasic) {
                if (wantLeftOrRight) {
                    throw new IllegalArgumentException("bad arguments");
                }
                return metaState & (directional ^ (-1));
            }
            if (wantLeftOrRight) {
                return metaState & (basic ^ (-1));
            }
            return metaState;
        }

        public int normalizeMetaState(int metaState) {
            if ((metaState & R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop) != 0) {
                metaState |= 1;
            }
            if ((metaState & 48) != 0) {
                metaState |= 2;
            }
            return metaState & R.styleable.ThemeTemplate_chinesePeriodKeypad;
        }

        @Override // android.support.v4.view.KeyEventCompat.KeyEventVersionImpl
        public boolean metaStateHasModifiers$255f299(int metaState) {
            return metaStateFilterDirectionalModifiers(metaStateFilterDirectionalModifiers(normalizeMetaState(metaState) & R.styleable.ThemeTemplate_chinesePeriodKeypad, 1, 1, 64, 128), 1, 2, 16, 32) == 1;
        }

        @Override // android.support.v4.view.KeyEventCompat.KeyEventVersionImpl
        public boolean metaStateHasNoModifiers(int metaState) {
            return (normalizeMetaState(metaState) & R.styleable.ThemeTemplate_chinesePeriodKeypad) == 0;
        }

        @Override // android.support.v4.view.KeyEventCompat.KeyEventVersionImpl
        public void startTracking(KeyEvent event) {
        }
    }

    /* loaded from: classes.dex */
    static class EclairKeyEventVersionImpl extends BaseKeyEventVersionImpl {
        EclairKeyEventVersionImpl() {
        }

        @Override // android.support.v4.view.KeyEventCompat.BaseKeyEventVersionImpl, android.support.v4.view.KeyEventCompat.KeyEventVersionImpl
        public final void startTracking(KeyEvent event) {
            event.startTracking();
        }
    }

    /* loaded from: classes.dex */
    static class HoneycombKeyEventVersionImpl extends EclairKeyEventVersionImpl {
        HoneycombKeyEventVersionImpl() {
        }

        @Override // android.support.v4.view.KeyEventCompat.BaseKeyEventVersionImpl
        public final int normalizeMetaState(int metaState) {
            return KeyEvent.normalizeMetaState(metaState);
        }

        @Override // android.support.v4.view.KeyEventCompat.BaseKeyEventVersionImpl, android.support.v4.view.KeyEventCompat.KeyEventVersionImpl
        public final boolean metaStateHasModifiers$255f299(int metaState) {
            return KeyEvent.metaStateHasModifiers(metaState, 1);
        }

        @Override // android.support.v4.view.KeyEventCompat.BaseKeyEventVersionImpl, android.support.v4.view.KeyEventCompat.KeyEventVersionImpl
        public final boolean metaStateHasNoModifiers(int metaState) {
            return KeyEvent.metaStateHasNoModifiers(metaState);
        }
    }

    static {
        if (Build.VERSION.SDK_INT >= 11) {
            IMPL = new HoneycombKeyEventVersionImpl();
        } else {
            IMPL = new BaseKeyEventVersionImpl();
        }
    }

    public static boolean hasNoModifiers(KeyEvent event) {
        return IMPL.metaStateHasNoModifiers(event.getMetaState());
    }

    public static void startTracking(KeyEvent event) {
        IMPL.startTracking(event);
    }
}
