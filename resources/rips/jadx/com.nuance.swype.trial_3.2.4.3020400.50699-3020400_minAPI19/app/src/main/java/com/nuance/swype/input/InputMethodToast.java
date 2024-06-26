package com.nuance.swype.input;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/* loaded from: classes.dex */
public class InputMethodToast {
    private static Toast mToast;

    @SuppressLint({"InflateParams"})
    public static void show(Context context, CharSequence text, int duration) {
        Context toastContext = IMEApplication.from(context).getToastContext();
        if (mToast == null) {
            mToast = new Toast(toastContext);
        }
        View v = LayoutInflater.from(toastContext).inflate(R.layout.toast, (ViewGroup) null);
        ((TextView) v.findViewById(R.id.message)).setText(text);
        mToast.setView(v);
        mToast.setDuration(duration);
        mToast.setGravity(17, 0, 0);
        mToast.show();
    }

    public static void hide() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}
