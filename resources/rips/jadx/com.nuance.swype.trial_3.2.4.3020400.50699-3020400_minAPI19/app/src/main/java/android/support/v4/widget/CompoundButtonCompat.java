package android.support.v4.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.CompoundButton;

/* loaded from: classes.dex */
public final class CompoundButtonCompat {
    private static final CompoundButtonCompatImpl IMPL;

    /* loaded from: classes.dex */
    interface CompoundButtonCompatImpl {
        Drawable getButtonDrawable(CompoundButton compoundButton);

        void setButtonTintList(CompoundButton compoundButton, ColorStateList colorStateList);

        void setButtonTintMode(CompoundButton compoundButton, PorterDuff.Mode mode);
    }

    static {
        int sdk = Build.VERSION.SDK_INT;
        if (sdk >= 23) {
            IMPL = new Api23CompoundButtonImpl();
        } else if (sdk >= 21) {
            IMPL = new LollipopCompoundButtonImpl();
        } else {
            IMPL = new BaseCompoundButtonCompat();
        }
    }

    /* loaded from: classes.dex */
    static class BaseCompoundButtonCompat implements CompoundButtonCompatImpl {
        BaseCompoundButtonCompat() {
        }

        @Override // android.support.v4.widget.CompoundButtonCompat.CompoundButtonCompatImpl
        public Drawable getButtonDrawable(CompoundButton button) {
            return CompoundButtonCompatDonut.getButtonDrawable(button);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.support.v4.widget.CompoundButtonCompat.CompoundButtonCompatImpl
        public void setButtonTintList(CompoundButton compoundButton, ColorStateList tint) {
            if (!(compoundButton instanceof TintableCompoundButton)) {
                return;
            }
            ((TintableCompoundButton) compoundButton).setSupportButtonTintList(tint);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.support.v4.widget.CompoundButtonCompat.CompoundButtonCompatImpl
        public void setButtonTintMode(CompoundButton compoundButton, PorterDuff.Mode tintMode) {
            if (!(compoundButton instanceof TintableCompoundButton)) {
                return;
            }
            ((TintableCompoundButton) compoundButton).setSupportButtonTintMode(tintMode);
        }
    }

    /* loaded from: classes.dex */
    static class LollipopCompoundButtonImpl extends BaseCompoundButtonCompat {
        LollipopCompoundButtonImpl() {
        }

        @Override // android.support.v4.widget.CompoundButtonCompat.BaseCompoundButtonCompat, android.support.v4.widget.CompoundButtonCompat.CompoundButtonCompatImpl
        public final void setButtonTintList(CompoundButton button, ColorStateList tint) {
            button.setButtonTintList(tint);
        }

        @Override // android.support.v4.widget.CompoundButtonCompat.BaseCompoundButtonCompat, android.support.v4.widget.CompoundButtonCompat.CompoundButtonCompatImpl
        public final void setButtonTintMode(CompoundButton button, PorterDuff.Mode tintMode) {
            button.setButtonTintMode(tintMode);
        }
    }

    /* loaded from: classes.dex */
    static class Api23CompoundButtonImpl extends LollipopCompoundButtonImpl {
        Api23CompoundButtonImpl() {
        }

        @Override // android.support.v4.widget.CompoundButtonCompat.BaseCompoundButtonCompat, android.support.v4.widget.CompoundButtonCompat.CompoundButtonCompatImpl
        public final Drawable getButtonDrawable(CompoundButton button) {
            return button.getButtonDrawable();
        }
    }

    public static void setButtonTintList(CompoundButton button, ColorStateList tint) {
        IMPL.setButtonTintList(button, tint);
    }

    public static void setButtonTintMode(CompoundButton button, PorterDuff.Mode tintMode) {
        IMPL.setButtonTintMode(button, tintMode);
    }

    public static Drawable getButtonDrawable(CompoundButton button) {
        return IMPL.getButtonDrawable(button);
    }
}
