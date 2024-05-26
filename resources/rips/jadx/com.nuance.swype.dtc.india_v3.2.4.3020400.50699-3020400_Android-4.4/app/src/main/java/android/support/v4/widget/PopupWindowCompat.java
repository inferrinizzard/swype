package android.support.v4.widget;

import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.PopupWindow;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public final class PopupWindowCompat {
    static final PopupWindowImpl IMPL;

    /* loaded from: classes.dex */
    interface PopupWindowImpl {
        void setOverlapAnchor(PopupWindow popupWindow, boolean z);

        void setWindowLayoutType(PopupWindow popupWindow, int i);

        void showAsDropDown(PopupWindow popupWindow, View view, int i, int i2, int i3);
    }

    /* loaded from: classes.dex */
    static class BasePopupWindowImpl implements PopupWindowImpl {
        BasePopupWindowImpl() {
        }

        @Override // android.support.v4.widget.PopupWindowCompat.PopupWindowImpl
        public void showAsDropDown(PopupWindow popup, View anchor, int xoff, int yoff, int gravity) {
            if ((GravityCompat.getAbsoluteGravity(gravity, ViewCompat.getLayoutDirection(anchor)) & 7) == 5) {
                xoff -= popup.getWidth() - anchor.getWidth();
            }
            popup.showAsDropDown(anchor, xoff, yoff);
        }

        @Override // android.support.v4.widget.PopupWindowCompat.PopupWindowImpl
        public void setOverlapAnchor(PopupWindow popupWindow, boolean overlapAnchor) {
        }

        @Override // android.support.v4.widget.PopupWindowCompat.PopupWindowImpl
        public void setWindowLayoutType(PopupWindow popupWindow, int layoutType) {
        }
    }

    /* loaded from: classes.dex */
    static class GingerbreadPopupWindowImpl extends BasePopupWindowImpl {
        GingerbreadPopupWindowImpl() {
        }

        @Override // android.support.v4.widget.PopupWindowCompat.BasePopupWindowImpl, android.support.v4.widget.PopupWindowCompat.PopupWindowImpl
        public void setWindowLayoutType(PopupWindow popupWindow, int layoutType) {
            if (!PopupWindowCompatGingerbread.sSetWindowLayoutTypeMethodAttempted) {
                try {
                    Method declaredMethod = PopupWindow.class.getDeclaredMethod("setWindowLayoutType", Integer.TYPE);
                    PopupWindowCompatGingerbread.sSetWindowLayoutTypeMethod = declaredMethod;
                    declaredMethod.setAccessible(true);
                } catch (Exception e) {
                }
                PopupWindowCompatGingerbread.sSetWindowLayoutTypeMethodAttempted = true;
            }
            if (PopupWindowCompatGingerbread.sSetWindowLayoutTypeMethod != null) {
                try {
                    PopupWindowCompatGingerbread.sSetWindowLayoutTypeMethod.invoke(popupWindow, Integer.valueOf(layoutType));
                } catch (Exception e2) {
                }
            }
        }
    }

    /* loaded from: classes.dex */
    static class KitKatPopupWindowImpl extends GingerbreadPopupWindowImpl {
        KitKatPopupWindowImpl() {
        }

        @Override // android.support.v4.widget.PopupWindowCompat.BasePopupWindowImpl, android.support.v4.widget.PopupWindowCompat.PopupWindowImpl
        public final void showAsDropDown(PopupWindow popup, View anchor, int xoff, int yoff, int gravity) {
            popup.showAsDropDown(anchor, xoff, yoff, gravity);
        }
    }

    /* loaded from: classes.dex */
    static class Api21PopupWindowImpl extends KitKatPopupWindowImpl {
        Api21PopupWindowImpl() {
        }

        @Override // android.support.v4.widget.PopupWindowCompat.BasePopupWindowImpl, android.support.v4.widget.PopupWindowCompat.PopupWindowImpl
        public void setOverlapAnchor(PopupWindow popupWindow, boolean overlapAnchor) {
            PopupWindowCompatApi21.setOverlapAnchor(popupWindow, overlapAnchor);
        }
    }

    /* loaded from: classes.dex */
    static class Api23PopupWindowImpl extends Api21PopupWindowImpl {
        Api23PopupWindowImpl() {
        }

        @Override // android.support.v4.widget.PopupWindowCompat.Api21PopupWindowImpl, android.support.v4.widget.PopupWindowCompat.BasePopupWindowImpl, android.support.v4.widget.PopupWindowCompat.PopupWindowImpl
        public final void setOverlapAnchor(PopupWindow popupWindow, boolean overlapAnchor) {
            popupWindow.setOverlapAnchor(overlapAnchor);
        }

        @Override // android.support.v4.widget.PopupWindowCompat.GingerbreadPopupWindowImpl, android.support.v4.widget.PopupWindowCompat.BasePopupWindowImpl, android.support.v4.widget.PopupWindowCompat.PopupWindowImpl
        public final void setWindowLayoutType(PopupWindow popupWindow, int layoutType) {
            popupWindow.setWindowLayoutType(layoutType);
        }
    }

    static {
        int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            IMPL = new Api23PopupWindowImpl();
            return;
        }
        if (version >= 21) {
            IMPL = new Api21PopupWindowImpl();
            return;
        }
        if (version >= 19) {
            IMPL = new KitKatPopupWindowImpl();
        } else if (version >= 9) {
            IMPL = new GingerbreadPopupWindowImpl();
        } else {
            IMPL = new BasePopupWindowImpl();
        }
    }

    public static void showAsDropDown(PopupWindow popup, View anchor, int xoff, int yoff, int gravity) {
        IMPL.showAsDropDown(popup, anchor, xoff, yoff, gravity);
    }

    public static void setOverlapAnchor(PopupWindow popupWindow, boolean overlapAnchor) {
        IMPL.setOverlapAnchor(popupWindow, overlapAnchor);
    }

    public static void setWindowLayoutType(PopupWindow popupWindow, int layoutType) {
        IMPL.setWindowLayoutType(popupWindow, layoutType);
    }
}
