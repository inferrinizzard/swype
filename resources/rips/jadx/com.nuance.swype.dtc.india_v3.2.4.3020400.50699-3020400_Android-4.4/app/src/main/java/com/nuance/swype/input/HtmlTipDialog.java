package com.nuance.swype.input;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class HtmlTipDialog extends SwypeDialog {
    private static final View.OnLongClickListener longClickConsumer = new View.OnLongClickListener() { // from class: com.nuance.swype.input.HtmlTipDialog.1
        @Override // android.view.View.OnLongClickListener
        public final boolean onLongClick(View v) {
            return true;
        }
    };
    private final DialogInterface.OnCancelListener onCancelListener;
    private final DialogInterface.OnDismissListener onDismissListener;
    private final String url;

    public HtmlTipDialog(String url, DialogInterface.OnCancelListener onCancelListner, DialogInterface.OnDismissListener onDismissListener) {
        this.url = url;
        this.onCancelListener = onCancelListner;
        this.onDismissListener = onDismissListener;
    }

    @Override // com.nuance.swype.input.SwypeDialog
    @SuppressLint({"SetJavaScriptEnabled"})
    protected Dialog onCreateDialog(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(1);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(this.onCancelListener);
        dialog.setOnDismissListener(this.onDismissListener);
        WebView webView = new WebView(context);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setOnLongClickListener(longClickConsumer);
        webView.setScrollBarStyle(0);
        webView.addJavascriptInterface(new JavaScriptBridge(dialog), "SwypeHost");
        webView.loadUrl(this.url);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-1, -2);
        dialog.setContentView(webView, params);
        return dialog;
    }

    /* loaded from: classes.dex */
    public static class JavaScriptBridge {
        private final WeakReference<Dialog> dialogRef;

        public JavaScriptBridge(Dialog dialog) {
            this.dialogRef = new WeakReference<>(dialog);
        }

        @JavascriptInterface
        public void cancel() {
            Dialog dialog = this.dialogRef.get();
            if (dialog != null) {
                dialog.cancel();
            }
        }

        @JavascriptInterface
        public void showHelp() {
            Dialog dialog = this.dialogRef.get();
            if (dialog != null) {
                HtmlTipDialog.showHelp(dialog.getContext());
                dialog.dismiss();
            }
        }

        @JavascriptInterface
        public void showSettings() {
            Dialog dialog = this.dialogRef.get();
            if (dialog != null) {
                HtmlTipDialog.showSettings(dialog.getContext());
                dialog.dismiss();
            }
        }

        @JavascriptInterface
        public void doNotShow(String tag) {
            Dialog dialog = this.dialogRef.get();
            if (dialog != null) {
                HtmlTipDialog.doNotShow(dialog.getContext(), tag);
                dialog.dismiss();
            }
        }
    }

    protected static void showSettings(Context context) {
        IMEApplication.from(context).showMainSettings();
    }

    protected static void showHelp(Context context) {
        IMEApplication.from(context).showTutorial();
    }

    protected static void doNotShow(Context context, String tag) {
        AppPreferences.from(context).enableShowEditGestureTip(false);
    }
}
